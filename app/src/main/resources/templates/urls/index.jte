@import hexlet.code.dto.urls.UrlsPage
@import hexlet.code.model.Url
@import hexlet.code.util.NamedRoutes
@import java.time.format.DateTimeFormatter

@param UrlsPage page

@template.layout.page(
page = page,
content = @`
    <div class="container-lg mt-5">
        !{var formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");}
        <h1>Сайты</h1>
        <table class="table table-bordered table-hover mt-3">
            <thead>
            <tr>
                <th class="col-1">ID</th>
                <th>Имя</th>
                <th class="col-2">Последняя проверка</th>
                <th class="col-1">Код ответа</th>
            </tr>
            </thead>
            <tbody>
            @if(!page.getUrlsWithChecks().isEmpty())
                @for(Url url : page.getUrlsWithChecks().keySet())
                    <tr>
                        <td>
                            ${url.getId()}
                        </td>
                        <td>
                            <a href="${NamedRoutes.urlPath(url.getId())}">${url.getName()}</a>
                        </td>
                        <td>
                            @if(page.getUrlsWithChecks().get(url).getCreatedAt() != null)
                                ${page.getUrlsWithChecks().get(url).getCreatedAt().toLocalDateTime().format(formatter)}
                            @endif
                        </td>
                        <td>
                            @if(page.getUrlsWithChecks().get(url).getStatusCode() != null)
                                ${page.getUrlsWithChecks().get(url).getStatusCode().toString()}
                            @endif
                        </td>
                    </tr>
                @endfor
            @endif
            </tbody>
        </table>
    </div>
`
)