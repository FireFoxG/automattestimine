package api.helpers;

public class FileFormatHelper {
    public static String addTxtIfNoFoundInFilename(String filename) {
        String filenameToUse = filename;
        if(!filenameToUse.contains(".txt")) {
            filenameToUse+=".txt";
        }
        return filenameToUse;
    }
}
