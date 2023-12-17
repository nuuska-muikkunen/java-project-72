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
        app.start(0);
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
        String expected = "Hello, World!";
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(actual).isEqualTo(expected);
    }
    void testEnvironment() throws Exception {
        HttpResponse<String> response = Unirest.get(baseUrl + "/").asString();
        String actual = String.valueOf(app.port());
        String expected = "8080";
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(actual).isEqualTo(expected);
    }

}
