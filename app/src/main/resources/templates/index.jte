@import hexlet.code.dto.urls.UrlsPage
@import hexlet.code.util.NamedRoutes

@param UrlsPage page

@template.page(
page = page,
content = @`
    <h1>Курсы</h1>
    <table class="table table-striped">
        @for(var url : page.getUrls())
            <tr>
                <td>
                    <a href="${NamedRoutes.urlPath(url.getName())}">${url.getId()}</a>
                </td>
                <td>
                    ${url.getName()}
                </td>
                <td>
                    ${url.getCreatedAt().toString()}
                </td>
            </tr>
        @endfor
    </table>
`,
footer = @`
    <p>created by <a href="https://ru.hexlet.io/">Hexlet</a></p>
`
)
