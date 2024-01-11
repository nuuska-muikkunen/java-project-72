package hexlet.code;

import static hexlet.code.util.Data.fixture;
import static hexlet.code.util.Data.getContentOfHtmlFile;
import static org.assertj.core.api.Assertions.assertThat;

import hexlet.code.model.Url;
import hexlet.code.repository.UrlsRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.testtools.JavalinTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import io.javalin.Javalin;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.zip.DataFormatException;

public class AppTest {
    private static Javalin app;
    private static String baseUrl;
    private static MockWebServer mockServer;
    private static String urlName;

    @BeforeAll
    public static void beforeAll() throws IOException, SQLException, DataFormatException {
        app = App.getApp();
        app.start(7070);
        int port = app.port();
        System.out.println("port after app start in BeforeAll= " + port);
        baseUrl = "http://localhost:" + port;
        var url = new Url("http://www.rbc.ru", Timestamp.valueOf(LocalDateTime.now()));
        UrlsRepository.save(url);

        mockServer = new MockWebServer();
        urlName = mockServer.url("/").toString();
        var mockResponse = new MockResponse().setBody(getContentOfHtmlFile(fixture("http.json")));
        mockServer.enqueue(mockResponse);
        if (!mockServer.getHostName().isEmpty()) {
            System.out.println("mockServer Host= " + mockServer.getHostName());
            System.out.println("mockServer Port= " + mockServer.getPort());
        }
    }

    @AfterAll
    public static void afterAll() throws IOException {
        app.stop();
        mockServer.shutdown();
    }

    @Test
    void testRoot() throws Exception {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.rootPath());
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("Page analyzer");
        });
//        HttpResponse<String> response = Unirest.get(baseUrl + "/").asString();
//        String actual = response.getBody();
//        String expected = "Пример";
//        assertThat(response.getStatus()).isEqualTo(200);
//        assertThat(actual).contains(expected);
    }

    @Test
    void testEnvironment() throws Exception {
        System.out.println("app.port() in testEnvironment = " + app.port());
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
    void testRegisterNewSite1() throws Exception {
        var responsePost = Unirest
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

    @Test
    void testShowUrl() throws Exception {
        var responseGet = Unirest
                .get(baseUrl + "/urls/1")
                .asEmpty();

        assertThat(responseGet.getStatus()).isEqualTo(200);

        HttpResponse<String> response = Unirest
                .get(baseUrl + "/urls/1")
                .asString();
        String body = response.getBody();
        assertThat(body).contains("http://www.rbc.ru");
        assertThat(body).contains("Запустить проверку");
    }

    @Test
    void testCheckUrl() throws Exception {
        var responsePost = Unirest
                .post(baseUrl + "/urls/1/checks")
                .asEmpty();

        assertThat(responsePost.getStatus()).isEqualTo(302);

        HttpResponse<String> response = Unirest
                .get(baseUrl + "/urls/1")
                .asString();
        String body = response.getBody();
        assertThat(body).contains("Сайт:");
        assertThat(body).contains("на сайте rbc.ru");
        assertThat(body).contains("Код ответа");
    }

}
