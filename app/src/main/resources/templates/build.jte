@import hexlet.code.util.NamedRoutes

@template.layout.page(
content = @`
    <form action="${NamedRoutes.urlsPath()}" method="post">
        <div class="mb-3">
            <label class="form-label">
                Name
                <input type="text" class="form-control" name="name" />
            </label>
        </div>
        <div class="mb-3">
            <label class="form-label">
                Description
                <input type="text" required class="form-control" name="createdAt" />
            </label>
        </div>
        <input type="submit" value="Register" />
    </form>
`
)