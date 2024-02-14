package hexlet.code.repository;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Optional;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;

public class UrlChecksRepository extends BaseRepository {
    public static void saveCheck(UrlCheck check) throws SQLException {
        String sql = "INSERT INTO url_checks (url_id, status_code, title, h1, description, created_at)"
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (var conn = dataSource.getConnection();
             var preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setLong(1, check.getUrlId());
            preparedStatement.setLong(2, check.getStatusCode());
            preparedStatement.setString(3, check.getTitle());
            preparedStatement.setString(4, check.getH1());
            preparedStatement.setString(5, check.getDescription());
            preparedStatement.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            preparedStatement.executeUpdate();
            var generatedKeys = preparedStatement.getGeneratedKeys();
            // Устанавливаем ID в сохраненную сущность
            if (generatedKeys.next()) {
                check.setId(generatedKeys.getLong(1));
            } else {
                throw new SQLException("DB have not returned an id after saving an entity");
            }
        }
    }

    public static Optional<ArrayList<UrlCheck>> getChecks(Long urlId) throws SQLException {
        var sql = "SELECT * FROM url_checks WHERE url_id = ? ORDER BY id DESC";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, urlId);
            var resultSet = stmt.executeQuery();
            var     result = new ArrayList<UrlCheck>();
            while (resultSet.next()) {
                var id = resultSet.getLong("id");
                var statusCode = resultSet.getInt("status_code");
                var title = resultSet.getString("title");
                var h1 = resultSet.getString("h1");
                var description = resultSet.getString("description");
                var createdAt = resultSet.getTimestamp("created_at");
                var check = new UrlCheck(urlId, statusCode, title, h1, description);
                check.setId(id);
                check.setCreatedAt(createdAt);
                result.add(check);
            }
            return Optional.of(result);
        }
    }

    public static Optional<UrlCheck> getLastCheck(Long urlId) throws SQLException {
        var sql = "SELECT * FROM url_checks WHERE url_id = ? ORDER BY id DESC LIMIT 1";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, urlId);
            var resultSet = stmt.executeQuery();
            var check = new UrlCheck();
            while (resultSet.next()) {
                var id = resultSet.getLong("id");
                var statusCode = resultSet.getInt("status_code");
                var title = resultSet.getString("title");
                var h1 = resultSet.getString("h1");
                var description = resultSet.getString("description");
                var createdAt = resultSet.getTimestamp("created_at");
                check = new UrlCheck(urlId, statusCode, h1, title, description);
                check.setId(id);
                check.setCreatedAt(createdAt);
            }
            return Optional.of(check);
        }
    }

    public static LinkedHashMap<Url, UrlCheck> getUrlsWithLastChecks() throws SQLException {
        var urls = UrlsRepository.getEntities();
        LinkedHashMap<Url, UrlCheck> outputMap = new LinkedHashMap<>();
        urls.stream()
                .peek(u -> {
                    try {
                        var lastCheck = getLastCheck(u.getId()).orElse(new UrlCheck());
                        outputMap.put(u, lastCheck);
                    } catch (SQLException e) {
                        outputMap.put(u, new UrlCheck());
                    }
                }).toList();
        return outputMap;
    }

}
