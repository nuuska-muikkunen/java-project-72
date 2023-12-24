package hexlet.code.controller;

import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import hexlet.code.dto.urls.UrlPage;
import hexlet.code.dto.urls.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlsRepository;
import hexlet.code.util.NamedRoutes;

import java.net.MalformedURLException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.sql.Timestamp;
import java.net.URL;

public class UrlsController {
    public static void root(Context ctx) throws SQLException {
        ctx.render("mainPage.jte");
    }

    public static void index(Context ctx) throws SQLException {
        var urls = UrlsRepository.getEntities();
        var page = new UrlsPage(urls);
        page.setFlashType(ctx.consumeSessionAttribute("flashType"));
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        ctx.render("urls/index.jte", Collections.singletonMap("page", page));
    }

    public static void show(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class).get();
        var url = UrlsRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("Url with id = " + id + " not found"));
        var page = new UrlPage(url);
        page.setCheckType(ctx.consumeSessionAttribute("checkType"));
        page.setCheck(ctx.consumeSessionAttribute("check"));
        ctx.render("urls/show.jte", Collections.singletonMap("page", page));
    }

    public static void create(Context ctx) throws SQLException {
        var createdAt = Timestamp.valueOf(LocalDateTime.now());
        var str = ctx.formParam("url");
        System.out.println("url = " + str);
        try {
            var name = new URL(str);
            var url = new Url(name.toString(), createdAt);
            if (UrlsRepository.isInDatabase(str)) {
                ctx.sessionAttribute("flashType", "danger");
                ctx.sessionAttribute("flash", "Страница " + str + " уже существует");
            } else {
                UrlsRepository.save(url);
                ctx.sessionAttribute("flashType", "success");
                ctx.sessionAttribute("flash", "Страница успешно добавлена");
            }
        } catch (MalformedURLException e) {
            ctx.sessionAttribute("flashType", "danger");
            ctx.sessionAttribute("flash", "Некорректный URL");
        }
        ctx.redirect(NamedRoutes.urlsPath());
    }
}
