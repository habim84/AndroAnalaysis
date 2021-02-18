package core.filetree;

import java.io.File;

public class FileItem {

    private String fileName;
    private String fileDirectory;
    private boolean isFile;

    public FileItem(File file) {
        this.fileName = file.getName();
        this.fileDirectory = file.getAbsolutePath();
        this.isFile = !file.isDirectory();
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileDirectory() {
        return fileDirectory;
    }

    public void setFileDirectory(String fileDirectory) {
        this.fileDirectory = fileDirectory;
    }

    public boolean isFile() {
        return isFile;
    }

    public void setFile(boolean file) {
        isFile = file;
    }

    @Override
    public String toString() {
        return this.fileName;
    }
}
