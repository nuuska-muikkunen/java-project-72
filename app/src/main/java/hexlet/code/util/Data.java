package hexlet.code.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.DataFormatException;

import hexlet.code.App;

public class Data {
    static final String PATH_TO_FIXTURE = "src/test/resources/";

    public static String fixture(String nameOfFile) {
        return PATH_TO_FIXTURE + nameOfFile;
    }
    public static String readResourceFile(String fileName) throws IOException {
        var inputStream = App.class.getClassLoader().getResourceAsStream(fileName);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    public static String getContentOfHtmlFile(String filePath1) throws DataFormatException, IOException {
        String endOfFile1 = filePath1.substring(filePath1.lastIndexOf(".") + 1);
        if (!endOfFile1.equals("json") && !endOfFile1.equals("yaml") && !endOfFile1.equals("yml")) {
            throw new DataFormatException("There are files of unknown format");
        }
        return Files.readString(Paths.get(filePath1).toAbsolutePath().normalize());
    }

    public static String toDateString(Timestamp dateTimeStamp) {
        var date = dateTimeStamp.toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return date.format(formatter);
    }

}
