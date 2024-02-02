package hexlet.code.controller;

import hexlet.code.model.UrlCheck;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;
import hexlet.code.repository.UrlsRepository;
import org.jsoup.Jsoup;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.sql.Timestamp;

import kong.unirest.Unirest;
import org.jsoup.nodes.Document;

import static hexlet.code.repository.UrlChecksRepository.saveCheck;

public class UrlChecksController {
    public static void createCheck(Context ctx) throws SQLException, RuntimeException {
        var urlId = ctx.pathParamAsClass("id", Long.class).get();
        String name;
        if (UrlsRepository.findById(urlId).isPresent()) {
            name = UrlsRepository.findById(urlId).get().getName();
        } else {
            throw (new SQLException("No such mane in DB"));
        }
        try {
            var response = Unirest.get(name).asString();
            Document doc = Jsoup.parse(response.getBody());
            var h1 = doc.getElementsByTag("h1").isEmpty()
                    ? "" : doc.getElementsByTag("h1").html();
            var metaTags = doc.getElementsByAttributeValue("name", "description");
            var description = metaTags.isEmpty() ? "" : metaTags.get(0).attr("content");
            var createdAt = Timestamp.valueOf(LocalDateTime.now());
            var check = new UrlCheck(urlId, response.getStatus(), doc.title(), h1, description, createdAt);
            saveCheck(check);
            ctx.sessionAttribute("flashType", "success");
            ctx.sessionAttribute("flash", "Страница успешно проверена");
        } catch (RuntimeException e) {
            ctx.sessionAttribute("flashType", "danger");
            ctx.sessionAttribute("flash", "Некорректный адрес");
        }
        ctx.redirect(NamedRoutes.urlPath(urlId));
    }

}
