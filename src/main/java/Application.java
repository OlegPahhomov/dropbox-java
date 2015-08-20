import com.google.gson.Gson;
import com.google.gson.JsonParser;
import config.AppDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.eclipse.jetty.util.MultiPartInputStreamParser;
import spark.*;
import spark.utils.IOUtils;

import javax.imageio.ImageIO;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.Part;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
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
            setRequestMultiPartFile(request);

            saveFilesToDb(request);
            response.redirect("/");
            return "success";
        }, toJson);

        Spark.post("/remove/:id", (request, response) -> {
            try (Connection connection = AppDataSource.getTransactConnection();
                 PreparedStatement ps = connection.prepareStatement("DELETE FROM FILE WHERE ID=?")) {
                Integer id = Integer.valueOf(request.params(":id"));
                ps.setInt(1, id);
                ps.executeUpdate();
                connection.commit();
            }
            response.redirect("/");
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

    private static void saveFilesToDb(Request request) throws IOException, ServletException, SQLException {
        List<Part> parts = getParts(request);
        if (parts.size() == 1) {
            saveOneFile(parts.get(0));
        } else {
            for (Part file : parts) saveOneFile(file);
        }
    }

    /**
     * It is magical, I cast to List, (it's ArrayList),
     * all objects are <Part> yet cannot cast at the spot
     */
    private static List<Part> getParts(Request request) throws IOException, ServletException {
        return (List) request.raw().getParts();
    }

    private static void saveOneFile(Part file) throws IOException, ServletException, SQLException {
        try (Connection connection = AppDataSource.getTransactConnection();
             PreparedStatement ps = connection.prepareStatement("INSERT INTO FILE(name, content, image_width, image_height) VALUES (?, ?, ?, ?)")) {
            fillPreparedStatement(ps, file);
            connection.commit();
        }
    }

    private static void setRequestMultiPartFile(Request request) {
        request.raw().setAttribute("org.eclipse.multipartConfig", new MultipartConfigElement("/tmp"));
    }

    private static void fillPreparedStatement(PreparedStatement ps, Part file) throws IOException, SQLException {
        ps.setString(1, getFileName(file));
        ps.setBinaryStream(2, file.getInputStream(), (int) file.getSize());
        BufferedImage img = ImageIO.read(file.getInputStream());
        ps.setInt(3, img.getWidth());
        ps.setInt(4, img.getHeight());
        ps.executeUpdate();
    }

    /**
     * hack from the web
     */
    static private String getFileName(Part part) {
        for (String cd : part.getHeader("content-disposition").split(";")) {
            if (cd.trim().startsWith("filename")) {
                return cd.substring(cd.indexOf('=') + 1).trim()
                        .replace("\"", "");
            }
        }
        return "unknown";
    }
}
