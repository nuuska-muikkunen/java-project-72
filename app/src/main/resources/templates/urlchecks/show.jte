@import hexlet.code.dto.urls.UrlChecksPage
@import hexlet.code.util.Data
@import hexlet.code.util.NamedRoutes

@param UrlChecksPage page

@template.layout.page(
content = @`
    <div class="container-lg mt-5">
        <h1>Сайт:${Data.getUrlOfCheck(page).getName()}</h1>
        <table class="table table-bordered table-hover mt-3">
            <tbody>
            <tr>
                <td>
                    ID
                </td>
                <td>
                    ${page.getUrlChecks().get(page.getUrlChecks().size() - 1).getUrlId()}
                </td>
            </tr>
            <tr>
                <td>
                    Имя
                </td>
                <td>
                    ${Data.getUrlOfCheck(page).getName()}
                </td>
            </tr>
            <tr>
                <td>
                    Дата создания
                </td>
                <td>
                    ${Data.getUrlOfCheck(page).getCreatedAt().toString()}
                </td>
            </tr>
            </tbody>
        </table>
        <h2 class="mt-5">Проверки</h2>
        <form method="post"
              action="${NamedRoutes.checkPath(page.getUrlChecks().get(page.getUrlChecks().size() - 1).getUrlId())}">
            <button type="submit" class="btn btn-primary">Запустить проверку</button>
        </form>
        <table class="table table-bordered table-hover mt-3">
            <thead>
            <tr>
                <th class="col-1">ID</th>
                <th class="col-1">Код ответа</th>
                <th>title</th>
                <th>h1</th>
                <th>description</th>
                <th class="col-2">Дата проверки</th>
            </tr>
            </thead>
            <tbody>
                @for(var check : page.getUrlChecks())
                    <tr>
                        <td>
                            ${check.getId()}
                        </td>
                        <td>
                            ${check.getStatusCode()}
                        </td>
                        <td>
                            ${check.getTitle()}
                        </td>
                        <td>
                            ${check.getH1()}
                        </td>
                        <td>
                            ${check.getDescription()}
                        </td>
                        <td>
                        ${check.getCreatedAt().toString()}
                        </td>
                    </tr>
                @endfor
            </tbody>
        </table>
    </div>
`
)
