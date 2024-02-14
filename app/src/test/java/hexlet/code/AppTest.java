package hexlet.code;

import hexlet.code.model.Url;
import hexlet.code.repository.UrlChecksRepository;
import hexlet.code.repository.UrlsRepository;
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

import static hexlet.code.util.Utils.readResourceFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AppTest {
    private static Javalin app;
    private static MockWebServer mockServer;
    private static String baseUrl;
    private static int port;

    @BeforeAll
    public static void beforeAll() throws IOException {
        mockServer = new MockWebServer();
        var mockResponse = new MockResponse().setBody(readResourceFile("index.html"));
        mockServer.enqueue(mockResponse);
    }

    @BeforeEach
    public final void beforeEach() throws SQLException, IOException {
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
    void testRegisterNewSites() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=http://www.rbc.ru";
            client.post(NamedRoutes.urlsPath(), requestBody);
            requestBody = "url=http://www.mail.ru";
            client.post(NamedRoutes.urlsPath(), requestBody);
            var response = client.get(NamedRoutes.urlsPath());
            assertThat(response.code()).isEqualTo(200);
            var bodyString = response.body().string();
            assertThat(UrlsRepository.getEntities()).hasSize(2);
            assertTrue(UrlsRepository.findByName("http://www.rbc.ru").isPresent());
            assertEquals("http://www.rbc.ru",
                    UrlsRepository.findByName("http://www.rbc.ru").get().getName());
            assertTrue(UrlsRepository.findByName("http://www.mail.ru").isPresent());
            assertEquals("http://www.mail.ru",
                    UrlsRepository.findByName("http://www.mail.ru").get().getName());
            assertThat(bodyString).contains("Сайты");
            assertThat(bodyString).contains("http://www.rbc.ru");
            assertThat(bodyString).contains("http://www.mail.ru");
        });
    }

    @Test
    void testWrongSite() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=http://www.rbc.ru";
            client.post(NamedRoutes.urlsPath(), requestBody);
            assertThat(UrlsRepository.getEntities()).hasSize(1);

            requestBody = "url=фывфыа";
            var response = client.post(NamedRoutes.urlsPath(), requestBody);
            var bodyString = response.body().string();
            assertThat(UrlsRepository.getEntities()).hasSize(1);

            requestBody = "url=http://";
            client.post(NamedRoutes.urlsPath(), requestBody);
            assertThat(UrlsRepository.getEntities()).hasSize(1);
        });
    }

    @Test
    void testDoubleSite() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=http://www.rbc.ru";
            client.post(NamedRoutes.urlsPath(), requestBody);
            client.post(NamedRoutes.urlsPath(), requestBody);
            assertThat(UrlsRepository.getEntities()).hasSize(1);
        });
    }

    @Test
    void testShowUrl() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=http://www.rbc.ru";
            client.post(NamedRoutes.urlsPath(), requestBody);
            var id = UrlsRepository.findByName("http://www.rbc.ru").get().getId();
            var response = client.get(NamedRoutes.urlPath(id));
            assertThat(response.code()).isEqualTo(200);
            var bodyString = response.body().string();
            assertThat(bodyString).contains("Сайт:");
            assertThat(bodyString).contains("http://www.rbc.ru");
            assertThat(bodyString).contains("Запустить проверку");
        });
    }

    @Test
    void testCheckUrl() throws SQLException {
        var url = mockServer.url("/").toString();
        Url urlForCheck = new Url(url);
        UrlsRepository.save(urlForCheck);
        JavalinTest.test(app, (server, client) -> {
            var response = client.post(NamedRoutes.checkPath(urlForCheck.getId()));
            try {
                assertThat(response.code()).isEqualTo(200);
                var lastCheck = UrlChecksRepository.getLastCheck(urlForCheck.getId()).orElseThrow();
                assertThat(lastCheck.getTitle()).isEqualTo("Title");
                assertThat(lastCheck.getH1()).isEqualTo("This is a header");
                assertThat(lastCheck.getDescription()).isEqualTo("description");
            } catch (final Throwable th) {
                System.out.println(th.getMessage());
            } finally {
                response.close();
            }
        });
    }

}
