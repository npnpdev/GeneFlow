package org.example;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.List;
import java.util.Set; // Added for Set<Person>

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private static final int THREADS_TO_ALLOCATE = Main.DefaultThreadNumber;
    private static final String EXPECTED_REQUEST = "GET_THREAD_COUNT";

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    private static void log(String message) {
        String timestamp = String.format("%tF %<tT.%<tL", new Date());
        System.out.println(timestamp + " [" + Thread.currentThread().getName() + "] " + message);
    }

    @Override
    public void run() {
        String clientInfo = clientSocket.getRemoteSocketAddress().toString();
        log("Rozpoczynanie obsługi klienta: " + clientInfo);

        try (Socket socket = this.clientSocket;
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream()))
        {
            // Odbiór zapytania od klienta
            log("Oczekiwanie na zapytanie od klienta: " + clientInfo);
            Object receivedRequest = ois.readObject();
            log("Odebrano obiekt od klienta: " + clientInfo + " -> " + receivedRequest);

            if (EXPECTED_REQUEST.equals(receivedRequest)) {
                log("Odebrano prawidłowe zapytanie o liczbę wątków od: " + clientInfo);

                // Wysłanie liczby wątków do klienta
                log("Wysyłanie liczby wątków (" + THREADS_TO_ALLOCATE + ") do klienta: " + clientInfo);
                oos.writeObject(THREADS_TO_ALLOCATE); // Wysyłamy Integer
                oos.flush();

                // Set do wyslania
                log("Etap 1: Inicjalizacja struktury danych dla klienta " + clientInfo + "...");
                String mode = "1";
                Set<Person> family = Main.initFamily(mode);
                if (family == null) {
                    log("BŁĄD: Nie udało się zainicjalizować rodziny dla klienta " + clientInfo);
                    return;
                }

                log("Etap 2: Generowanie danych testowych dla klienta " + clientInfo + "...");
                Main.createTestData(family, mode);
                log("Zakończono generowanie danych testowych. Liczba elementów: " + (family != null ? family.size() : "null"));

                log("Wysyłanie obiektu rodziny (Set<Person>) do klienta: " + clientInfo);
                oos.writeObject(family);
                oos.flush();
                log("Obiekt rodziny wysłany do klienta: " + clientInfo);

                // Odbiór wyników (ResultManager) od klienta - obliczeń
                log("Oczekiwanie na obiekt ResultManager od klienta: " + clientInfo);
                Object receivedResultObject = ois.readObject();
                log("Odebrano drugi obiekt (wyniki) od klienta: " + clientInfo);

                // Sprawdzenie typu i rzutowanie
                if (receivedResultObject instanceof ResultManager resultManager) {
                    log("Odebrany obiekt to ResultManager. Przetwarzanie wyników dla: " + clientInfo);

                    // Pobranie wyników
                    List<String> results = resultManager.getResults();

                    // Wyświetlenie wyników na konsoli serwera
                    log("--- Wyniki od klienta " + clientInfo + " ---");
                    if (results == null || results.isEmpty()) {
                        log("(Brak wyników lub lista jest null/pusta)");
                    } else {
                        for (String result : results) {
                            log("  " + result);
                        }
                    }
                    log("--- Koniec wyników od klienta " + clientInfo + " ---");

                } else if (receivedResultObject == null) {
                    log("OSTRZEŻENIE: Klient " + clientInfo + " wysłał null zamiast ResultManager. Możliwy błąd po stronie klienta.");
                }
                else {
                    log("BŁĄD: Oczekiwano ResultManager, ale odebrano: " +
                            receivedResultObject.getClass().getName() +
                            " od " + clientInfo);
                }

            } else {
                log("BŁĄD: Odebrano nieoczekiwane zapytanie początkowe: " + receivedRequest + " od " + clientInfo);
            }

        } catch (IOException e) {
            log("Błąd wejścia/wyjścia lub rozłączenie podczas komunikacji z klientem " + clientInfo + ": " + e.getMessage());
        } catch (ClassNotFoundException e) {
            log("Błąd deserializacji: Nie znaleziono klasy obiektu lub niezgodna wersja dla klienta " + clientInfo + ": " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            log("Nieoczekiwany błąd podczas obsługi klienta " + clientInfo + ": " + e.getMessage());
            e.printStackTrace();
        }
        finally {
            log("Zakończono obsługę klienta i zamknięto połączenie: " + clientInfo);
        }
    }
}