package org.flywaydb.maven;

import org.jooq.util.GenerationTool;
import org.jooq.util.jaxb.Configuration;
import org.jooq.util.jaxb.Database;
import org.jooq.util.jaxb.Generator;
import org.jooq.util.jaxb.Target;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public final class JooqSchemaGenerator {
    static void generate(
            final Connection connection,
            final String includesRegex,
            final String excludesRegex,
            final String packageName,
            final String directory) throws Exception {
        GenerationTool generator = new GenerationTool();
        generator.setConnection(connection);
        Configuration configuration = new Configuration()
                .withGenerator(
                        new Generator()
                                .withName("org.jooq.util.JavaGenerator")
                                .withDatabase(
                                        new Database()
                                                .withName(getJooqDatabaseClassName(connection))
                                                .withIncludes(includesRegex == null ? ".*" : includesRegex)
                                                .withExcludes(excludesRegex == null ? "" : excludesRegex)
                                                .withInputSchema("public"))
                                .withTarget(
                                        new Target()
                                                .withPackageName(packageName)
                                                .withDirectory(directory)));

        generator.run(configuration);
    }

    private static String getJooqDatabaseClassName(Connection connection) throws SQLException {
        // TODO: Improve how this is done
        // TODO: Add other databases
        return dbNameToJooqClassName.get(connection.getMetaData().getDatabaseProductName().toLowerCase());
    }

    private static Map<String, String> dbNameToJooqClassName = new HashMap<String, String>();
    static {
        dbNameToJooqClassName.put("PostgreSQL".toLowerCase(), "org.jooq.util.postgres.PostgresDatabase");
        dbNameToJooqClassName.put("MySQL".toLowerCase(), "org.jooq.util.mysq.MySQLDatabase");
    }
}
