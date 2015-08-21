import com.google.gson.Gson;
import files.FileReader;
import files.validator.FileValidator;
import files.FileService;
import spark.*;

import javax.servlet.MultipartConfigElement;

import static spark.Spark.before;
import static spark.Spark.halt;
import static spark.Spark.post;
import static spark.SparkBase.staticFileLocation;

public class Application {

    public static final String ERROR_PAGE = "/error.html";
    static ResponseTransformer toJson = new Gson()::toJson;

    public static void main(String[] args) {
        staticFileLocation("webapp"); // it maps index.html to "/"

        before("/hidden", (request, response) -> halt(401, "You are not welcome to the secret page"));
        before("/hidden.html", (request, response) -> halt(401, "You are not welcome to the secret page"));

        post("/add", (request, response) -> {
            setRequestMultiPartFile(request);
            if (FileValidator.invalidInsert(request)) {
                response.redirect(ERROR_PAGE);
                halt();
            }
            FileService.saveFilesToDb(request);
            response.redirect("/");
            return "success";
        }, toJson);

        post("/remove/:id", (request, response) -> {
            if (FileValidator.invalidDelete(request)) {
                response.redirect(ERROR_PAGE);
                halt();
            }
            FileService.deleteFileFromDb(request);
            response.redirect("/");
            return "success";
        }, toJson);

        Spark.get("/files", (request, response) -> FileReader.getPictures(), toJson);

        Spark.get("/picture/:id", (request, response) -> {
            if (FileValidator.invalidGetById(request)){
                response.redirect(ERROR_PAGE);
                halt();
            }
            response.type("image/jpeg");
            return FileReader.getPicture(request);
        });

        Spark.after((request, response) -> {
            // For security reasons do not forget to change "*" to url
            response.header("Access-Control-Allow-Origin", "*");
            response.type("application/json");
        });

        Spark.after("/picture/:id", (request, response) -> response.type("image/jpeg"));

    }

    private static void setRequestMultiPartFile(Request request) {
        request.raw().setAttribute("org.eclipse.multipartConfig", new MultipartConfigElement("/tmp"));
    }

}
