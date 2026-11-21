package library;

import java.util.ArrayList;

/**
 * Small helper methods for writing and reading one line of a CSV file.
 */
final class CsvUtil {

    private CsvUtil() {
    }

    /**
     * Puts a value inside double quotes so it can safely contain commas.
     *
     * @param value the text to write (null is written as empty text)
     * @return the quoted text
     */
    static String quote(String value) {
        String text = (value == null) ? "" : value.replace("\r", " ").replace("\n", " ");
        return "\"" + text.replace("\"", "\"\"") + "\"";
    }

    /**
     * Splits one line into its values, understanding double quotes.
     *
     * @param line the line to split
     * @return the values of the line
     */
    static ArrayList<String> splitLine(String line) {
        ArrayList<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean insideQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (insideQuotes) {
                if (c == '"' && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"'); // two quotes in a row mean one quote character
                    i++;
                } else if (c == '"') {
                    insideQuotes = false;
                } else {
                    current.append(c);
                }
            } else if (c == '"') {
                insideQuotes = true;
            } else if (c == ',') {
                fields.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        fields.add(current.toString());
        return fields;
    }
}
