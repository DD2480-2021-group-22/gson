package com.google.gson;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * The FileAppender class is used to append ints as single lines to a file. It is constructed
 * using a filepath and creates the file at the filepath if needed. Any writes to a closed
 * FileAppender will result in unspecified behaviour.
 */
public class FileAppender {

    // PrintWriter solution largely based on SO question at:
    // https://stackoverflow.com/questions/4614227/how-to-add-a-new-line-of-text-to-an-existing-file-in-java
    String filepath;

    /**
     * Creates a FileAppender which will attempt to write to the file specified.
     * Creates the file at the filepath if it does not exist.
     * @param filePath The file to append lines to.
     */
    public FileAppender(String filePath) {
        this.filepath = filePath;
    }

    /**
     * Appends an int to the end of the current file as a single line.
     * This method will not throw an exception if the FileAppender has been closed.
     * @param x the integer to write.
     */
    public void appendInt(int x) {
        PrintWriter printWriter;
        try {
            File coverageFile = new File(filepath);
            coverageFile.createNewFile(); // create file if it does not exist
            FileWriter fw = new FileWriter(coverageFile, true); // appends to file
            printWriter = new PrintWriter(fw, true); // automatic flush on
        } catch (Exception e) {
            System.out.println("FAILED TO SET UP TEMP FILE WRITER");
            throw new RuntimeException("FAILED TO SET UP TEMP FILE WRITER", e);
        }
        printWriter.println(x);
        printWriter.close();
    }
}
