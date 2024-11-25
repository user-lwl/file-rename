package com.lwl.filerename.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 重命名
 */
@RestController
@RequestMapping("/fileRename")
public class fileRenameController {

    @Value("${lwl.iniFilePath}")
    private String iniFilePath;

    @Value("${lwl.targetFolderPath}")
    private String targetFolderPath;

    @Value("${lwl.logPath}")
    private String logPath;

    /**
     * 替换文件名中部分内容
     * 此处使用config1.ini
     * @param args
     */
    public static void main_part(String[] args) {
        // 指定INI文件路径和目标文件夹路径
        String iniFilePath = "F:\\file-rename\\src\\main\\resources\\config1.ini";
        String targetFolderPath = "F:\\test-txt";
        String logPath = "F:\\log.txt";

        // 文件名修改数量
        AtomicInteger fileRenameCount = new AtomicInteger();
        // 文件夹名修改数量
        AtomicInteger floderRenameCount = new AtomicInteger();
        // 文件名修改失败数量
        AtomicInteger fileRenameCountFail = new AtomicInteger();
        // 文件夹名修改失败数量
        AtomicInteger floderRenameCountFail = new AtomicInteger();
        try {
            // 读取INI文件内容
            Properties properties = new Properties();
            properties.load(new FileInputStream(iniFilePath));

            // 获取旧文件名和新文件名列表
            String oldNamesStr = properties.getProperty("oldName");
            String newNamesStr = properties.getProperty("newName");

            List<String> oldNames = Arrays.asList(oldNamesStr.split(","));
            List<String> newNames = Arrays.asList(newNamesStr.split(","));

            // 确保两个列表长度相同
            if (oldNames.size() != newNames.size()) {
                throw new IllegalArgumentException("Old names and new names lists must have the same length.");
            }
            // 遍历目标文件夹及其子文件夹中的所有文件并重命名
            Files.walk(Paths.get(targetFolderPath))
                    .filter(Files::isRegularFile)
                    .forEach(oldFilePath -> {
                        String oldName = oldFilePath.getFileName().toString().trim();
                        String newName = oldName;
                        for (int i = 0; i < oldNames.size(); i++) {
                            newName = newName.replace(oldNames.get(i).trim(), newNames.get(i).trim());
                        }
                        if (!newName.equals(oldName)) {
                            Path newFilePath = oldFilePath.resolveSibling(newName);
                            try {
                                Files.move(oldFilePath, newFilePath, StandardCopyOption.REPLACE_EXISTING);
                                System.out.println("Renamed file: " + oldName + " -> " + newName);
                                logMessage("Renamed file: " + oldName + " -> " + newName, logPath);
                                fileRenameCount.getAndIncrement();
                            } catch (IOException e) {
                                System.err.println("Failed to rename file: " + oldName);
                                logMessage("Failed to rename file: " + oldName, logPath);
                                e.printStackTrace();
                                fileRenameCountFail.getAndIncrement();
                            }
                        } else {
                            System.out.println("No rename rule found for file: " + oldName);
                            logMessage("No rename rule found for file: " + oldName, logPath);
                        }
                    });

            // 遍历目标文件夹及其子文件夹中的所有文件夹并重命名
            Files.walk(Paths.get(targetFolderPath))
                    .filter(Files::isDirectory)
                    .sorted(Comparator.comparingInt((Path p) -> p.getNameCount()).reversed()) // 从最深的子目录开始重命名
                    .forEach(oldDirPath -> {
                        String oldName = oldDirPath.getFileName().toString().trim();
                        String newName = oldName;
                        for (int i = 0; i < oldNames.size(); i++) {
                            newName = newName.replace(oldNames.get(i).trim(), newNames.get(i).trim());
                        }
                        if (!newName.equals(oldName)) {
                            Path newDirPath = oldDirPath.resolveSibling(newName);
                            try {
                                Files.move(oldDirPath, newDirPath, StandardCopyOption.REPLACE_EXISTING);
                                System.out.println("Renamed directory: " + oldName + " -> " + newName);
                                logMessage("Renamed directory: " + oldName + " -> " + newName, logPath);
                                floderRenameCount.getAndIncrement();
                            } catch (IOException e) {
                                System.err.println("Failed to rename directory: " + oldName);
                                logMessage("Failed to rename directory: " + oldName, logPath);
                                e.printStackTrace();
                                floderRenameCountFail.getAndIncrement();
                            }
                        } else {
                            System.out.println("No rename rule found for directory: " + oldName);
                            logMessage("No rename rule found for directory: " + oldName, logPath);
                        }
                    });
            System.out.println("file rename count:" + fileRenameCount.get());
            logMessage("file rename count:" + fileRenameCount.get(), logPath);
            System.out.println("directory rename count:" + floderRenameCount.get());
            logMessage("directory rename count:" + floderRenameCount.get(), logPath);
            System.out.println("file rename failed count:" + fileRenameCountFail.get());
            logMessage("file rename failed count:" + fileRenameCountFail.get(), logPath);
            System.out.println("directory rename failed count:" + floderRenameCountFail.get());
            logMessage("directory rename failed count:" + floderRenameCountFail.get(), logPath);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * 根据全路径替换全部文件名
     * 此处使用config.ini
     * @param args
     */
    public static void main(String[] args) {
        // 指定INI文件路径和目标文件夹路径
        String iniFilePath = "F:\\程序\\code\\file-rename\\src\\main\\resources\\config.ini";
        String targetFolderPath = "F:\\程序\\重命名";
        String logPath = "F:\\程序\\log.txt";

        // 文件名修改数量
        AtomicInteger fileRenameCount = new AtomicInteger();
        // 文件夹名修改数量
        AtomicInteger floderRenameCount = new AtomicInteger();
        // 文件名修改失败数量
        AtomicInteger fileRenameCountFail = new AtomicInteger();
        // 文件夹名修改失败数量
        AtomicInteger floderRenameCountFail = new AtomicInteger();
        try {
            // 读取INI文件内容
            Properties properties = new Properties();
            properties.load(new InputStreamReader(new FileInputStream(iniFilePath), StandardCharsets.UTF_8));

            // 获取旧文件名和新文件名列表
            String oldNamesStr = properties.getProperty("oldFileName");
            String newNamesStr = properties.getProperty("newFileName");

            List<String> oldNames = Arrays.asList(oldNamesStr.split(","));
            List<String> newNames = Arrays.asList(newNamesStr.split(","));

            // 确保两个列表长度相同
            if (oldNames.size() != newNames.size()) {
                throw new IllegalArgumentException("Old names and new names lists must have the same length.");
            }
            // 遍历目标文件夹及其子文件夹中的所有文件夹并重命名
            for (int i = 0; i < oldNames.size(); i++) {
                String oldName = oldNames.get(i).trim();
                String newName = newNames.get(i).trim();
                Path oldFilePath = Paths.get(oldName);
                Path newFilePath = Paths.get(newName);
                try {
                    Files.move(oldFilePath, newFilePath, StandardCopyOption.REPLACE_EXISTING);
                    logMessage("Renamed file: " + oldName + " -> " + newName, logPath);
                    fileRenameCount.getAndIncrement();
                } catch (IOException e) {
                    logMessage("Failed to rename file: " + oldName, logPath);
                    e.printStackTrace();
                    fileRenameCountFail.getAndIncrement();
                }
            }

            // 获取旧文件名和新文件名列表
            String oldDirectoryNamesStr = properties.getProperty("oldDirectoryFileName");
            String newDirectoryNamesStr = properties.getProperty("newDirectoryFileName");

            List<String> oldDirectoryNames = Arrays.asList(oldDirectoryNamesStr.split(","));
            List<String> newDirectoryNames = Arrays.asList(newDirectoryNamesStr.split(","));

            // 确保两个列表长度相同
            if (oldDirectoryNames.size() != newDirectoryNames.size()) {
                throw new IllegalArgumentException("Old names and new names lists must have the same length.");
            }

            // 遍历目标文件夹及其子文件夹中的所有文件夹并重命名
            for (int i = 0; i < oldDirectoryNames.size(); i++) {
                String oldDirectoryName = oldDirectoryNames.get(i);
                String newDirectoryName = newDirectoryNames.get(i);
                Path oldDirectoryPath = Paths.get(oldDirectoryName);
                Path newDirectoryPath = Paths.get(newDirectoryName);
                try {
                    Files.move(oldDirectoryPath, newDirectoryPath, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Renamed directory: " + oldDirectoryName + " -> " + newDirectoryName);
                    logMessage("Renamed directory: " + oldDirectoryName + " -> " + newDirectoryName, logPath);
                    floderRenameCount.getAndIncrement();
                } catch (IOException e) {
                    System.err.println("Failed to rename directory: " + oldDirectoryName);
                    logMessage("Failed to rename directory: " + oldDirectoryName, logPath);
                    e.printStackTrace();
                    floderRenameCountFail.getAndIncrement();
                }
            }
            System.out.println("file rename count:" + fileRenameCount.get());
            logMessage("file rename count:" + fileRenameCount.get(), logPath);
            System.out.println("directory rename count:" + floderRenameCount.get());
            logMessage("directory rename count:" + floderRenameCount.get(), logPath);
            System.out.println("file rename failed count:" + fileRenameCountFail.get());
            logMessage("file rename failed count:" + fileRenameCountFail.get(), logPath);
            System.out.println("directory rename failed count:" + floderRenameCountFail.get());
            logMessage("directory rename failed count:" + floderRenameCountFail.get(), logPath);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * 根据文件全路径更新文件名
     * 此处使用config.ini
     */
    @GetMapping("/renameFileByFileName")
    public void renameFileByFileName(){
        // 指定INI文件路径和目标文件夹路径
//        String iniFilePath = "F:\\程序\\code\\file-rename\\src\\main\\resources\\config.ini";
//        String logPath = "F:\\程序\\log.txt";

        // 文件名修改数量
        AtomicInteger fileRenameCount = new AtomicInteger();
        // 文件夹名修改数量
        AtomicInteger floderRenameCount = new AtomicInteger();
        // 文件名修改失败数量
        AtomicInteger fileRenameCountFail = new AtomicInteger();
        // 文件夹名修改失败数量
        AtomicInteger floderRenameCountFail = new AtomicInteger();
        try {
            // 读取INI文件内容
            Properties properties = new Properties();
            properties.load(new InputStreamReader(new FileInputStream(iniFilePath), StandardCharsets.UTF_8));

            // 获取旧文件名和新文件名列表
            String oldNamesStr = properties.getProperty("oldFileName");
            String newNamesStr = properties.getProperty("newFileName");

            List<String> oldNames = Arrays.asList(oldNamesStr.split(","));
            List<String> newNames = Arrays.asList(newNamesStr.split(","));

            // 确保两个列表长度相同
            if (oldNames.size() != newNames.size()) {
                throw new IllegalArgumentException("Old names and new names lists must have the same length.");
            }
            // 遍历目标文件夹及其子文件夹中的所有文件夹并重命名
            for (int i = 0; i < oldNames.size(); i++) {
                String oldName = oldNames.get(i).trim();
                String newName = newNames.get(i).trim();
                Path oldFilePath = Paths.get(oldName);
                Path newFilePath = Paths.get(newName);
                try {
                    Files.move(oldFilePath, newFilePath, StandardCopyOption.REPLACE_EXISTING);
                    logMessage("Renamed file: " + oldName + " -> " + newName, logPath);
                    fileRenameCount.getAndIncrement();
                } catch (IOException e) {
                    logMessage("Failed to rename file: " + oldName, logPath);
                    e.printStackTrace();
                    fileRenameCountFail.getAndIncrement();
                }
            }

            // 获取旧文件名和新文件名列表
            String oldDirectoryNamesStr = properties.getProperty("oldDirectoryFileName");
            String newDirectoryNamesStr = properties.getProperty("newDirectoryFileName");

            List<String> oldDirectoryNames = Arrays.asList(oldDirectoryNamesStr.split(","));
            List<String> newDirectoryNames = Arrays.asList(newDirectoryNamesStr.split(","));

            // 确保两个列表长度相同
            if (oldDirectoryNames.size() != newDirectoryNames.size()) {
                throw new IllegalArgumentException("Old names and new names lists must have the same length.");
            }

            // 遍历目标文件夹及其子文件夹中的所有文件夹并重命名
            for (int i = 0; i < oldDirectoryNames.size(); i++) {
                String oldDirectoryName = oldDirectoryNames.get(i);
                String newDirectoryName = newDirectoryNames.get(i);
                Path oldDirectoryPath = Paths.get(oldDirectoryName);
                Path newDirectoryPath = Paths.get(newDirectoryName);
                try {
                    Files.move(oldDirectoryPath, newDirectoryPath, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Renamed directory: " + oldDirectoryName + " -> " + newDirectoryName);
                    logMessage("Renamed directory: " + oldDirectoryName + " -> " + newDirectoryName, logPath);
                    floderRenameCount.getAndIncrement();
                } catch (IOException e) {
                    System.err.println("Failed to rename directory: " + oldDirectoryName);
                    logMessage("Failed to rename directory: " + oldDirectoryName, logPath);
                    e.printStackTrace();
                    floderRenameCountFail.getAndIncrement();
                }
            }
            System.out.println("file rename count:" + fileRenameCount.get());
            logMessage("file rename count:" + fileRenameCount.get(), logPath);
            System.out.println("directory rename count:" + floderRenameCount.get());
            logMessage("directory rename count:" + floderRenameCount.get(), logPath);
            System.out.println("file rename failed count:" + fileRenameCountFail.get());
            logMessage("file rename failed count:" + fileRenameCountFail.get(), logPath);
            System.out.println("directory rename failed count:" + floderRenameCountFail.get());
            logMessage("directory rename failed count:" + floderRenameCountFail.get(), logPath);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * 按照config.ini文件替换当前文件夹下文件夹以及文件的文件名
     * 此处使用config1.ini
     */
    @GetMapping("/rename")
    public void renameFile(){

        // 文件名修改数量
        AtomicInteger fileRenameCount = new AtomicInteger();
        // 文件夹名修改数量
        AtomicInteger floderRenameCount = new AtomicInteger();
        // 文件名修改失败数量
        AtomicInteger fileRenameCountFail = new AtomicInteger();
        // 文件夹名修改失败数量
        AtomicInteger floderRenameCountFail = new AtomicInteger();
        try {
            // 读取INI文件内容
            Properties properties = new Properties();
            properties.load(new FileInputStream(iniFilePath));

            // 获取旧文件名和新文件名列表
            String oldNamesStr = properties.getProperty("oldName");
            String newNamesStr = properties.getProperty("newName");

            List<String> oldNames = Arrays.asList(oldNamesStr.split(","));
            List<String> newNames = Arrays.asList(newNamesStr.split(","));

            // 确保两个列表长度相同
            if (oldNames.size() != newNames.size()) {
                throw new IllegalArgumentException("Old names and new names lists must have the same length.");
            }
            // 遍历目标文件夹及其子文件夹中的所有文件并重命名
            Files.walk(Paths.get(targetFolderPath))
                    .filter(Files::isRegularFile)
                    .forEach(oldFilePath -> {
                        String oldName = oldFilePath.getFileName().toString().trim();
                        String newName = oldName;
                        for (int i = 0; i < oldNames.size(); i++) {
                            newName = newName.replace(oldNames.get(i).trim(), newNames.get(i).trim());
                        }
                        if (!newName.equals(oldName)) {
                            Path newFilePath = oldFilePath.resolveSibling(newName);
                            try {
                                Files.move(oldFilePath, newFilePath, StandardCopyOption.REPLACE_EXISTING);
                                System.out.println("Renamed file: " + oldName + " -> " + newName);
                                logMessage("Renamed file: " + oldName + " -> " + newName, logPath);
                                fileRenameCount.getAndIncrement();
                            } catch (IOException e) {
                                System.err.println("Failed to rename file: " + oldName);
                                logMessage("Failed to rename file: " + oldName, logPath);
                                e.printStackTrace();
                                fileRenameCountFail.getAndIncrement();
                            }
                        } else {
                            System.out.println("No rename rule found for file: " + oldName);
                            logMessage("No rename rule found for file: " + oldName, logPath);
                        }
                    });

            // 遍历目标文件夹及其子文件夹中的所有文件夹并重命名
            Files.walk(Paths.get(targetFolderPath))
                    .filter(Files::isDirectory)
                    .sorted(Comparator.comparingInt((Path p) -> p.getNameCount()).reversed()) // 从最深的子目录开始重命名
                    .forEach(oldDirPath -> {
                        String oldName = oldDirPath.getFileName().toString().trim();
                        String newName = oldName;
                        for (int i = 0; i < oldNames.size(); i++) {
                            newName = newName.replace(oldNames.get(i).trim(), newNames.get(i).trim());
                        }
                        if (!newName.equals(oldName)) {
                            Path newDirPath = oldDirPath.resolveSibling(newName);
                            try {
                                Files.move(oldDirPath, newDirPath, StandardCopyOption.REPLACE_EXISTING);
                                System.out.println("Renamed directory: " + oldName + " -> " + newName);
                                logMessage("Renamed directory: " + oldName + " -> " + newName, logPath);
                                floderRenameCount.getAndIncrement();
                            } catch (IOException e) {
                                System.err.println("Failed to rename directory: " + oldName);
                                logMessage("Failed to rename directory: " + oldName, logPath);
                                e.printStackTrace();
                                floderRenameCountFail.getAndIncrement();
                            }
                        } else {
                            System.out.println("No rename rule found for directory: " + oldName);
                            logMessage("No rename rule found for directory: " + oldName, logPath);
                        }
                    });
            System.out.println("file rename count:" + fileRenameCount.get());
            logMessage("file rename count:" + fileRenameCount.get(), logPath);
            System.out.println("directory rename count:" + floderRenameCount.get());
            logMessage("directory rename count:" + floderRenameCount.get(), logPath);
            System.out.println("file rename failed count:" + fileRenameCountFail.get());
            logMessage("file rename failed count:" + fileRenameCountFail.get(), logPath);
            System.out.println("directory rename failed count:" + floderRenameCountFail.get());
            logMessage("directory rename failed count:" + floderRenameCountFail.get(), logPath);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * 写日志
     * @param message
     * @param logFilePath
     */
    private static void logMessage(String message, String logFilePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath, true))) {
            writer.write(message);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
