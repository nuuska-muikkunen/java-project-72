package hexlet.code;

import static org.assertj.core.api.Assertions.assertThat;

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

}
