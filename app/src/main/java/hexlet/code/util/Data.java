package hexlet.code.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import hexlet.code.App;

public class Data {
    public static String readResourceFile(String fileName) throws IOException {
        var inputStream = App.class.getClassLoader().getResourceAsStream(fileName);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    public static String toDateString(Timestamp dateTimeStamp) {
        var date = dateTimeStamp.toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return date.format(formatter);
    }

}
