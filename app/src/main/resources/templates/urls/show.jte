@import hexlet.code.dto.urls.UrlPage
@import hexlet.code.util.NamedRoutes
@import static hexlet.code.util.Data.getChecksList
@import static hexlet.code.controller.UrlChecksController.hasChecks
@import static hexlet.code.util.Data.toDateString

@param UrlPage page

@template.layout.page(
content = @`
    <div class="container-lg mt-5">
        <h1>Сайт:${page.getUrl().getName()}</h1>
        <table class="table table-bordered table-hover mt-3">
            <tbody>
                <tr>
                    <td>
                        ID
                    </td>
                    <td>
                        ${page.getUrl().getId()}
                    </td>
                </tr>
                <tr>
                    <td>
                        Имя
                    </td>
                    <td>
                        ${page.getUrl().getName()}
                    </td>
                </tr>
                <tr>
                    <td>
                        Дата создания
                    </td>
                    <td>
                        ${toDateString(page.getUrl().getCreatedAt())}
                    </td>
                </tr>
            </tbody>
        </table>
    <h2 class="mt-5">Проверки</h2>
    <form method="post" action="${NamedRoutes.checkPath(page.getUrl().getId())}">
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
        @if (hasChecks(page.getUrl().getId()))
            @for(var check : getChecksList(page))
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
                        ${toDateString(check.getCreatedAt())}
                    </td>
                </tr>
            @endfor
        @endif
        </tbody>
    </table>
    </div>
`
)
