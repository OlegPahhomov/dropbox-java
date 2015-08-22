package files.controller;

import files.crud.FileReader;
import files.service.FileService;
import files.validator.FileValidator;
import spark.Request;
import spark.Response;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import java.io.IOException;
import java.sql.SQLException;

import static spark.Spark.halt;

public class FileController {
    public static final String ERROR_PAGE = "../html/error.html";

    public static String addFile(Request request, Response response) throws IOException, ServletException, SQLException {
        setRequestMultiPartFile(request);
        if (FileValidator.invalidInsert(request)) {
            response.redirect(ERROR_PAGE);
            halt();
        }
        FileService.saveFilesToDb(request);
        return "success";
    }

    public static String deleteFile(Request request, Response response) throws SQLException {
        if (FileValidator.invalidDelete(request)) {
            response.redirect(ERROR_PAGE);
            halt();
        }
        FileService.deleteFileFromDb(request);
        return "success";
    }

    public static Object getPicture(Request request, Response response) throws SQLException {
        if (FileValidator.invalidGetById(request)){
            response.redirect(ERROR_PAGE);
            halt();
        }
        response.type("image/jpeg");
        return FileReader.getPicture(request);
    }

    /**
     * keep request, response input, so it's substitutable with lambda in Application.java
     */
    public static Object getPictures(Request request, Response response) throws SQLException {
        return FileReader.getPictures();
    }

    private static void setRequestMultiPartFile(Request request) {
        request.raw().setAttribute("org.eclipse.multipartConfig", new MultipartConfigElement("/tmp"));
    }
}
