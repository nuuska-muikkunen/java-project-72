package hexlet.code;

import hexlet.code.model.Url;
import hexlet.code.repository.UrlsRepository;
import hexlet.code.util.Data;
import hexlet.code.util.NamedRoutes;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import static hexlet.code.util.Data.readResourceFile;
import static org.assertj.core.api.Assertions.assertThat;

public class AppTest {
    private static Javalin app;
    private static String baseUrl;
    private static MockWebServer mockServer;
    private static int port;

    @BeforeAll
    public static void beforeAll() throws IOException {
        mockServer = new MockWebServer();
        baseUrl = mockServer.url("/").toString();
        port = mockServer.getPort();
        var mockResponse = new MockResponse().setBody(readResourceFile("index.html"));
        mockServer.enqueue(mockResponse);
    }

    @BeforeEach
     public void beforeEach() throws SQLException, IOException {
        app = App.getApp();
    }

    @AfterAll
    public static void afterAll() throws IOException {
        mockServer.shutdown();
    }

    @Test
    void testRoot() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.rootPath());
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("Анализатор страниц");
        });
    }

    @Test
    void testEnvironment() throws Exception {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.rootPath());
            assertThat(response.code()).isEqualTo(200);
            System.out.println("port= " + port);
            System.out.println("app.port()= " + app.port());
            System.out.println("server.port()= " + server.port());
            System.out.println("mockServer.getPort()= " + mockServer.getPort());
            assertThat(server.port()).isEqualTo(app.port()); // how to establish port 7070?
        });
    }

    @Test
    void testIndex() throws Exception {
        var url = new Url("http://www.rbc.ru", Timestamp.valueOf(LocalDateTime.now()));
        UrlsRepository.save(url);
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.urlsPath());
            assertThat(response.code()).isEqualTo(200);
            var bodyString = response.body().string();
            assertThat(bodyString).contains("Сайты");
            assertThat(bodyString).contains("http://www.rbc.ru");
            assertThat(bodyString).doesNotContain("http://www.mail.ru");
        });
    }

    @Test
    void testRegisterNewSite1() throws Exception {
        var url = new Url("http://www.rbc.ru", Timestamp.valueOf(LocalDateTime.now()));
        UrlsRepository.save(url);
        JavalinTest.test(app, (server, client) -> {
            //how to post param data?
            var response = client.post(NamedRoutes.urlsPath(), "http://www.rbc.ru");
            assertThat(response.code()).isEqualTo(302);
            var headerString = response.header("Location");
            assertThat(headerString).contains("/urls");
        });
        var url1 = new Url("http://www.vk.ru", Timestamp.valueOf(LocalDateTime.now()));
        UrlsRepository.save(url1);
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.urlsPath());
            var bodyString = response.body().string();
            assertThat(bodyString).contains("http://www.rbc.ru");
            assertThat(bodyString).contains("http://www.vk.ru");

            var urlrbc = UrlsRepository.search("http://www.rbc.ru").get();
            var urlvk = UrlsRepository.search("http://www.vk.ru").get();
            assertThat(urlrbc.getName()).isEqualTo("http://www.rbc.ru");
            assertThat(urlvk.getName()).isEqualTo("http://www.vk.ru");
        });
    }

    @Test
    void testShowUrl() throws Exception {
        var url = new Url("http://www.rbc.ru", Timestamp.valueOf(LocalDateTime.now()));
        UrlsRepository.save(url);
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.urlPath("1"));
            assertThat(response.code()).isEqualTo(200);
            var bodyString = response.body().string();
            assertThat(bodyString).contains("Сайт:");
            assertThat(bodyString).contains("http://www.rbc.ru");
            assertThat(bodyString).contains("Запустить проверку");
        });
    }

    @Test
    void testCheckUrl() throws Exception {
        var url = new Url("http://www.rbc.ru", Timestamp.valueOf(LocalDateTime.now()));
        UrlsRepository.save(url);
        JavalinTest.test(app, (server, client) -> {
            var response = client.post(NamedRoutes.checkPath("1"));
            assertThat(response.code()).isEqualTo(200);
            var bodyString = response.body().string();
            assertThat(bodyString).contains("Сайты");
            assertThat(bodyString).contains(Data.toDateString(UrlsRepository.find(1L).get().getCreatedAt()));
            assertThat(bodyString).contains("200");
            assertThat(bodyString).contains("http://www.rbc.ru");
        });
    }

}
