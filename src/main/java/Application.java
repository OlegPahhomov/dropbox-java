import com.google.gson.Gson;
import com.google.gson.JsonParser;
import config.AppDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import spark.ResponseTransformer;
import spark.Spark;

import java.sql.Connection;

import static spark.SparkBase.staticFileLocation;

public class Application {

    static ResponseTransformer toJson = new Gson()::toJson;
    static QueryRunner queryRunner = new QueryRunner();
    static JsonParser jsonParser = new JsonParser();

    public static void main(String[] args) {
        staticFileLocation("webapp");

        //Spark.get("/", (request, response) -> "Greetings!", toJson);

        Spark.get("/files", (request, response) -> {
            try (Connection connection = AppDataSource.getConnection()) {
                return queryRunner.query(connection, "SELECT * FROM FILE", new MapListHandler());
            }
        }, toJson);

        Spark.after((request, response) -> {
            // For security reasons do not forget to change "*" to url
            response.header("Access-Control-Allow-Origin", "*");
            response.type("application/json");
        });
    }
}
