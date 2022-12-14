package com.SLOC;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LOC {

    private List<File> uniqueFiles;
    private List<File> allFiles;
    private Map<String, File> fileContentMap;

    private int totalFiles;
    private int totalUniqueFiles;
    private int totalBlankLines;
    private int totalCommentLines;
    private int totalCodeLines;

    LOC() {
        this.uniqueFiles = new ArrayList<>();
        this.allFiles = new ArrayList<>();
        this.fileContentMap = new HashMap<>();

        this.totalFiles = 0;
        this.totalUniqueFiles = 0;
        this.totalBlankLines = 0;
        this.totalCommentLines = 0;
        this.totalCodeLines = 0;
    }

    /**
     * Program Start Point.
     */
    public void start(String path) {
        File file = new File(path);
        if(file.exists()) { // Check if file or directory exists
            if(file.isDirectory()) { // Check if path is directory
                this.getListOfAllFiles(path);
                this.getListOfUniqueFiles();
                this.processFiles();
            }else if(file.isFile()) { // Check if path is file
                totalFiles = 1;
                totalUniqueFiles = 1;
                this.readFileByLine(new File(path));
            }
        }else {
            System.out.println("Error! path: "+path+" does not exist!");
        }
    }

    /**
     * Read java file.
     * @return content of a file
     * @param filePath path of a file
     */
    public String readFile(File filePath) throws IOException {
        return Files.readString(Paths.get(String.valueOf(filePath)));
    }

    /**
     * finds all java files inside a directory.
     * @param directory directory path
     */
    public void getListOfAllFiles(String directory) {
        try (Stream<Path> all = Files.walk(Paths.get(directory), Integer.MAX_VALUE)) {
            List<String> result = all
                    .map(Path::toString)
                    .filter(file -> file.endsWith(".java"))
                    .collect(Collectors.toList());
            for(String x : result) {
                allFiles.add(new File(x));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        totalFiles = allFiles.size();
    }

    /**
     * finds unique java files inside a directory.
     */
    public void getListOfUniqueFiles() {
        allFiles.forEach(filePath -> {
            try {
                String content = this.readFile(filePath);
                if(!fileContentMap.containsKey(content)) {
                    fileContentMap.put(content, filePath);
                    uniqueFiles.add(filePath);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        if(uniqueFiles.size() > 0) {
            totalUniqueFiles = uniqueFiles.size();
        }
    }

    /**
     * Process list of files inside a directory.
     */
    public void processFiles() {
        for (File filePath : uniqueFiles) {
            this.readFileByLine(filePath);
        }
    }

    /**
     * Read a file line by line and counts
     * blank lines, comments and source code lines.
     * @param filePath path of a file
     */
    public void readFileByLine(File filePath) {
        AtomicBoolean isComment = new AtomicBoolean(false);
        try (Stream<String> stream = Files.lines(Paths.get(String.valueOf(filePath)))) {
            stream.map(String::trim).forEach(currentLine -> {
                if (!isComment.get() && (currentLine.isBlank() || currentLine.equals(" "))) { // counts blank lines
                    totalBlankLines++;
                } else if (!isComment.get() && currentLine.startsWith("//")) { // counts single line comment
                    totalCommentLines++;
                } else if (!isComment.get() && currentLine.startsWith("/*")) {
                    if (!currentLine.endsWith("*/")) { // counts single line comment
                        isComment.set(true); // enable multiple comment code on
                    }
                    totalCommentLines++;
                } else if (isComment.get()) { //counts multiple lines comments
                    if(currentLine.isBlank()) { // blank lines inside comments
                        totalBlankLines++;
                    }else {
                        totalCommentLines++;
                        if(currentLine.endsWith("*/") || currentLine.contains("*/")) {
                            isComment.set(false);
                        }
                    }
                } else { // counts code lines
                    if(currentLine.contains("/*")) {
                        if (!(currentLine.endsWith("*/") || currentLine.contains("*/"))) {
                            isComment.set(true); // enable multiple comment code on
                        }
                    }
                    totalCodeLines++;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getTotalFiles() {
        return totalFiles;
    }

    public int getTotalUniqueFiles() {
        return totalUniqueFiles;
    }

    public int getTotalBlankLines() {
        return totalBlankLines;
    }

    public int getTotalCommentLines() {
        return totalCommentLines;
    }

    public int getTotalCodeLines() {
        return totalCodeLines;
    }

    @Override
    public String toString() {
        return  "1. The total number of Java files: " + getTotalFiles() +
                "\n2. The total number of unique Java files: " + getTotalUniqueFiles() +
                "\n3. The total number of blank lines. (in all unique java files): " + getTotalBlankLines() +
                "\n4. The total number of comment lines. (in all unique java files): " + getTotalCommentLines() +
                "\n5. The total number of code lines. (only code line excluding comments and blanks):" + getTotalCodeLines();
    }
}

