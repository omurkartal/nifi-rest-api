package edu.omur.nifirestapi.utility;

import java.io.*;

public final class FileUtility {
    public static void writeToFile(String fileName, String inputText) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(inputText);
        writer.close();
    }

    public static String readFirstLineFromFile(String fileName) throws IOException {
        final BufferedReader reader = new BufferedReader(new FileReader(fileName));
        final String currentLine = reader.readLine();
        reader.close();
        return currentLine;
    }
}
