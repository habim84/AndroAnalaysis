package core.configmanager;

public class ConfigModel {
    private String apkToolDirectory;

    public ConfigModel()
    {
        this.apkToolDirectory = "";
    }


    public ConfigModel(String apkToolDirectory) {
        this.apkToolDirectory = apkToolDirectory;
    }

    public String getApkToolDirectory() {
        return apkToolDirectory;
    }

    public void setApkToolDirectory(String apkToolDirectory) {
        this.apkToolDirectory = apkToolDirectory;
    }
}
