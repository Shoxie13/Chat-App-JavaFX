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

package com.chat.client;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;

import javafx.application.Application;
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
import javafx.stage.Stage;

public class ClientApplication extends Application {
    private ArrayList<Thread> threads;

    @Override
    public void stop() throws Exception {
        super.stop();
        for (Thread thread : threads) {
            thread.interrupt();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        threads = new ArrayList<Thread>();
        primaryStage.setTitle("Chat Client");
        primaryStage.setResizable(false);
        primaryStage.setScene(makeInitScene(primaryStage));
        primaryStage.show();
    }

    public Scene makeInitScene(Stage primaryStage) {
        GridPane rootPane = new GridPane();
        rootPane.setPadding(new Insets(20));
        rootPane.setVgap(10);
        rootPane.setHgap(10);
        rootPane.setAlignment(Pos.CENTER);

        // make the text fields and set properties
        TextField nameField = new TextField();
        TextField hostNameField = new TextField();
        TextField portNumberField = new TextField();

        nameField.setPrefWidth(200.0);
        hostNameField.setPrefWidth(200.0);
        portNumberField.setPrefWidth(200.0);

        // make the labels and set properties
        Label nameLabel = new Label("Name:");
        Label hostNameLabel = new Label("Host Name:");
        Label portNumberLabel = new Label("Port Number:");
        Label errorLabel = new Label();

        // make the button and its handler
        Button submitClientInfoButton = new Button("Done");
        submitClientInfoButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent Event) {
                // instantiate the client class and start it's thread
                Client client;

                try {
                    if (nameField.getText().matches("^[A-Z][a-z]*$") && nameField.getText().length() >= 3) {

                        client = new Client(hostNameField.getText(), Integer
                                .parseInt(portNumberField.getText()),
                                nameField
                                        .getText());
                        Thread clientThread = new Thread(client);

                        clientThread.setDaemon(true);
                        clientThread.start();
                        threads.add(clientThread);

                        nameField.setStyle("");
                        // change the scene of the primaryStage
                        primaryStage.close();
                        primaryStage.setScene(makeChatUI(client));
                        primaryStage.show();
                    } else {
                        nameField.setStyle("-fx-text-box-border: #B22222; -fx-focus-color: #B22222;");
                        errorLabel.setTextFill(Color.RED);
                        errorLabel.setText("Invalid name! Please try again...");
                    }
                } catch (ConnectException e) {
                    hostNameField.setStyle("-fx-text-box-border: #B22222; -fx-focus-color: #B22222;");
                    errorLabel.setTextFill(Color.RED);
                    errorLabel.setText("Invalid Host! Please try again...");
                } catch (NumberFormatException | IOException e) {
                    portNumberField.setStyle("-fx-text-box-border: #B22222; -fx-focus-color: #B22222;");
                    errorLabel.setTextFill(Color.RED);
                    errorLabel.setText("Invalid Port! Please try again...");
                }
            }
        });

        // add the components to the root pane arguments
        rootPane.add(nameLabel, 0, 0);
        rootPane.add(nameField, 1, 0);
        rootPane.add(hostNameLabel, 0, 1);
        rootPane.add(hostNameField, 1, 1);
        rootPane.add(portNumberLabel, 0, 2);
        rootPane.add(portNumberField, 1, 2);
        rootPane.add(submitClientInfoButton, 0, 3, 2, 1);
        rootPane.add(errorLabel, 1, 3);

        return new Scene(rootPane, 400, 200);
    }

    public Scene makeChatUI(Client client) {
        // make the root pane and set properties
        GridPane rootPane = new GridPane();
        rootPane.setPadding(new Insets(20));
        rootPane.setAlignment(Pos.CENTER);
        rootPane.setHgap(10);
        rootPane.setVgap(10);

        // make the button pane and set properties
        GridPane buttonPane = new GridPane();
        buttonPane.setAlignment(Pos.CENTER);
        buttonPane.setHgap(20);

        Label errorLabel = new Label();
        errorLabel.setAlignment(Pos.CENTER);

        // creates list view for the chatlog
        ListView<String> chatListView = new ListView<String>();
        chatListView.setItems(client.chatLog);

        // makes a chat box and filters the send message
        TextField chatTextField = new TextField();
        Button sendButton = new Button("Send");
        sendButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!(chatTextField.getText().trim().isEmpty())) {
                    chatTextField.setStyle("");
                    client.writeToServer(chatTextField.getText().trim());
                    chatTextField.clear();
                    errorLabel.setText("");
                } else {
                    chatTextField.setStyle("-fx-text-box-border: #B22222; -fx-focus-color: #B22222;");
                    errorLabel.setTextFill(Color.RED);
                    errorLabel.setText("You can't send empty message");
                }
            }
        });

        Button clearButton = new Button("Clear");
        clearButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                chatTextField.clear();
                chatTextField.setStyle("");
                errorLabel.setText("");
            }
        });

        // add the components to the button pane
        buttonPane.add(sendButton, 0, 0);
        buttonPane.add(clearButton, 1, 0);

        // add the components to the root pane
        rootPane.add(chatListView, 0, 0);
        rootPane.add(chatTextField, 0, 1);
        rootPane.add(errorLabel, 0, 2);
        rootPane.add(buttonPane, 0, 3);

        return new Scene(rootPane);
    }

    public static void main(String[] args) {
        launch();
    }
}
