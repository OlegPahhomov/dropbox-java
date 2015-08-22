package db.migration;

import files.util.FileResizer;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.*;

public class V2__Insert_files implements JdbcMigration {

    public static final String CUTE_KITTENS = "src/main/resources/db/pics/Cute-Kittens.jpg";
    public static final String TERMINATOR = "src/main/resources/db/pics/Terminator.jpeg";
    public static final String HOME = "src/main/resources/db/pics/Home.jpeg";
    public static final String DRAKENSANG = "src/main/resources/db/pics/Drakensang.jpg";


    @Override
    public void migrate(Connection connection) throws Exception {
        try (PreparedStatement ps = connection.prepareStatement("INSERT INTO FILE(name, content, image_width, image_height) VALUES (?, ?, ?, ?)")) {
            insertToDb(ps, CUTE_KITTENS);
            insertToDb(ps, TERMINATOR);
            insertToDb(ps, DRAKENSANG);
            insertToDb(ps, HOME);
        }
    }

    private void insertToDb(PreparedStatement ps, String filename) throws IOException, SQLException {
        File file = new File(filename);
        BufferedImage img = ImageIO.read(file);
        if (FileResizer.needsResize(img)) {
            BufferedImage resizedImage = FileResizer.dynamicResize(img);
            ByteArrayOutputStream os = FileResizer.getByteArrayOutputStream(resizedImage);
            fillPsAndExecute(ps, file.getName(), resizedImage, new ByteArrayInputStream(os.toByteArray()), os.size());
        } else {
            fillPsAndExecute(ps, file.getName(), img, new FileInputStream(file), (int) file.length());
        }
    }

    private void fillPsAndExecute(PreparedStatement ps, String fileName, BufferedImage img, InputStream inputStream, int size) throws SQLException {
        ps.setString(1, fileName);
        ps.setBinaryStream(2, inputStream, size);
        ps.setInt(3, img.getWidth());
        ps.setInt(4, img.getHeight());
        ps.executeUpdate();
    }
}