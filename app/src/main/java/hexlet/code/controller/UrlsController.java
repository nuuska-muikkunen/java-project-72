package hexlet.code.controller;

import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import hexlet.code.dto.urls.UrlPage;
import hexlet.code.dto.urls.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlsRepository;
import hexlet.code.util.NamedRoutes;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.sql.Timestamp;

public class UrlsController {
    public static void index(Context ctx) throws SQLException {
        var urls = UrlsRepository.getEntities();
        var page = new UrlsPage(urls);
        ctx.render("index.jte", Collections.singletonMap("page", page));
    }

    public static void show(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class).get();
        var url = UrlsRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("Url with id = " + id + " not found"));
        var page = new UrlPage(url);
        ctx.render("show.jte", Collections.singletonMap("page", page));
    }

    public static void build(Context ctx) {
        ctx.render("build.jte");
    }

    public static void create(Context ctx) throws SQLException {
        var name = ctx.formParam("name");
        var createdAt = ctx.formParam("created_at");
        var url = new Url(name, Timestamp.valueOf(LocalDateTime.parse(createdAt)));
        UrlsRepository.save(url);
        ctx.redirect(NamedRoutes.urlsPath());
    }
}
