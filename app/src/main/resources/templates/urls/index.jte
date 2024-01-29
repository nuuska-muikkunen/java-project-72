@import hexlet.code.dto.urls.UrlsPage
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
                @for(var urlWithCheck : page.getUrlsWithChecks().entrySet())
                    <tr>
                        <td>
                            ${urlWithCheck.getKey().getId()}
                        </td>
                        <td>
                            <a href="${NamedRoutes.urlPath(urlWithCheck.getKey().getId())}">
                                ${urlWithCheck.getKey().getName()}</a>
                        </td>
                        <td>
                            @if(urlWithCheck.getValue().getCreatedAt() != null)
                                ${urlWithCheck.getValue().getCreatedAt().toLocalDateTime().format(formatter)}
                            @endif
                        </td>
                        <td>
                            @if (urlWithCheck.getValue().getStatusCode() != null)
                                ${urlWithCheck.getValue().getStatusCode().toString()}
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
