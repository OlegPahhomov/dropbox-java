package files.crud;

import config.AppDataSource;
import files.util.FileResizer;

import javax.imageio.ImageIO;
import javax.servlet.http.Part;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * CRUD* without R (Read)
 * Create, read, update, delete
 */
public class FileCrud {


    public static void saveOneFile(Part file) throws SQLException, IOException {
        try (Connection connection = AppDataSource.getTransactConnection();
             PreparedStatement ps = connection.prepareStatement("INSERT INTO FILE(name, content, image_width, image_height) VALUES (?, ?, ?, ?)")) {
            fillInsertPS(ps, file);
            connection.commit();
        }
    }

    public static void deleteOneFile(Integer id) throws SQLException {
        try (Connection connection = AppDataSource.getTransactConnection();
             PreparedStatement ps = connection.prepareStatement("DELETE FROM FILE WHERE ID=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
            connection.commit();
        }
    }

    private static void fillInsertPS(PreparedStatement ps, Part part) throws IOException, SQLException {
        BufferedImage img = ImageIO.read(part.getInputStream());
        String fileName = getFileName(part);

        if (FileResizer.needsResize(img)) {
            BufferedImage resizedImage = FileResizer.dynamicResize(img);
            ByteArrayOutputStream os = FileResizer.getByteArrayOutputStream(resizedImage);
            fillPsAndExecute(ps, fileName, resizedImage, new ByteArrayInputStream(os.toByteArray()), os.size());
        } else {
            fillPsAndExecute(ps, fileName, img, part.getInputStream(), (int) part.getSize());
        }
    }

    private static void fillPsAndExecute(PreparedStatement ps, String fileName, BufferedImage img, InputStream inputStream, int size) throws SQLException {
        ps.setString(1, fileName);
        ps.setBinaryStream(2, inputStream, size);
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
