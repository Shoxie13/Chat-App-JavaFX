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

import com.chat.server.Server;

import javafx.application.Platform;

public class ClientThread implements Runnable {
    private Socket clientSocket;
    private Server baseServer;
    private BufferedReader incomingMessageReader;
    private PrintWriter outgoingMessageWriter;
    private String clientName;

    // two arg constructor for clientthread
    public ClientThread(Socket clientSocket, Server baseServer) {
        this.setClientSocket(clientSocket);
        this.baseServer = baseServer;

        try {
            // reads all incoming messages from the server
            incomingMessageReader = new BufferedReader(new InputStreamReader(
                    clientSocket.getInputStream()));

            // writes outgoing messages
            outgoingMessageWriter = new PrintWriter(
                    clientSocket.getOutputStream(), true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // overloading run method from Runnable
    public void run() {
        try {
            this.clientName = getClientNameFromNetwork();

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    baseServer.clientNames.add(clientName + " - " + clientSocket.getRemoteSocketAddress());
                }
            });

            String inputToServer;
            while (true) {
                inputToServer = incomingMessageReader.readLine();
                baseServer.writeToAllSockets(inputToServer);
            }

        } catch (SocketException e) {
            baseServer.clientDisconnected(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // writes messages to server
    public void writeToServer(String input) {
        outgoingMessageWriter.println(input);
    }

    // gets the client name from network
    public String getClientNameFromNetwork() throws IOException {
        return incomingMessageReader.readLine();
    }

    // gets client name
    public String getClientName() {
        return this.clientName;
    }

    // gets the client socket
    public Socket getClientSocket() {
        return clientSocket;
    }

    // sets the client socket
    void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }
}
