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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.chat.client.ClientThread;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Server implements Runnable {
    private int port;
    private ServerSocket socket;
    private ArrayList<Socket> clients;
    private ArrayList<ClientThread> clientThreads;
    public ObservableList<String> serverLog;
    public ObservableList<String> clientNames;

    // one arg constructor to initialize everything
    public Server(int port) throws IOException {
        this.port = port;
        serverLog = FXCollections.observableArrayList();
        clientNames = FXCollections.observableArrayList();
        clients = new ArrayList<Socket>();
        clientThreads = new ArrayList<ClientThread>();
        socket = new ServerSocket(port);
    }

    public void startServer() {
        try {
            socket = new ServerSocket(this.port);
            serverLog = FXCollections.observableArrayList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            while (true) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        serverLog.add("Waiting for connection...");
                    }
                });

                final Socket clientSocket = socket.accept();

                clients.add(clientSocket);

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        serverLog.add("Client "
                                + clientSocket.getRemoteSocketAddress()
                                + " connected!");
                    }
                });

                ClientThread clientThreadHolderClass = new ClientThread(
                        clientSocket, this);
                Thread clientThread = new Thread(clientThreadHolderClass);

                clientThreads.add(clientThreadHolderClass);
                clientThread.setDaemon(true);
                clientThread.start();

                ServerApplication.threads.add(clientThread);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // when client disconnects remove everything for that specific client
    public void clientDisconnected(ClientThread client) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                serverLog.add(client.getClientName() + " "
                        + client.getClientSocket().getRemoteSocketAddress()
                        + " disconnected!");

                clients.remove(clientThreads.indexOf(client));
                clientNames.remove(clientThreads.indexOf(client));
                clientThreads.remove(clientThreads.indexOf(client));
            }
        });

    }

    // writes to all clients available
    public void writeToAllSockets(String input) {
        for (ClientThread clientThread : clientThreads) {
            clientThread.writeToServer(input);
        }
    }

}
