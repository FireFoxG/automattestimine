package api.helpers;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;

public class TextFileWriter {
    private void writeToFile(List<String> list, String filename, OpenOption... options) {
        List<String> lines = list;
        Path file = Paths.get(filename);
        try {
            Files.write(file, lines, Charset.forName("UTF-8"), options);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("TextFileWriter: IOE");
        }
    }

    public void writeLinesToFile(List<String> list, String filename) {
        writeToFile(list, filename, StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING,StandardOpenOption.WRITE);
    }

    public void appendLinesToFile(List<String> list, String filename) {
        writeToFile(list, filename, StandardOpenOption.APPEND);
    }

    public void writeDataToFile(String line, String filename) {
        writeToFile(Arrays.asList(line), filename, StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING,StandardOpenOption.WRITE);
    }

    public void appendDataToFile(String line, String filename) {
        writeToFile(Arrays.asList(line), filename, StandardOpenOption.APPEND);
    }
}
