package db.migration;

import org.apache.commons.dbutils.QueryRunner;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;

import java.sql.Connection;

public class V1__Add_files_table implements JdbcMigration {
    public void migrate(Connection connection) throws Exception {
        new QueryRunner().update(connection,
                "CREATE TABLE file (\n" +
                        "  id      BIGSERIAL PRIMARY KEY                  NOT NULL,\n" +
                        "  name    TEXT CHECK (NAME <> '')                NOT NULL,\n" +
                        "  content BYTEA                                  NOT NULL,\n" +
                        "  image_width INTEGER                            NOT NULL,\n" +
                        "  image_height INTEGER                           NOT NULL\n" +
                        ");"
        );
    }
}