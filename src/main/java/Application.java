import com.google.gson.Gson;
import com.google.gson.JsonParser;
import files.FileValidator;
import org.apache.commons.dbutils.QueryRunner;
import files.FileService;
import spark.*;

import javax.servlet.MultipartConfigElement;

import static spark.Spark.post;
import static spark.SparkBase.staticFileLocation;

public class Application {

    static ResponseTransformer toJson = new Gson()::toJson;

    public static void main(String[] args) {
        staticFileLocation("webapp"); // it maps index.html to "/"

        post("/add", (request, response) -> {
            setRequestMultiPartFile(request);
            if (FileValidator.invalidInsert(request)) {
                response.redirect("/error");
                return "error";
            }
            FileService.saveFilesToDb(request);
            response.redirect("/");
            return "success";
        }, toJson);

        post("/remove/:id", (request, response) -> {
            if (FileValidator.invalidDelete(request)) {
                response.redirect("/error");
                return "error";
            }
            FileService.deleteFileFromDb(request);
            response.redirect("/");
            return "success";
        }, toJson);

        Spark.get("/files", (request, response) -> {
            return FileService.getPictures();
        }, toJson);

        Spark.get("/picture/:id", (request, response) -> {
            response.type("image/jpeg");
            return FileService.getPicture(request);
        });

        Spark.after((request, response) -> {
            // For security reasons do not forget to change "*" to url
            response.header("Access-Control-Allow-Origin", "*");
            response.type("application/json");
        });

        Spark.after("/picture/:id", (request, response) -> {
            // For security reasons do not forget to change "*" to url
            response.type("image/jpeg");
        });

    }

    private static void setRequestMultiPartFile(Request request) {
        request.raw().setAttribute("org.eclipse.multipartConfig", new MultipartConfigElement("/tmp"));
    }

}
