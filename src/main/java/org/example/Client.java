package org.example;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Client {
    private static final String SERVER_ADDRESS = "153.19.54.202";
    private static final int SERVER_PORT = 12345;
    private static final String THREAD_REQUEST_MESSAGE = "GET_THREAD_COUNT";

    // Metoda log'owania
    private static void log(String message) {
        String timestamp = String.format("%tF %<tT.%<tL", new Date());
        System.out.println(timestamp + " [" + Thread.currentThread().getName() + "] " + message);
    }

    public static void main(String[] args) {
        log("Uruchamianie klienta...");

        ResultManager resultManager = null;
        long totalCalculationTime = 0;
        int numThreadsFromServer = Main.DefaultThreadNumber;
        Set<Person> family = null;

        log("Próba nawiązania połączenia z serwerem: " + SERVER_ADDRESS + ":" + SERVER_PORT);
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream()))
        {
            log("Połączono z serwerem.");

            log("Wysyłanie zapytania o liczbę wątków...");
            oos.writeObject(THREAD_REQUEST_MESSAGE);
            oos.flush();
            log("Zapytanie wysłane. Oczekiwanie na odpowiedź (liczba wątków)...");

            Object response = ois.readObject();
            if (response instanceof Integer) {
                numThreadsFromServer = (Integer) response;
                if (numThreadsFromServer <= 0) {
                    log("BŁĄD: Serwer zwrócił nieprawidłową liczbę wątków: " + numThreadsFromServer + ". Zakończenie.");
                    return;
                }
                log("Otrzymano liczbę wątków od serwera: " + numThreadsFromServer);
            } else {
                log("BŁĄD: Otrzymano nieoczekiwaną odpowiedź (zamiast liczby wątków) od serwera: " +
                        (response == null ? "null" : response.getClass().getName()) + ". Zakończenie.");
                return;
            }

            log("Oczekiwanie na obiekt rodziny (Set<Person>) od serwera...");
            Object familyObject = ois.readObject();

            if (familyObject instanceof Set) {
                try {
                    Set<Person> receivedFamily = (Set<Person>) familyObject;

                    if (receivedFamily != null) {
                        family = receivedFamily;
                        log("Otrzymano obiekt rodziny (Set<Person>) od serwera. Liczba elementów: " + family.size());
                    } else {
                        log("BŁĄD: Otrzymano null Set<Person> od serwera. Prawdopodobny błąd po stronie serwera. Zakończenie.");
                        return;
                    }
                } catch (ClassCastException e) {
                    log("BŁĄD: Otrzymany obiekt Set nie zawierał oczekiwanego typu Person. " + e.getMessage() + ". Zakończenie.");
                    return;
                }
            } else {
                log("BŁĄD: Oczekiwano Set<Person>, ale otrzymano: " +
                        (familyObject == null ? "null" : familyObject.getClass().getName()) + ". Zakończenie.");
                return;
            }

            if (family != null && !family.isEmpty()) {
                log("Etap 3: Uruchamianie analizy równoległej z " + numThreadsFromServer + " wątkami...");
                long startTime = System.nanoTime();

                try {
                    resultManager = runParallelAnalysisInternal(family, numThreadsFromServer);

                    long endTime = System.nanoTime();
                    totalCalculationTime = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
                    log("Zakończono analizę równoległą. Czas obliczeń: " + totalCalculationTime + " ms");

                } catch (Exception e) {
                    log("Wystąpił krytyczny błąd podczas lokalnych obliczeń: " + e.getMessage());
                    e.printStackTrace();
                    resultManager = null;
                }
            } else if (family != null && family.isEmpty()) {
                log("Otrzymano pusty zbiór rodziny od serwera. Pomijanie obliczeń.");
                resultManager = new ResultManager();
            }
            else {
                log("Nie można kontynuować - brak poprawnych danych rodziny od serwera.");
                resultManager = null;
            }

            if (resultManager != null) {
                log("Przygotowano ResultManager (może być pusty jeśli nie było danych/obliczeń).");
                log("Wysyłanie obiektu ResultManager do serwera...");
                oos.writeObject(resultManager);
                oos.flush();
                log("Obiekt ResultManager został wysłany pomyślnie.");
            } else {
                log("Nie udało się wygenerować wyników (ResultManager is null) lub wystąpił błąd przed obliczeniami. Nic nie wysłano.");
            }

        } catch (UnknownHostException e) {
            log("BŁĄD KRYTYCZNY: Nie można znaleźć serwera: " + SERVER_ADDRESS);
        } catch (IOException e) {
            log("BŁĄD KRYTYCZNY: Problem z połączeniem, komunikacją lub strumieniami: " + e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            log("BŁĄD KRYTYCZNY: Nie można znaleźć klasy podczas odbierania obiektu od serwera: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            log("BŁĄD KRYTYCZNY: Wystąpił nieoczekiwany błąd w kliencie: " + e.getMessage());
            e.printStackTrace();
        }
        finally {
            log("Zakończono próbę komunikacji z serwerem.");
        }

        log("Klient zakończył działanie.");
    }

    private static ResultManager runParallelAnalysisInternal(Set<Person> family, int numThreads) {
        if (family == null || family.isEmpty()) {
            log("[Client Analiza Wewn.] OSTRZEŻENIE: Przekazano pusty lub null zbiór 'family' do analizy. Zwracanie pustego ResultManager.");
            return new ResultManager();
        }

        TaskManager taskManager = new TaskManager();
        ResultManager parResultManager = new ResultManager();
        ResultFormatter formatter = new DefaultResultFormatter();

        log("[Client Analiza Wewn.] Dodawanie zadań na podstawie otrzymanej rodziny...");
        int taskCount = 0;
        for (Person root : family) {
            if (root != null) {
                taskManager.addTask(root);
                taskCount++;
                if (root.getChildren() != null) {
                    for (Person child : root.getChildren()) {
                        if (child != null) {
                            taskManager.addTask(child);
                            taskCount++;
                        }
                    }
                }
            }
        }
        log("[Client Analiza Wewn.] Dodano " + taskCount + " zadań (korzenie i ich dzieci).");
        taskManager.shutdown();

        List<Thread> threads = new ArrayList<>();
        log("[Client Analiza Wewn.] Tworzenie i start " + numThreads + " wątków Worker...");
        for (int i = 0; i < numThreads; i++) {
            Thread thread = new Thread(new CalculationThread(taskManager, parResultManager, formatter));
            thread.setName("CalcWorker-" + (i + 1));
            thread.start();
            threads.add(thread);
        }

        log("[Client Analiza Wewn.] Oczekiwanie na zakończenie Workerów...");
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                log("[Client Analiza Wewn.] Przerwano oczekiwanie na wątki.");
                Thread.currentThread().interrupt();
            }
        }
        log("[Client Analiza Wewn.] Wszystkie wątki Worker zakończyły pracę.");

        return parResultManager;
    }
}