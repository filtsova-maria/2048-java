package utils;

import java.io.File;
import java.text.DecimalFormat;

/**
 * A utility program to calculate the total size of written java code.
 * This program recursively scans all subdirectories in the project to find .java files and sums their sizes.
 */
public class ProjectSizeCalculator {
    public static void main(String[] args) {
        String currentDirectory = System.getProperty("user.dir");
        System.out.println("Directory: " + currentDirectory);

        File folder = new File(currentDirectory);

        long totalSizeInBytes = calculateJavaFilesSize(folder);
        double sizeInKilobytes = totalSizeInBytes / 1024.0;

        DecimalFormat df = new DecimalFormat("0.00");
        System.out.println("Total size of .java files: " + df.format(sizeInKilobytes) + " KB");
    }

    /**
     * Recursively calculates the total size of all .java files within a directory.
     *
     * @param folder the directory to scan for .java files
     * @return the total size of all .java files in bytes
     */
    private static long calculateJavaFilesSize(File folder) {
        long totalSize = 0;

        File[] files = folder.listFiles();
        if (files == null) return totalSize;

        for (File file : files) {
            if (file.isDirectory()) {
                totalSize += calculateJavaFilesSize(file);
            } else if (file.isFile() && file.getName().endsWith(".java")) {
                totalSize += file.length();
            }
        }

        return totalSize;
    }
}
