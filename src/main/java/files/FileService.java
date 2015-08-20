package files;

import spark.Request;

import javax.servlet.ServletException;
import javax.servlet.http.Part;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class FileService {


    public static void saveFilesToDb(Request request) throws IOException, ServletException, SQLException {
        List<Part> parts = getParts(request);
        if (parts.size() == 1) FileCrud.saveOneFile(parts.get(0));
        else {
            //parts.forEach(FileCrud::saveOneFile); can't do this if exceptions are unfixed
            for (Part file : parts) FileCrud.saveOneFile(file);
        }
    }

    public static void deleteFileFromDb(Request request) throws SQLException {
        Integer id = Integer.valueOf(request.params(":id"));
        FileCrud.deleteOneFile(id);
    }


    public static Object getPictures() throws SQLException {
        return FileCrud.getPictures();
    }


    public static Object getPicture(Request request) throws SQLException {
        Long id = Long.valueOf(request.params(":id"));
        return FileCrud.getPicture(id);
    }


    /**
     * It is magical, I cast to List, (it's ArrayList),
     * all objects are <Part> yet cannot cast at the spot
     */
    public static List<Part> getParts(Request request) throws ServletException, IOException {
        return (List) request.raw().getParts();
    }
}
