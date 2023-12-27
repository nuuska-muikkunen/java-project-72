package hexlet.code.controller;

import hexlet.code.dto.urls.UrlCheckPage;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlChecksRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;
import hexlet.code.repository.UrlsRepository;
import org.jsoup.Jsoup;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.sql.Timestamp;

import kong.unirest.Unirest;
import org.jsoup.nodes.Document;

public class UrlChecksController {
    public static void createCheck(Context ctx) throws SQLException {
        var urlId = ctx.pathParamAsClass("id", Long.class).get();
        var response = Unirest.get(UrlsRepository.find(urlId).orElseThrow().getName()).asString();
        var body = response.getBody();
        var statusCode = response.getStatus();
        Document doc = Jsoup.parse(body);
        var title = doc.getElementsByTag("title").isEmpty()
                ? "" : doc.getElementsByTag("title").html();
        var h1 = doc.getElementsByTag("h1").isEmpty()
                ? "" : doc.getElementsByTag("h1").html();
        var description = doc.getElementsByTag("meta").isEmpty()
                        ? ""
                : doc.getElementsByAttributeValue("name", "description").get(0).attributes().get("content");
        var createdAt = Timestamp.valueOf(LocalDateTime.now());
        var check = new UrlCheck(urlId, statusCode, title, h1, description, createdAt);
        UrlChecksRepository.saveCheck(check);
        var page = new UrlCheckPage(check);
        ctx.sessionAttribute("checkType", "success");
        ctx.sessionAttribute("check", "Страница успешно проверена");
        System.out.println("check = " + page.getCheck() + "\ncheckType= " + page.getCheckType());
        ctx.redirect(NamedRoutes.urlPath(urlId));
    }

    public static boolean hasChecks(Long urlId) {
        try {
            return UrlChecksRepository.getChecks(urlId).isPresent();
        } catch (SQLException e) {
            return false;
        }
    }

}
