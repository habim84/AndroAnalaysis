package core.apktool;

import javafx.application.Platform;
import main.MainController;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class DexHandler {

    private static final String DEX2JAR_TOOL = "C:/Users/Harith Borhan/Downloads/dex2jar/dex2jar-2.0/d2j-dex2jar.bat";
    private static final String JAR_FILENAME = "classes-dex2jar";
    private String apkDir;
    private String unpackDir;
    private ZipFile jarFile;
    private MainController controller;

    public DexHandler(String apkDir, String unpackDir, MainController controller) {
        this.apkDir = apkDir;
        this.unpackDir = unpackDir;
        this.controller = controller;
    }

    public void unpackApk()
    {
        try {
            ZipFile apkFile = new ZipFile(this.apkDir);
            if (apkFile.isValidZipFile())
            {
                this.controller.logPrint("This apk is a valid zip file");
                List fileList = apkFile.getFileHeaders();
                int counter = 1;
                for (Object o : fileList) {
                    FileHeader f = (FileHeader) o;
                    String fileName = f.getFileName();
                    if (fileName.contains(".dex")) {
                        this.controller.logPrint("File Name: " + fileName);
                        System.out.println("File Name: " + fileName);
                        apkFile.extractFile(fileName, this.unpackDir+"/unzip"+Integer.toString(counter));
                        convertJar(fileName, counter);
                        counter++;
                    }
                }
            } else
            {
                this.controller.logPrint("This isnt a valid zip file");
            }
        } catch (ZipException e)
        {
            e.printStackTrace();
            this.controller.logPrint("Cannot even open it");
        }
    }

    public void convertJar(String filename, int counter)
    {
        try {
            // Converting dex to jar isn't working
            Runtime rt = Runtime.getRuntime();
            String command  = "d2j-dex2jar .\\"+filename;
            System.out.println("Command: "+ command);
            this.controller.logPrint("Command: "+command);
            System.out.println("Working Directory: "+this.unpackDir+"\\unzip"+Integer.toString(counter));
            this.controller.logPrint("Current Working Directory: "+this.unpackDir+"\\unzip"+Integer.toString(counter));
            this.controller.logPrint("Convert "+" to JAR file...");
            Process pr = rt.exec(command,
                    null,
                    new File(this.unpackDir+"\\unzip"+Integer.toString(counter)));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
                    String line;
                    try {
                        while((line = input.readLine()) != null)
                        {
                            String newLine = line;
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    controller.changeStatus(newLine, true);
                                    controller.logPrint(newLine);
                                }
                            });
                            System.out.println(line);
                        }
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                // Do something after converting the dex to jar
                                unzipJar(counter);
                            }
                        });
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void unzipJar(int counter)
    {
        try {
            jarFile = new ZipFile(this.apkDir+"/unzip"+Integer.toString(counter)+"/"+JAR_FILENAME);
            if (jarFile.isValidZipFile())
            {
                this.controller.logPrint("This jar is a valid zip file.");
                jarFile.extractAll(this.unpackDir+"/unzip/jar_unzip");
            }
        } catch(ZipException e)
        {
            e.printStackTrace();
            this.controller.logPrint("Could not open JAR file or the JAR file specified is missing.");
            System.out.println("Could not open JAR file or the JAR file specified is missing");
        }
    }

}
