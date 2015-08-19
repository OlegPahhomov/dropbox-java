package db.migration;

import org.flywaydb.core.api.migration.jdbc.JdbcMigration;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
        FileInputStream fileInputStream = new FileInputStream(file);
        BufferedImage img = ImageIO.read(file);
        ps.setString(1, file.getName());
        ps.setBinaryStream(2, fileInputStream, (int) file.length());
        ps.setInt(3, img.getWidth());
        ps.setInt(4, img.getHeight());
        ps.executeUpdate();
    }

}
