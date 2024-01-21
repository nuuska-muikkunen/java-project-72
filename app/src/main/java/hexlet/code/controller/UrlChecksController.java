package hexlet.code.controller;

import hexlet.code.model.UrlCheck;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;
import hexlet.code.repository.UrlsRepository;
import org.jsoup.Jsoup;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.sql.Timestamp;
import java.util.Comparator;

import kong.unirest.Unirest;
import org.jsoup.nodes.Document;

import static hexlet.code.repository.UrlChecksRepository.saveCheck;
import static hexlet.code.repository.UrlChecksRepository.getChecks;
import static hexlet.code.util.Data.toDateString;

public class UrlChecksController {
    public static void createCheck(Context ctx) throws SQLException, RuntimeException {
        var urlId = ctx.pathParamAsClass("id", Long.class).get();
        String name;
        if (UrlsRepository.find(urlId).isPresent()) {
            name = UrlsRepository.find(urlId).get().getName();
        } else {
            throw (new SQLException("No such mane in DB"));
        }
        try {
            var response = Unirest.get(name).asString();
            var body = response.getBody();
            var statusCode = response.getStatus();
            Document doc = Jsoup.parse(body);
            var title = doc.getElementsByTag("title").isEmpty()
                    ? "" : doc.getElementsByTag("title").html();
            var h1 = doc.getElementsByTag("h1").isEmpty()
                    ? "" : doc.getElementsByTag("h1").html();
            var metaTags = doc.getElementsByAttributeValue("name", "description");
            var description = metaTags.isEmpty() ? "" : metaTags.get(0).attr("content");
            var createdAt = Timestamp.valueOf(LocalDateTime.now());
            var check = new UrlCheck(urlId, statusCode, title, h1, description, createdAt);
            saveCheck(check);
            ctx.sessionAttribute("checkType", "success");
            ctx.sessionAttribute("check", "Страница успешно проверена");
            ctx.redirect(NamedRoutes.urlPath(urlId));
        } catch (RuntimeException e) {
            ctx.sessionAttribute("checkType", "danger");
            ctx.sessionAttribute("check", "Некорректный адрес");
            ctx.redirect(NamedRoutes.urlsPath());
        }
    }
    public static String getLatestCheckTime(Long urlId) {
        try {
            if (getChecks(urlId).isPresent() && !getChecks(urlId).get().isEmpty()) {
                return toDateString(getChecks(urlId).get().stream()
                        .max(Comparator.comparing(UrlCheck::getCreatedAt))
                        .get()
                        .getCreatedAt());
            } else {
                return "";
            }
        } catch (SQLException e) {
            return "";
        }
    }

    public static String getLatestCheckStatus(Long urlId) {
        try {
            if (getChecks(urlId).isPresent() && !getChecks(urlId).get().isEmpty()) {
                return getChecks(urlId).get().stream()
                        .max(Comparator.comparing(UrlCheck::getCreatedAt))
                        .get()
                        .getStatusCode()
                        .toString();
            } else {
                return "";
            }
        } catch (SQLException e) {
            return "";
        }
    }

}
