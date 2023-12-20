@import gg.jte.Content
@import hexlet.code.dto.BasePage

@param Content content
@param BasePage page = null

<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Bootstrap demo</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet"
          integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN"
          crossorigin="anonymous">
</head>
<body class="d-flex flex-column min-vh-100">
<nav class="navbar navbar-dark bg-dark navbar-expand-lg bg-body-tertiary" data-bs-theme="dark">
    <div class="container-fluid text-white">
        <a class="navbar-brand" href="/">Анализатор страниц</a>
        <div class="collapse navbar-collapse" id="navbarText">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item">
                    <a class="nav-link" href="/">Главная</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="/urls">Сайты</a>
                </li>
            </ul>
        </div>
    </div>
</nav>

@if(page != null && page.getFlash() != null)
    <div class="alert alert-${page.getFlashType()}" role="alert">
        ${page.getFlash()}
    </div>
@endif

<main class="flex-grow-1">
    ${content}
</main>

<footer class="footer border-top py-3 mt-5 bg-light">
    <div class="text-center">
        created by
        <a href="https://ru.hexlet.io/" target="_blank">Hexlet</a>
    </div>
</footer>

</body>
</html>
