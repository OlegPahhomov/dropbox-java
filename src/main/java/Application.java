import com.google.gson.Gson;
import files.controller.FileController;
import spark.*;

import static spark.Spark.*;
import static spark.SparkBase.staticFileLocation;

public class Application {

    static ResponseTransformer toJson = new Gson()::toJson;

    public static void main(String[] args) {
        staticFileLocation("webapp"); // it maps index.html to "/"

        before("/hidden", (request, response) -> halt(401, "You are not welcome to the secret page"));
        before("/hidden.html", (request, response) -> halt(401, "You are not welcome to the secret page"));

        post("/add", FileController::AddFile);
        post("/remove/:id", FileController::deleteFile);

        get("/files", FileController::getPictures, toJson);
        get("/picture/:id", FileController::getPicture);

        after((request, response) -> {
            // For security reasons do not forget to change "*" to url
            response.header("Access-Control-Allow-Origin", "*");
            response.type("application/json");
        });

        after("/picture/:id", (request, response) -> response.type("image/jpeg"));

    }


}
