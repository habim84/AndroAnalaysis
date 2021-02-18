package core.apktool;

import core.filetree.FileItem;
import javafx.application.Platform;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import main.MainController;
import java.io.*;
import java.util.ArrayList;

public class ApkToolProcess {

    private static final String APKTOOL_MAIN = "brut.apktool.Main";
    private static final String KEYTEXT = "Press any key to continue . . . ";
    private String apktoolDir;
    private String apkDir;
    private String unpackDir;
    private String finalUnpackDir;
    private MainController controller;

    public ApkToolProcess(String apkDir, String apkToolDir, String apkParentDir, MainController controller) {
        this.apkDir = apkDir;
        this.apktoolDir = apkToolDir;
        this.unpackDir = apkParentDir;
        this.controller = controller;
    }

    public void unpackApkTool() {
        try {
            Runtime rt = Runtime.getRuntime();
            String command = "java -jar \"" + this.apktoolDir + "\" d \"" + this.apkDir + "\"";
            System.out.println("Command: " + command);
            this.controller.logPrint("Command: " + command);
            System.out.println("Working Directory: " + this.unpackDir);
            this.controller.logPrint("Current Working Directory: " + this.unpackDir);
            this.controller.logPrint("Commencing unpacking APK file...");
            MainController c = this.controller;
            Process pr = rt.exec(command,
                    null,
                    new File(this.unpackDir));
            new Thread(new Runnable() {

                public void run() {
                    BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
                    String line;
                    try {
                        while ((line = input.readLine()) != null) {
                            String newLine = line;
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    c.changeStatus(newLine, true);
                                    c.logPrint(newLine);
                                }
                            });
                            System.out.println(line);
                        }
                        System.out.println("APK Directory: " + apkDir);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                finalUnpackDir = apkDir.substring(0, apkDir.length() - 4);
                                File rootFolder = new File(finalUnpackDir);
                                TreeItem<FileItem> root = updateTreeStructure(rootFolder);
                                DexHandler dexHandler = new DexHandler(apkDir, unpackDir, controller);
                                dexHandler.unpackApk();
                                c.changeStatus("Completed Unpacking APK!", false);
                                c.logPrint("[SUCCESS] Completed Unpacking APK!");
                                c.setParent(root);
                                System.out.println("Final unpacked Directory: " + finalUnpackDir);
                                System.out.println("Everything worked!");
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            pr.waitFor();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    public void packApkTool(String appPath, String outPath) {
        try {
            Process process = new ProcessBuilder(this.apktoolDir, "b", "-o", outPath, appPath).start();
            InputStream inputStream = process.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
            //
        } catch (IOException e) {
            System.out.println("Could not find the program.");
            e.printStackTrace();
        }
    }

    public String getApkDir() {
        return apkDir;
    }

    public void setApkDir(String apkDir) {
        this.apkDir = apkDir;
    }

    private TreeItem<FileItem> updateTreeStructure(final File rootFolder) {
        FileItem root = new FileItem(rootFolder);
        TreeItem<FileItem> rootItem = new TreeItem<>(root);
        Image folderImage = new Image(String.valueOf(getClass().getResource("resource/folder-icon.png")));
        ImageView rootImage = new ImageView(folderImage);
        rootItem.setGraphic(rootImage);
        File[] fileList = rootFolder.listFiles();
        System.out.println("Checking this directory: " + rootFolder.getName());
        if (fileList != null && fileList.length < 100) {
            System.out.println("Folder is not empty!");
            for (final File fileEntry : fileList) {
                if (fileEntry.isDirectory()) {
                    this.controller.logPrint("Found directory " + fileEntry.getName());
                    TreeItem<FileItem> dirItem = updateTreeStructure(fileEntry);
                    rootItem.getChildren().add(dirItem);
                } else {
                    ImageView fileImage = new ImageView(new Image(String.valueOf(getClass().getResource("resource/file-icon.png"))));
                    this.controller.logPrint("Found file " + fileEntry.getName());
                    System.out.println("Found file: " + fileEntry.getName());
                    FileItem file = new FileItem(fileEntry);
                    TreeItem<FileItem> fileItem = new TreeItem<>(file);
                    fileItem.setGraphic(fileImage);
                    rootItem.getChildren().add(fileItem);
                }
            }
        }
        return rootItem;
    }

}
