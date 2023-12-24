package hexlet.code.dto.urls;

import hexlet.code.model.UrlCheck;
import hexlet.code.dto.BasePage;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UrlCheckPage extends BasePage {
    private UrlCheck urlCheck;
}
