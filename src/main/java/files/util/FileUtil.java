package files.util;

import spark.Request;

import javax.servlet.ServletException;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.List;

public class FileUtil {

    /**
     * It is magical, I cast to List, (it's ArrayList),
     * all objects are <Part> yet cannot cast at the spot
     */
    public static List<Part> getParts(Request request) throws ServletException, IOException {
        return (List) request.raw().getParts();
    }
}
