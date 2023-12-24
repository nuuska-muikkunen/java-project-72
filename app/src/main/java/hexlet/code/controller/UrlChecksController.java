package hexlet.code.controller;

import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlChecksRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;
import hexlet.code.repository.UrlsRepository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.sql.Timestamp;

import kong.unirest.Unirest;
public class UrlChecksController {
    public static void createCheck(Context ctx) throws SQLException {
        var urlId = ctx.pathParamAsClass("id", Long.class).get();
        var response = Unirest.get(UrlsRepository.find(urlId).get().getName()).asString();
        var statusCode = response.getStatus();
        var body = response.getBody();
        var title = (body.contains("<title>") && body.contains("</title>"))
                ? body.substring(body.indexOf("<title>") + "<title>".length(), body.indexOf("</title>")) : "";
        var h1 = (body.contains("<h1>") && body.contains("</h1>"))
                ? body.substring(body.indexOf("<h1>") + "<h1>".length(), body.indexOf("</h1>")) : "";
        var description = (body.contains("<meta name=\"description\" content=\""))
                ? body.substring(body.indexOf("<meta name=\"description\" content=\"")
                        + "<meta name=\"description\" content=\"".length(),
                body.indexOf("\"/>", body.indexOf("<meta name=\"description\" content=\""))) : "";
        var createdAt = Timestamp.valueOf(LocalDateTime.now());
        var check = new UrlCheck(urlId, statusCode, title, h1, description, createdAt);
        UrlChecksRepository.saveCheck(check);
        ctx.sessionAttribute("checkType", "success");
        ctx.sessionAttribute("check", "Страница успешно проверена");
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
