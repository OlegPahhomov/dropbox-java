package files.crud;

import config.AppDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import spark.Request;

import java.sql.Connection;
import java.sql.SQLException;


/**
 * CRUD*'s Reader only
 * Create, read, update, delete
 *
 * The idea is to minimise delegation.
 * Keep here only simple methods
 */
public class FileReader {

    static QueryRunner queryRunner = new QueryRunner();

    public static Object getPictures() throws SQLException {
        try (Connection connection = AppDataSource.getConnection()) {
            return queryRunner.query(connection, "SELECT *, IMAGE_WIDTH::float / IMAGE_HEIGHT AS RATIO FROM FILE", new MapListHandler());
        }
    }

    public static Object getPicture(Request request) throws SQLException {
        Long id = Long.valueOf(request.params(":id"));
        return getPicture(id);
    }

    public static Object getPicture(Long id) throws SQLException {
        try (Connection connection = AppDataSource.getConnection()) {
            return queryRunner.query(connection, "SELECT content FROM FILE WHERE ID=?", new ScalarHandler<>(), id);
        }
    }
}
