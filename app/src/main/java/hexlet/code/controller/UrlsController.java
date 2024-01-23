package hexlet.code.controller;

import hexlet.code.model.UrlCheck;
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
import java.util.ArrayList;
import java.util.Collections;

import static hexlet.code.repository.UrlChecksRepository.getChecks;

public class UrlsController {
    public static void root(Context ctx) {
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
        var urlChecks = getChecks(id).isPresent() ? getChecks(id).get() : new ArrayList<UrlCheck>();
        var page = new UrlPage(url, urlChecks);
        page.setFlashType(ctx.consumeSessionAttribute("flashType"));
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        ctx.render("urls/show.jte", Collections.singletonMap("page", page));
    }

    public static void create(Context ctx) throws SQLException {
        String str = ctx.formParam("url").trim();
        try {
            var name = new URI(str);
            if (name.getScheme() == null || name.getAuthority() == null
                || name.getScheme().isEmpty() || name.getAuthority().isEmpty()) {
                ctx.sessionAttribute("flashType", "danger");
                ctx.sessionAttribute("flash", "Страница " + str + " некорректная");
            } else {
                String addressString = name.getScheme() + "://" + name.getAuthority();
                var url = new Url(addressString);
                if (UrlsRepository.isInDatabase(addressString)) {
                    ctx.sessionAttribute("flashType", "danger");
                    ctx.sessionAttribute("flash", "Страница " + addressString + " уже существует");
                } else {
                    UrlsRepository.save(url);
                    ctx.sessionAttribute("flashType", "success");
                    ctx.sessionAttribute("flash", "Страница успешно добавлена");
                }
            }
        } catch (URISyntaxException e) {
            ctx.sessionAttribute("flashType", "danger");
            ctx.sessionAttribute("flash", "Некорректный URL");
        }
        ctx.redirect(NamedRoutes.urlsPath());
    }
}
