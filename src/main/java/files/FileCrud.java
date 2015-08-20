package files;

import config.AppDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import javax.imageio.ImageIO;
import javax.servlet.http.Part;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FileCrud {

    static QueryRunner queryRunner = new QueryRunner();

    public static Object getPictures() throws SQLException {
        try (Connection connection = AppDataSource.getConnection()) {
            return queryRunner.query(connection, "SELECT *, IMAGE_WIDTH::float / IMAGE_HEIGHT AS RATIO FROM FILE", new MapListHandler());
        }
    }

    public static Object getPicture(Long id) throws SQLException {
        try (Connection connection = AppDataSource.getConnection()) {
            return queryRunner.query(connection, "SELECT content FROM FILE WHERE ID=?", new ScalarHandler<>(), id);
        }
    }

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

    private static void fillInsertPS(PreparedStatement ps, Part file) throws IOException, SQLException {
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
