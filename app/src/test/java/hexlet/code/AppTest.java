package hexlet.code;

//import static hexlet.code.App.createTemplateEngine;
//import static hexlet.code.util.Data.readResourceFile;
import static org.assertj.core.api.Assertions.assertThat;

//import com.zaxxer.hikari.HikariConfig;
//import com.zaxxer.hikari.HikariDataSource;
import hexlet.code.model.Url;
//import hexlet.code.repository.BaseRepository;
import hexlet.code.repository.UrlsRepository;
//import io.javalin.rendering.template.JavalinJte;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import io.javalin.Javalin;

import java.io.IOException;
//import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

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

        var url = new Url("http://www.rbc.ru", Timestamp.valueOf(LocalDateTime.now()));
        UrlsRepository.save(url);
        url = new Url("http://www.rbc.ru:8080", Timestamp.valueOf(LocalDateTime.now()));
        UrlsRepository.save(url);
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
        assertThat(actual).contains("http://www.rbc.ru");
        assertThat(actual).doesNotContain("http://www.mail.ru");
    }

    @Test
    void testRegisterNewUser1() throws Exception {
        HttpResponse responsePost = Unirest
                .post(baseUrl + "/urls")
                .field("url", "http://www.vk.ru")
                .asEmpty();

        assertThat(responsePost.getStatus()).isEqualTo(302);
        assertThat(responsePost.getHeaders().getFirst("Location")).isEqualTo("/urls");

        HttpResponse<String> response = Unirest
                .get(baseUrl + "/urls")
                .asString();
        String body = response.getBody();
        assertThat(body).contains("http://www.rbc.ru");
        assertThat(body).contains("http://www.vk.ru");

        var urlrbc = UrlsRepository.search("http://www.rbc.ru").get();
        var urlvk = UrlsRepository.search("http://www.vk.ru").get();
        assertThat(urlrbc.getName()).isEqualTo("http://www.rbc.ru");
        assertThat(urlvk.getName()).isEqualTo("http://www.vk.ru");
    }

}
