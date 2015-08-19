import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import config.AppDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import spark.*;

import javax.imageio.ImageIO;
import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static spark.SparkBase.staticFileLocation;

public class Application {

    static ResponseTransformer toJson = new Gson()::toJson;
    static QueryRunner queryRunner = new QueryRunner();
    static JsonParser jsonParser = new JsonParser();

    public static void main(String[] args) {
        staticFileLocation("webapp");

        //Spark.get("/", (request, response) -> "Greetings!", toJson);

        Spark.post("/add", (request, response) -> {
            MultipartConfigElement multipartConfigElement = new MultipartConfigElement("/tmp");
            request.raw().setAttribute("org.eclipse.multipartConfig", multipartConfigElement);
            Part file = request.raw().getPart("file"); //file is name of the upload form
            BufferedImage img = ImageIO.read(file.getInputStream());
            try (Connection connection = AppDataSource.getTransactConnection();
                 PreparedStatement ps = connection.prepareStatement("INSERT INTO FILE(name, content, image_width, image_height) VALUES (?, ?, ?, ?)")) {
                ps.setString(1, file.getName());
                ps.setBinaryStream(2, file.getInputStream(), (int) file.getSize());
                ps.setInt(3, img.getWidth());
                ps.setInt(4, img.getHeight());
                ps.executeUpdate();
                connection.commit();
            }

            return "success";
        }, toJson);

        Spark.get("/files", (request, response) -> {
            try (Connection connection = AppDataSource.getConnection()) {
                return queryRunner.query(connection, "SELECT *, IMAGE_WIDTH::float / IMAGE_HEIGHT AS RATIO FROM FILE", new MapListHandler());
            }
        }, toJson);

        Spark.get("/picture/:id", (request, response) -> {
            try (Connection connection = AppDataSource.getConnection()) {
                response.type("image/jpeg");
                Long id = Long.valueOf(request.params(":id"));
                return queryRunner.query(connection, "SELECT content FROM FILE WHERE ID=?", new ScalarHandler<>(), id);
            }
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
}
