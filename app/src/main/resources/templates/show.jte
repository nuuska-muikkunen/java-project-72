@import hexlet.code.dto.urls.UrlPage

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
                        ${page.getUrl().getCreatedAt().toString()}
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
`
)
