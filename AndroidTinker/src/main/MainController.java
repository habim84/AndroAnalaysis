package main;

import core.apktool.ApkToolProcess;
import core.configmanager.ConfigManager;
import core.configmanager.ConfigModel;
import core.filetree.FileItem;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import tinkerdialogs.apktooldirdialog.ApkToolDirDialogController;

import java.io.*;
import java.util.Scanner;

public class MainController {

    private static final String APKTOOL_DIALOG_TITLE = "Browse Apktool Directory";

    private Stage stage;
    private String apkToolDir;
    private ConfigModel configModel;
    private ConfigManager configManager;
    private StringBuilder logData;
    private SingleSelectionModel<Tab> selectionModel;

    @FXML
    Text StatusTag;
    @FXML
    ProgressIndicator StatusProgress;
    @FXML
    Button ClearLogButton;
    @FXML
    TextArea LogTextArea;
    @FXML
    MenuBar menuBar;
    @FXML
    TreeView<FileItem> FolderTreeView;
    @FXML
    TabPane CodeTabPane;
    @FXML
    SplitPane HorizontalSplit;
    @FXML
    SplitPane VerticalSplit;

    @FXML
    public void initialize() {
        this.configManager = new ConfigManager();
        this.configModel = this.configManager.getConfigObject();
        this.logData = new StringBuilder();

        CodeTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.SELECTED_TAB);

        this.selectionModel = CodeTabPane.getSelectionModel();

        LogTextArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                LogTextArea.setScrollTop(Double.MAX_VALUE);
            }
        });

        FolderTreeView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    TreeItem<FileItem> selectedFile = FolderTreeView.getSelectionModel().getSelectedItem();
                    if (selectedFile.getValue().isFile()) {
                        CodeArea code = new CodeArea();
                        code.setParagraphGraphicFactory(LineNumberFactory.get(code));
                        File codeFile = new File(selectedFile.getValue().getFileDirectory());
                        String codeStr = readTextFromFile(codeFile);
                        code.replaceText(codeStr);
                        Tab tab = new Tab(selectedFile.getValue().getFileName(), code);
                        tab.setClosable(true);
                        CodeTabPane.getTabs().add(tab);
                        selectionModel.select(tab);
                    }
                }
                else if (event.getButton() == MouseButton.SECONDARY)
                {
                    logPrint("Right clicking!");
                }
            }
        });
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void onChangeApktoolDir(ActionEvent event) {
        this.openApktoolDirDialog();
    }

    public void openApktoolDirDialog() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass()
                    .getResource("../tinkerdialogs/apktooldirdialog/ApkToolDirDialog.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            Stage apktoolDialogStage = new Stage();
            ApkToolDirDialogController dirDialogController =
                    (ApkToolDirDialogController) fxmlLoader.getController();
            apktoolDialogStage.setTitle(APKTOOL_DIALOG_TITLE);
            apktoolDialogStage.setAlwaysOnTop(true);
            apktoolDialogStage.initOwner(this.stage);
            apktoolDialogStage.initModality(Modality.WINDOW_MODAL);
            dirDialogController.setStage(apktoolDialogStage);
            dirDialogController.setController(this);
            apktoolDialogStage.setScene(new Scene(root, 640, 143));
            apktoolDialogStage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Somehow the dialog won't spawn?");
        }
    }

    public void setConfiguration() {
        if (this.configModel.getApkToolDirectory().length() > 0) {
            this.apkToolDir = this.configModel.getApkToolDirectory();
        } else {
            this.apkToolDir = "";
            this.openApktoolDirDialog();
        }
    }


    public void setApkToolDir(String apkToolDir) {
        this.apkToolDir = apkToolDir;
    }


    @FXML
    private void onOpenApkMenu(final ActionEvent event) {
        System.out.println("Opening apk menu...");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open APK File");
        File apkFile = fileChooser.showOpenDialog(stage);
        if (apkFile != null) {
            System.out.println("Found a file");
            System.out.println("File: " + apkFile.getName());
            if (apkFile.getParentFile().getName().length() > 0) {
                ApkToolProcess apkToolProcess = new ApkToolProcess(apkFile.getAbsolutePath(),
                        this.apkToolDir,
                        apkFile.getParentFile().getAbsolutePath(),
                        this);
                new Thread(apkToolProcess::unpackApkTool).start();

            }
        } else {
            System.out.println("Did not pick a file.");
            this.logPrint("Load an APK file to unpack and view smali files.");
        }
    }

    public void setParent(TreeItem<FileItem> item) {
        System.out.println("Setting new tree root");
        FolderTreeView.setRoot(item);
    }

    @FXML
    private void onRefreshMenu(final ActionEvent event) {
        System.out.println("Refreshing whole window...");
        this.logPrint("Refreshing whole window...");
    }

    @FXML
    private void onExitMenu(final ActionEvent event) {
        this.exitProgram();
    }


    public void checkApktoolDir() {
        if (this.apkToolDir.length() == 0) {
            this.exitProgram();
        }
    }

    private void exitProgram() {
        System.out.println("Saving data...");
        this.configModel.setApkToolDirectory(this.apkToolDir);
        this.configManager.setConfigObject(this.configModel);
        this.configManager.writeConfig();
        System.out.println("Exit program...");
        System.exit(0);
    }

    public void changeStatus(String statusText, boolean loadFlag) {
        StatusTag.setText(statusText);
        StatusProgress.setVisible(loadFlag);
    }

    public void logPrint(String logText) {
        this.logData.append(logText).append("\n");
        LogTextArea.setText(this.logData.toString());
        LogTextArea.appendText("");
    }

    @FXML
    public void clearLogPrint(ActionEvent event) {
        LogTextArea.setText("");
    }

    private String readTextFromFile(File codeFile) {
        StringBuilder code = new StringBuilder();
        try {
            Scanner sc = new Scanner(codeFile);
            while (sc.hasNextLine()) {
                code.append(sc.nextLine());
                code.append("\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return code.toString();
    }

}
