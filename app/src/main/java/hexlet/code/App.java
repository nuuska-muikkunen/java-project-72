package hexlet.code;

import hexlet.code.controller.UrlChecksController;
import hexlet.code.controller.UrlsController;
import io.javalin.Javalin;
import java.io.IOException;
import java.sql.SQLException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import hexlet.code.repository.BaseRepository;
import hexlet.code.util.NamedRoutes;
import static hexlet.code.util.Utils.readResourceFile;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import io.javalin.rendering.template.JavalinJte;
import gg.jte.resolve.ResourceCodeResolver;

public class App {

    private static int getPort() {
        return Integer.parseInt(System.getenv().getOrDefault("PORT", "7070"));
    }

    protected static TemplateEngine createTemplateEngine() {
        ClassLoader classLoader = App.class.getClassLoader();
        ResourceCodeResolver codeResolver = new ResourceCodeResolver("templates", classLoader);
        return TemplateEngine.create(codeResolver, ContentType.Html);
    }

    private static boolean isProduction() {
        var dataString = System.getenv("JDBC_DATABASE_URL");
        return dataString != null;
    }

    private static HikariDataSource configureDatasource() {
        var hikariConfig = new HikariConfig();
        if (isProduction()) {
            hikariConfig.setUsername(System.getenv("JDBC_DATABASE_USERNAME"));
            hikariConfig.setPassword(System.getenv("JDBC_DATABASE_PASSWORD"));
            hikariConfig.setJdbcUrl(System.getenv("JDBC_DATABASE_URL"));
        } else {
            hikariConfig.setJdbcUrl("jdbc:h2:mem:hexlet_project;DB_CLOSE_DELAY=-1;");
        }
        return new HikariDataSource(hikariConfig);
    }

    private static void initializeDB(HikariDataSource dataSource) throws IOException, SQLException {

        var sql = readResourceFile("schema.sql");

        JavalinJte.init(createTemplateEngine());

        try (var connection = dataSource.getConnection();
             var statement = connection.createStatement()) {
            statement.execute(sql);
        }

        BaseRepository.dataSource = dataSource;
    }

    private static void registerPaths(Javalin app) {
        app.get(NamedRoutes.rootPath(), UrlsController::root);
        app.get(NamedRoutes.urlsPath(), UrlsController::index);
        app.get(NamedRoutes.urlPath("{id}"), UrlsController::show);
        app.post(NamedRoutes.urlsPath(), UrlsController::create);
        app.post(NamedRoutes.checkPath("{id}"), UrlChecksController::createCheck);
    }
    public static Javalin getApp() throws IOException, SQLException {

        var dataSource = configureDatasource();

        initializeDB(dataSource);

        var app = Javalin.create(config -> config.plugins.enableDevLogging());

        registerPaths(app);

        return app;
    }

    public static void main(String[] args) throws SQLException, IOException {
        Javalin app = getApp();
        app.start(getPort());
    }

}
