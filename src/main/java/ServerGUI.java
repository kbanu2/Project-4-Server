import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.Serializable;
import java.util.function.Consumer;

public class ServerGUI extends Application {
    private TextField portTextField = new TextField();
    private Button createServer = new Button("Create Server");
    private ListView<String> serverLogs = new ListView<>();
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Create Server");
        primaryStage.setScene(createSetUpScene());

        Consumer<Serializable> callback = data -> {
            Platform.runLater(() -> serverLogs.getItems().add(data.toString()));
        };

        createServer.setOnAction(event -> {
            primaryStage.setTitle("Server Logs");

            try{
                //ToDo: Implement code to create server thread with input port
                Server server = new Server(callback,1000);
            }
            catch (Exception e){
                System.out.println("Could not start server");
                e.printStackTrace();
            }

            primaryStage.setScene(createServerScene());
        });

        primaryStage.show();
    }

    public Scene createSetUpScene(){
        HBox hBox1 = new HBox(new Label("Enter Server Port: "), portTextField);
        HBox hBox2 = new HBox(new Label("Create Server: " ), createServer);
        VBox vBox = new VBox(new Label("Create New Server"), hBox1, hBox2);

        hBox1.setAlignment(Pos.CENTER);
        hBox1.setSpacing(10);
        hBox2.setAlignment(Pos.CENTER);
        hBox2.setSpacing(10);
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(10);
        return new Scene(vBox, 500, 500);
    }

    public Scene createServerScene(){
        HBox hBox = new HBox(serverLogs);
        VBox vBox = new VBox(hBox);

        vBox.setAlignment(Pos.CENTER);
        hBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(5);
        hBox.setSpacing(5);
        serverLogs.setPrefHeight(500);
        return new Scene(vBox, 750, 750);
    }
}
