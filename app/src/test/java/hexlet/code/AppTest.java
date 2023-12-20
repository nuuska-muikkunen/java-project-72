package hexlet.code;

//import static hexlet.code.util.Data.readResourceFile;
import static org.assertj.core.api.Assertions.assertThat;

//import com.zaxxer.hikari.HikariConfig;
//import com.zaxxer.hikari.HikariDataSource;
//import hexlet.code.repository.BaseRepository;
//import io.javalin.rendering.template.JavalinJte;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import io.javalin.Javalin;

import java.io.IOException;
import java.sql.SQLException;

public class AppTest {
    private static Javalin app;
    private static String baseUrl;

    @BeforeAll
    public static void beforeAll() throws IOException, SQLException {
        app = App.getApp();
        var portNumber = System.getenv("PORT") == null ? 7070 : Integer.parseInt(System.getenv("PORT"));
        app.start(portNumber);
        int port = app.port();
        baseUrl = "http://localhost:" + port;
//        var hikariConfig = new HikariConfig();
//
//        var dataString = System.getenv("JDBC_DATABASE_URL") == null
//                ? "jdbc:h2:mem:project" : System.getenv("JDBC_DATABASE_URL");
//
//        hikariConfig.setJdbcUrl(dataString + ";DB_CLOSE_DELAY=-1;");
//
//        var dataSource = new HikariDataSource(hikariConfig);
//        var sql = readResourceFile("schema.sql");
//
//        try (var connection = dataSource.getConnection();
//             var statement = connection.createStatement()) {
//            statement.execute(sql);
//        }
//        BaseRepository.dataSource = dataSource;
//        var app = Javalin.create(config -> {
//            config.plugins.enableDevLogging();
//        });
    }

    @AfterAll
    public static void afterAll() {
        app.stop();
    }

    @Test
    void testRoot() throws Exception {
        HttpResponse<String> response = Unirest.get(baseUrl + "/").asString();
        String actual = response.getBody();
        String expected = "Пример";
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(actual).contains(expected);
    }

    @Test
    void testEnvironment() throws Exception {
        HttpResponse<String> response = Unirest.get(baseUrl + "/").asString();
        String actual = String.valueOf(app.port());
        String expected = "7070";
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testIndex() throws Exception {
        HttpResponse<String> response = Unirest.get(baseUrl + "/urls").asString();
        String actual = response.getBody();
        String expected = "Сайты";
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(actual).contains(expected);
    }

//    @Test
//    void testListArticles1() throws Exception {
//        HttpResponse<String> response = Unirest.get(baseUrl + "/urls").asString();
//        String body = response.getBody();
//
//        assertThat(response.getStatus()).isEqualTo(200);
//        assertThat(body).contains("http://www.rbc.ru");
//        assertThat(body).contains("http://www.rbc.ru:7070");
//        assertThat(body).contains("Последняя проверка");
//        assertThat(body).doesNotContain("\thttp://www.ethtrgh");
//        assertThat(body).contains("?page=2");
//    }

}
