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

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import io.javalin.rendering.template.JavalinJte;
import gg.jte.resolve.ResourceCodeResolver;

public class App {

    private static TemplateEngine createTemplateEngine() {
        ClassLoader classLoader = App.class.getClassLoader();
        ResourceCodeResolver codeResolver = new ResourceCodeResolver("templates", classLoader);
        return TemplateEngine.create(codeResolver, ContentType.Html);
    }

    public static Javalin getApp() throws IOException, SQLException {

        var hikariConfig = new HikariConfig();

        var dataString = System.getenv("JDBC_DATABASE_URL") == null
            ? "jdbc:h2:mem:project" : System.getenv("JDBC_DATABASE_URL");

        hikariConfig.setJdbcUrl(dataString + ";DB_CLOSE_DELAY=-1;");

        var dataSource = new HikariDataSource(hikariConfig);
        var sql = readResourceFile("schema.sql");

        JavalinJte.init(createTemplateEngine());

        try (var connection = dataSource.getConnection();
             var statement = connection.createStatement()) {
            statement.execute(sql);
        }
        BaseRepository.dataSource = dataSource;
        var app = Javalin.create(config -> {
            config.plugins.enableDevLogging();
        });

        app.get("/", ctx -> {
            ctx.render("mainPage.jte");
        });

        app.post(NamedRoutes.urlsPath(), UrlsController::create);
        app.get(NamedRoutes.urlsPath(), UrlsController::index);
        app.post(NamedRoutes.buildUrlPath(), UrlsController::build);
        app.get(NamedRoutes.urlPath("{id}"), UrlsController::show);
        return app;
    }
    public static void main(String[] args) throws SQLException, IOException {
        Javalin app = getApp();
        var portNumber = System.getenv("PORT") == null ? 7070 : Integer.parseInt(System.getenv("PORT"));
        app.start(portNumber);
    }
}
