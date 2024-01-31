package hexlet.code.controller;

import hexlet.code.dto.BasePage;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlChecksRepository;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import hexlet.code.dto.urls.UrlPage;
import hexlet.code.dto.urls.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlsRepository;
import hexlet.code.util.NamedRoutes;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.ArrayList;

import static hexlet.code.repository.UrlChecksRepository.getChecks;
import static hexlet.code.repository.UrlsRepository.isInDatabase;

public class UrlsController {
    public static void root(Context ctx) {
        var page = new BasePage();
        page.setFlashType(ctx.consumeSessionAttribute("flashType"));
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        ctx.render("mainPage.jte", Collections.singletonMap("page", page));
    }

    public static void index(Context ctx) throws SQLException {
        var urlsWithChecks = UrlChecksRepository.getAllChecksOrdered();
        var page = new UrlsPage(urlsWithChecks);
        page.setFlashType(ctx.consumeSessionAttribute("flashType"));
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        ctx.render("urls/index.jte", Collections.singletonMap("page", page));
    }

    public static void show(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class).get();
        var url = UrlsRepository.findById(id)
                .orElseThrow(() -> new NotFoundResponse("Url with id = " + id + " not found"));
        var urlChecks = getChecks(id).isPresent()
                ? getChecks(id).get() : new ArrayList<UrlCheck>();
        var page = new UrlPage(url, urlChecks);
        page.setFlashType((String) ctx.consumeSessionAttribute("flashType"));
        page.setFlash((String) ctx.consumeSessionAttribute("flash"));
        ctx.render("urls/show.jte", Collections.singletonMap("page", page));
    }

    public static void create(Context ctx) throws SQLException {
        String str = ctx.formParam("url").trim();
        try {
            var name = new URI(str);
            if (name.getScheme() == null || name.getAuthority() == null
                || name.getScheme().isEmpty() || name.getAuthority().isEmpty()) {
                ctx.sessionAttribute("flashType", "danger");
                ctx.sessionAttribute("flash", "Некорректный URL");
                ctx.redirect(NamedRoutes.rootPath());
            } else {
                String addressString = name.getScheme() + "://" + name.getAuthority();
                if (isInDatabase(addressString)) {
                    ctx.sessionAttribute("flashType", "info");
                    ctx.sessionAttribute("flash", "Страница уже существует");
                } else {
                    var url = new Url(addressString);
                    UrlsRepository.save(url);
                    ctx.sessionAttribute("flashType", "success");
                    ctx.sessionAttribute("flash", "Страница успешно добавлена");
                }
                ctx.redirect(NamedRoutes.urlsPath());
            }
        } catch (URISyntaxException e) {
            ctx.sessionAttribute("flashType", "danger");
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.redirect(NamedRoutes.rootPath());
        }
    }
}
