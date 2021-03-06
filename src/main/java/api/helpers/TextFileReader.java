package api.helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TextFileReader {

    public List<String> readDataFromFile(String filename) {
        BufferedReader br = null;
        FileReader fr = null;
        List<String> arrayOfLines = new ArrayList<String>();
        try {
            File file = new File(filename);
            if(file.exists() && !file.isDirectory()) {
                fr = new FileReader(file);
                br = new BufferedReader(fr);

                String currentLine;

                while ((currentLine = br.readLine()) != null) {
                    arrayOfLines.add(currentLine);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
                if (fr != null)
                    fr.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
        return arrayOfLines;
    }

}
