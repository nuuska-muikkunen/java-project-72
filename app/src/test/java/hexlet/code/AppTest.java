package hexlet.code;

import hexlet.code.model.Url;
import hexlet.code.repository.UrlsRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.StringJoiner;

import static org.assertj.core.api.Assertions.assertThat;

public class AppTest {
    private static Javalin app;
    private static MockWebServer mockServer;
    private static String baseUrl;
    private static final String HTML_PATH = "src/test/resources/index.html";

    public static String getContentOfHtmlFile() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(HTML_PATH));
        String lineOfFile = reader.readLine();
        var result = new StringJoiner("\n");

        while (lineOfFile != null) {
            result.add(lineOfFile);
            lineOfFile = reader.readLine();
        }
        return result.toString();
    }

    @BeforeAll
    public static void beforeAll() throws IOException, SQLException {
        mockServer = new MockWebServer();
        baseUrl = mockServer.url("/").toString();
        var mockResponse = new MockResponse().setBody(getContentOfHtmlFile());
        mockServer.enqueue(mockResponse);
    }

    @BeforeEach
    public void beforeEach() throws SQLException, IOException {
        app = App.getApp();
    }

    @AfterEach
    public void afterEach() {
        app.stop();
    }


    @AfterAll
    public static void afterAll() throws IOException {
        mockServer.shutdown();
    }

    @Test
    void testRoot() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(baseUrl);
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().toString()).contains("Анализатор страниц");
        });
    }

    @Test
    void testEnvironment() throws Exception {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.rootPath());
            assertThat(response.code()).isEqualTo(200);
            assertThat(server.port()).isEqualTo(7070);
        });
    }

    @Test
    void testIndex() throws Exception {
        JavalinTest.test(app, (server, client) -> {
            var url = new Url("http://www.rbc.ru", Timestamp.valueOf(LocalDateTime.now()));
            UrlsRepository.save(url);
            var response = client.get(NamedRoutes.urlsPath());
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().toString()).contains("Сайты");
            assertThat(response.body().toString()).contains("http://www.rbc.ru");
            assertThat(response.body().toString()).doesNotContain("http://www.mail.ru");
        });
    }

    @Test
    void testRegisterNewSite1() throws Exception {
        JavalinTest.test(app, (server, client) -> {
            var responsePost = client.post(NamedRoutes.urlsPath(), "http://www.rbc.ru");
            assertThat(responsePost.code()).isEqualTo(302);
            assertThat(responsePost.header("Location")).isEqualTo("/urls");

            var responseGet = client.get(NamedRoutes.urlsPath());
            var body = responseGet.body().toString();
            assertThat(body).contains("http://www.rbc.ru");

            var urlrbc = UrlsRepository.search("http://www.rbc.ru").get();
            assertThat(urlrbc.getName()).isEqualTo("http://www.rbc.ru");
        });
    }

    @Test
    void testShowUrl() throws Exception {
        JavalinTest.test(app, (server, client) -> {
            var responseGet = client.get(NamedRoutes.urlPath("1"));
            assertThat(responseGet.code()).isEqualTo(302);
            var body = responseGet.body().toString();
            assertThat(body).contains("http://www.rbc.ru");
            assertThat(body).contains("Запустить проверку");
        });
    }

    @Test
    void testCheckUrl() throws Exception {
        JavalinTest.test(app, (server, client) -> {
            var url = new Url("http://www.rbc.ru", Timestamp.valueOf(LocalDateTime.now()));
            UrlsRepository.save(url);
            var responsePost = client.post(NamedRoutes.checkPath("1"),
                    "{http://www.rbc.ru}");
            assertThat(responsePost.code()).isEqualTo(302);

            var responseGet = client.get(NamedRoutes.checkPath("1"));
            var body = responseGet.body().toString();
            assertThat(body).contains("Сайт:");
            assertThat(body).contains("на сайте rbc.ru");
            assertThat(body).contains("Код ответа");
        });
    }

}
