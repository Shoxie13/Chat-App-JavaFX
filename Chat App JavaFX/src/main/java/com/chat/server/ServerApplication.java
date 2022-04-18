/**********************************************
Workshop 9
Course: JAC444 - Semester 4
Last Name: Abdi
First Name: Tareq
ID: 123809196
This assignment represents my own work in accordance with Seneca Academic Policy.
Signature - TA
Date: 03/04/2022
**********************************************/

package com.chat.server;

import java.io.IOException;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ServerApplication extends Application {
    public static ArrayList<Thread> threads;

    @Override
    public void start(Stage primaryStage) throws Exception {
        threads = new ArrayList<Thread>();
        primaryStage.setTitle("Multi-Thread Chat Server");
        primaryStage.setResizable(false);
        primaryStage.setScene(generateUIPort(primaryStage));
        primaryStage.show();
    }

    // makes the port User Interface to determine on which port the server will run
    public Scene generateUIPort(Stage primaryStage) {
        GridPane rootPane = new GridPane();
        rootPane.setPadding(new Insets(20));
        rootPane.setVgap(10);
        rootPane.setHgap(10);
        rootPane.setAlignment(Pos.CENTER);

        Text portText = new Text("Port Number:");
        Label errorLabel = new Label();

        TextField portTextField = new TextField();
        portTextField.setPrefWidth(200.0);

        Button portApprovalButton = new Button("Done");
        portApprovalButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                // create server that will rund on a thread
                try {
                    Server server = new Server(Integer.parseInt(portTextField
                            .getText()));
                    Thread serverThread = (new Thread(server));
                    serverThread.setName("Server Thread");
                    serverThread.setDaemon(true);
                    serverThread.start();
                    threads.add(serverThread);

                    // switch the views
                    primaryStage.hide();
                    primaryStage.setScene(generateUIServer(server));
                    primaryStage.show();
                } catch (IllegalArgumentException e) {
                    portTextField.setStyle("-fx-text-box-border: #B22222; -fx-focus-color: #B22222;");
                    errorLabel.setTextFill(Color.RED);
                    errorLabel.setText("Invalid port! Please try again...");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        rootPane.add(portText, 0, 0);
        rootPane.add(portTextField, 1, 0);
        rootPane.add(portApprovalButton, 0, 1);
        rootPane.add(errorLabel, 1, 1);

        return new Scene(rootPane, 300, 100);
    }

    // makes the User Interface of the server with the logs
    public Scene generateUIServer(Server server) {
        // create the root pane and set properties
        GridPane rootPane = new GridPane();
        rootPane.setAlignment(Pos.CENTER);
        rootPane.setPadding(new Insets(20));
        rootPane.setHgap(10);
        rootPane.setVgap(10);

        // create the server log ListView
        Label logLabel = new Label("Server Log");
        ListView<String> logView = new ListView<String>();
        ObservableList<String> logList = server.serverLog;
        logView.setItems(logList);

        // create the client list ListView
        Label clientLabel = new Label("Connected Clients");
        ListView<String> clientView = new ListView<String>();
        ObservableList<String> clientList = server.clientNames;
        clientView.setItems(clientList);

        rootPane.add(logLabel, 0, 0);
        rootPane.add(logView, 0, 1);
        rootPane.add(clientLabel, 1, 0);
        rootPane.add(clientView, 1, 1);

        return new Scene(rootPane);
    }

    public static void main(String[] args) {
        launch();
    }
}
