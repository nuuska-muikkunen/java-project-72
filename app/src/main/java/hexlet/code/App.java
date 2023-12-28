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
import static hexlet.code.util.Data.readResourceFile;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import io.javalin.rendering.template.JavalinJte;
import gg.jte.resolve.ResourceCodeResolver;

public class App {

    protected static TemplateEngine createTemplateEngine() {
        ClassLoader classLoader = App.class.getClassLoader();
        ResourceCodeResolver codeResolver = new ResourceCodeResolver("templates", classLoader);
        return TemplateEngine.create(codeResolver, ContentType.Html);
    }

    public static Javalin getApp() throws IOException, SQLException {

        var hikariConfig = new HikariConfig();
        var dataString = System.getenv("JDBC_DATABASE_URL");
        System.out.println("JDBC_DATABASE_URL= " + dataString);
        if (dataString == null || dataString.equals("jdbc:h2:mem:hexlet_project")) {
            // jdbc:h2:mem:hexlet_project
            hikariConfig.setJdbcUrl("jdbc:h2:mem:hexlet_project;DB_CLOSE_DELAY=-1;");
        } else {
            hikariConfig.setUsername(System.getenv("JDBС_DATABASE_USERNAME"));
            hikariConfig.setPassword(System.getenv("JDBC_DATABASE_PASSWORD"));
//            hikariConfig.setPassword("Monday.55");
            // jdbc:postgresql://dpg-clsmorlcm5oc73b8f840-a/hexlet_learning_javalin
            // jdbc:postgresql://localhost:5432/hexlet user=postgres password=xxxxxxx sslmode=prefer connect_timeout=10
            hikariConfig.setJdbcUrl(dataString);
        }
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

        app.get(NamedRoutes.rootPath(), UrlsController::root);
        app.get(NamedRoutes.urlsPath(), UrlsController::index);
        app.post(NamedRoutes.urlsPath(), UrlsController::create);
        app.get(NamedRoutes.urlPath("{id}"), UrlsController::show);
        app.post(NamedRoutes.checkPath("{id}"), UrlChecksController::createCheck);
        return app;
    }
    public static void main(String[] args) throws SQLException, IOException {
        Javalin app = getApp();
        var portNumber = System.getenv("PORT") == null ? 7070 : Integer.parseInt(System.getenv("PORT"));
        app.start(portNumber);
    }
}
