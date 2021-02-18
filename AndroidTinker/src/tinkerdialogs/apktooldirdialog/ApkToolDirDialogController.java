package tinkerdialogs.apktooldirdialog;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.MainController;

import java.io.File;

public class ApkToolDirDialogController {

    private String apkToolDirectory;
    private Stage stage;
    private MainController parentController;
    @FXML TextField ApkDirTextfield;

    public void setStage(Stage stage)
    {
        this.stage = stage;
    }

    public void setController(MainController mainController) { this.parentController = mainController; }

    @FXML
    private void onBrowseApkTool(final ActionEvent event)
    {
        System.out.println("Browsing apktool directory...");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Browse Apktool Program");
        File apkToolFile = fileChooser.showOpenDialog(stage);
        if (apkToolFile != null)
        {
            System.out.println("Found file: "+apkToolFile.getName());
            this.parentController.logPrint("[SUCCESS] Found file: "+apkToolFile.getName());
            System.out.println("Directory: "+ apkToolFile.getAbsolutePath());
            this.parentController.logPrint("[SUCCESS] Directory: "+apkToolFile.getAbsolutePath());
            this.apkToolDirectory = apkToolFile.getAbsolutePath();
            ApkDirTextfield.setText(this.apkToolDirectory);
        } else {
            System.out.println("Did not pick the program from file picker");
            this.parentController.logPrint("[ERROR] Failed to fetch apktool.jar path!");
        }
    }

    @FXML
    private void onApkToolDirConfirm(final ActionEvent event)
    {
        System.out.println("Found apktool directory. Setting up environment.");
        this.parentController.logPrint("\nSetting new apktool.jar as default apk unpacker...");
        this.parentController.setApkToolDir(this.apkToolDirectory);
        this.stage.close();
    }

    @FXML
    private void onApkToolDirCancel(final ActionEvent event)
    {
        System.out.println("Cancelling apktool directory setter");
        this.parentController.logPrint("\nCancelling apktool.jar path setting...");
        this.parentController.checkApktoolDir();
        this.stage.close();
    }

    @FXML
    public void initialize() {
        //this.stage = (Stage) this.ApkDirTextfield.getScene().getWindow();
        this.apkToolDirectory = "";
    }
}
