package files;

import spark.Request;

import javax.imageio.ImageIO;
import javax.servlet.http.Part;
import java.awt.image.BufferedImage;
import java.util.List;

public class FileValidator {

    public static boolean invalidInsert(Request request) {
        return !validInsert(request);
    }

    public static boolean invalidDelete(Request request) {
        return !validDelete(request);
    }

    /**
     * If we can get an image for each part and it's not a null (.getXxx succeeds)
     */
    private static boolean validInsert(Request request) {
        try {
            List<Part> parts = FileService.getParts(request);
            for (Part part : parts) {
                BufferedImage img = ImageIO.read(part.getInputStream());
                img.getWidth();
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * If we can get id out, and it's a number, it is correct
     */
    private static boolean validDelete(Request request) {
        try {
            Integer id = Integer.valueOf(request.params(":id"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
