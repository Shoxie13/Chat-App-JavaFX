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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Client implements Runnable {
    private Socket clientSocket;
    private BufferedReader serverToClientReader;
    private PrintWriter clientToServerWriter;
    private String name;
    public ObservableList<String> chatLog;

    // default constructor
    public Client(String name) {
        this.name = name;
    }

    // three arg constructor
    public Client(String hostName, int portNumber, String name) throws UnknownHostException, IOException {
        clientSocket = new Socket(hostName, portNumber);

        serverToClientReader = new BufferedReader(new InputStreamReader(
                clientSocket.getInputStream()));
        clientToServerWriter = new PrintWriter(
                clientSocket.getOutputStream(), true);
        chatLog = FXCollections.observableArrayList();

        this.name = name;
        clientToServerWriter.println(name);
    }

    // writes to the server
    public void writeToServer(String input) {
        clientToServerWriter.println(name + " : " + input);
    }

    public void run() {
        // loops so it can update the server log
        while (true) {
            try {
                final String inputFromServer = serverToClientReader.readLine();
                Platform.runLater(new Runnable() {
                    public void run() {
                        chatLog.add(inputFromServer);
                    }
                });

            } catch (SocketException e) {
                Platform.runLater(new Runnable() {
                    public void run() {
                        chatLog.add("Server Error! Please re-connect...");
                    }
                });
                break;
            } catch (

            IOException e) {
                e.printStackTrace();
            }
        }
    }
}
