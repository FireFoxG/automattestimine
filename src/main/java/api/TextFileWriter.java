package api;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class FileWriter {
    public void writeLinesToFile(List<String> list, String filename) {
        List<String> lines = list;
        Path file = Paths.get(filename);
        try {
            Files.write(file, lines, Charset.forName("UTF-8"));
        } catch (IOException e) {
            System.out.println("FileWriter: IOE");
        }
    }

    public void writeStringToFile(String line, String filename) {
        List<String> lineArray = Arrays.asList(line);
        writeLinesToFile(lineArray, filename);
    }
}
