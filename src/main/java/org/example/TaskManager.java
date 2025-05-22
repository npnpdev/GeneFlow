package org.example;

import java.util.LinkedList;

public class TaskManager {
    private final LinkedList<Person> tasks = new LinkedList<>();
    private boolean shutdown = false;

    // Dodaje zadanie (obiekt Person) do kolejki i powiadamia oczekujące wątki
    public synchronized void addTask(Person person) {
        tasks.add(person);
        // Powiadamiamy wszystkie wątki
        notifyAll();
    }

    // Pobiera zadanie z kolejki; jeśli brak zadań, czeka aż pojawią się nowe lub zostanie wywołane shutdown
    public synchronized Person getTask() throws InterruptedException {
        while (tasks.isEmpty() && !shutdown) {
            wait(); // Możemy dodać czas w wait, żeby budzik wątek okresowo?
        }
        if (shutdown && tasks.isEmpty()) {
            return null;
        }
        return tasks.remove();
    }

    // Sygnalizuje zakończenie działania – budzi wszystkie wątki oczekujące na zadania
    public synchronized void shutdown() {
        shutdown = true;
        notifyAll();
    }
}