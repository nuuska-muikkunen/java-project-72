package hexlet.code;

import hexlet.code.controller.UrlsController;
import io.javalin.Javalin;
import java.io.IOException;
import java.sql.SQLException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import hexlet.code.repository.BaseRepository;
import hexlet.code.util.NamedRoutes;
import static hexlet.code.util.Data.readResourceFile;

public class App {
    public static Javalin getApp() throws IOException, SQLException {

        var hikariConfig = new HikariConfig();
//        var database = JDBC_DATABASE_URL.substring(0, JDBC_DATABASE_URL.indexOf('?'));
        hikariConfig.setJdbcUrl("jdbc:h2:mem:hexlet_project;DB_CLOSE_DELAY=-1;");

        var dataSource = new HikariDataSource(hikariConfig);
        var sql = readResourceFile("schema.sql");
        try (var connection = dataSource.getConnection();
             var statement = connection.createStatement()) {
            statement.execute(sql);
        }
        BaseRepository.dataSource = dataSource;
        var app = Javalin.create(config -> {
            config.plugins.enableDevLogging();
            config.staticFiles.add(staticFileConfig -> {
                staticFileConfig.directory = "/css";
            });
        });
        app.get(NamedRoutes.urlsPath(), UrlsController::index);
        app.get(NamedRoutes.buildUrlPath(), UrlsController::build);
        app.get(NamedRoutes.urlPath("{id}"), UrlsController::show);
        app.post(NamedRoutes.urlsPath(), UrlsController::create);
        return app;
    }
    public static void main(String[] args) throws SQLException, IOException {
        Javalin app = getApp();
        app.start(7070);
    }
}
