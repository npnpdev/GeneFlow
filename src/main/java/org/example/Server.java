package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.lang.Thread;

public class Server {
    private static final int PORT = 12345;

    private static void log(String message) {
        String timestamp = String.format("%tF %<tT.%<tL", new Date());
        System.out.println(timestamp + " [" + Thread.currentThread().getName() + "] " + message);
    }

    public static void main(String[] args) {
        log("Uruchamianie serwera...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            log("Serwer uruchomiony. Nasłuchiwanie na porcie: " + PORT);

            while (true) {
                log("Oczekiwanie na połączenie klienta...");
                try {
                    Socket clientSocket = serverSocket.accept();
                    String clientInfo = clientSocket.getRemoteSocketAddress().toString();
                    log("Zaakceptowano połączenie od: " + clientInfo);


                    ClientHandler clientHandler = new ClientHandler(clientSocket);
                    Thread handlerThread = new Thread(clientHandler);
                    handlerThread.setName("ClientHandler-" + clientSocket.getPort());
                    handlerThread.start();

                } catch (IOException e) {
                    log("Błąd podczas akceptowania połączenia: " + e.getMessage());
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            log("Krytyczny błąd serwera. Nie można nasłuchiwać na porcie " + PORT + ": " + e.getMessage());
            e.printStackTrace();
        } finally {
            log("Serwer kończy działanie.");
        }
    }
}