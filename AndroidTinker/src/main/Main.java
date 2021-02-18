package main;

import core.configmanager.ConfigModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = (Parent) fxmlLoader.load();
        //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        ((MainController) fxmlLoader.getController()).setStage(primaryStage);
        primaryStage.setTitle("Android Tinker");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.setX(bounds.getMinX());
        primaryStage.setY(bounds.getMinY());
        primaryStage.setWidth(bounds.getWidth());
        primaryStage.setHeight(bounds.getHeight());
        primaryStage.show();
        ((MainController) fxmlLoader.getController()).setConfiguration();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
