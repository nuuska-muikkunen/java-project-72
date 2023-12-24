package hexlet.code.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import hexlet.code.dto.urls.UrlChecksPage;
import hexlet.code.dto.urls.UrlPage;

import hexlet.code.App;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlChecksRepository;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlsRepository;

public class Data {

    public static String readResourceFile(String fileName) throws IOException {
        var inputStream = App.class.getClassLoader().getResourceAsStream(fileName);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    public static List<UrlCheck> getChecksList(UrlPage page) {
        try {
            return UrlChecksRepository.getChecks(page.getUrl().getId()).get();
        } catch (SQLException e) {
            return new ArrayList<>();
        }
    }

    public static String getLatestCheckTime(Url url) {
        try {
            if (UrlChecksRepository.getChecks(url.getId()).isPresent()
                    && UrlChecksRepository.getChecks(url.getId()).get().size() >= 1) {
                return UrlChecksRepository.getChecks(url.getId())
                        .get().get(UrlChecksRepository.getChecks(url.getId()).get().size() - 1)
                        .getCreatedAt().toString();
            } else {
                return "";
            }
        } catch (SQLException e) {
            return "";
        }
    }

    public static String getLatestCheckStatus(Url url) {
        try {
            if (UrlChecksRepository.getChecks(url.getId()).isPresent()
                    && UrlChecksRepository.getChecks(url.getId()).get().size() >= 1) {
                return String.valueOf(UrlChecksRepository.getChecks(url.getId())
                        .get().get(UrlChecksRepository.getChecks(url.getId()).get().size() - 1)
                        .getStatusCode());
            } else {
                return "";
            }
        } catch (SQLException e) {
            return "";
        }
    }

    public static Url getUrlOfCheck(UrlChecksPage page) {
        try {
            return UrlsRepository.find(page.getUrlChecks().get(page.getUrlChecks().size() - 1).getUrlId())
                    .get();
        } catch (SQLException e) {
            return new Url("http://example@example.example",
                    Timestamp.valueOf(LocalDateTime.now()));
        }
    }

}
