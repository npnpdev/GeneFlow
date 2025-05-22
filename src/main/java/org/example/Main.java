package org.example;

import javax.xml.transform.Result;
import java.util.*;

import static java.lang.Integer.parseInt;

public class Main {

    public static int DefaultThreadNumber = 4;
    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("Wymagany jest przynajmniej jeden parametr!");
            return;
        }
        // argument0 - kryterium sortowania
        String mode = args[0];
        // argument1 - ilość wątków
        int numThreads = args.length > 1 ? Integer.parseInt(args[1]) : DefaultThreadNumber;

        System.out.println("Tryb: " + mode + ", Liczba wątków: " + numThreads);

        // Inicjalizacja zbioru rodzinnego
        Set<Person> family = initFamily(mode);
        if (family == null) return; // Błąd przy złym trybie

        // Wypełniamy zbiór danymi
        createTestData(family, mode);

        // Formater odpowiedzialny za formatowanie i wypisywanie wyników
        ResultFormatter formatter = new DefaultResultFormatter();

        // Zbieranie dodatkowych zadań dla analizy sekwencyjnej
        Set<Person> additionalSequentialTasks = getAdditionalTasks(mode, family);
        // Przetwarzanie sekwencyjne
        runSequentialAnalysis(family, additionalSequentialTasks, formatter);

        Set<Person> additionalParallelTasks = getAdditionalTasks(mode, family);
        runParallelAnalysis(family, additionalParallelTasks, numThreads, formatter);

    }
    private static void runSequentialAnalysis(Set<Person> family, Set<Person> additionalTasks, ResultFormatter formatter) {
        System.out.println("\nAnaliza Sekwencyjna");
        ResultManager seqResultManager = new ResultManager();
        long startTime = System.currentTimeMillis();

        for(Person root : family) {
            long startRoot = System.currentTimeMillis();
            double legacyIndexRoot = CalculationThread.computeMonteCarloWeightedAverageAge(root);
            long processingTimeRoot = System.currentTimeMillis() - startRoot;
            seqResultManager.addResult(formatter.format(root, legacyIndexRoot, processingTimeRoot));

            for(Person child : root.getChildren()) {
                long startChild = System.currentTimeMillis();
                double legacyIndexChild = CalculationThread.computeMonteCarloWeightedAverageAge(child);
                long processingTimeChild = System.currentTimeMillis() - startChild;
                seqResultManager.addResult(formatter.format(child, legacyIndexChild, processingTimeChild));
            }
        }

        for (Person additional : additionalTasks) {
            long startAdditional = System.currentTimeMillis();
            double legacyIndex = CalculationThread.computeMonteCarloWeightedAverageAge(additional);
            long processingTime = System.currentTimeMillis() - startAdditional;
            seqResultManager.addResult(formatter.format(additional, legacyIndex, processingTime));
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Czas sekwencyjny (bez czasu oczekiwania na dane): " + totalTime + " ms\n");
        // Zapisanie wyniku
        seqResultManager.writeHeaderAndResults("Sekwencyjna", totalTime);
        // Wypisanie wyniku
        formatter.printResults(seqResultManager);

    }

    private static void runParallelAnalysis(Set<Person> family, Set<Person> additionalTasks, int numThreads, ResultFormatter formatter) {
        System.out.println("\nAnaliza Równoległa");
        TaskManager taskManager = new TaskManager();
        ResultManager parResultManager = new ResultManager();

        // Dodajemy domyślne zadania (rooty i dzieci)
        for (Person root : family) {
            taskManager.addTask(root);
            for (Person child : root.getChildren()) {
                taskManager.addTask(child);
            }
        }
        // Dodajemy dodatkowe zadania
        for (Person additional : additionalTasks) {
            taskManager.addTask(additional);
        }
        // Sygnalizujemy, że nie pojawią się nowe zadania
        taskManager.shutdown();

        List<Thread> threads = new ArrayList<>();
        long startTime = System.currentTimeMillis();

        // Tworzymy wątki
        for (int i = 0; i < numThreads; i++) {
            Thread thread = new Thread(new CalculationThread(taskManager, parResultManager, formatter));
            thread.setName("Worker-" + (i + 1));
            thread.start();
            threads.add(thread);
        }

        // Czekamy na zakończenie wszystkich wątków
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Czas równoległy (bez czasu oczekiwania na dane): " + totalTime + " ms\n");
        // Zapisanie wyniku
        parResultManager.writeHeaderAndResults("Równoległa (" + numThreads + " wątków)", totalTime);
        // Wypisanie wyniku
        formatter.printResults(parResultManager);
    }

    private static Set<Person> getAdditionalTasks(String mode, Set<Person> family) {
        Set<Person> additionalTasks = initFamily(mode);
        Scanner scanner = new Scanner(System.in);
        System.out.println("Wpisz ID obiektu do dodania lub exit, aby zakończyć:");

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("exit")) {
                break;
            }
            try {
                int id = Integer.parseInt(input);
                Person found = findPersonById(family, id);
                if (found != null) {
                    additionalTasks.add(found);
                    System.out.println("Dodano: " + found.getName() + " " + found.getSurname());
                } else {
                    System.out.println("Nie znaleziono obiektu o ID " + id);
                }
            } catch (NumberFormatException e) {
                System.out.println("Nieprawidłowy format. Wpisz ID lub exit");
            }
        }
        return additionalTasks;
    }

    private static Person findPersonById(Set<Person> persons, int id) {
        for (Person person : persons) {
            if (person.getId() == id) {
                return person;
            }
            Person found = findPersonById(person.getChildren(), id);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    public static Set<Person> initFamily(String mode) {
        return switch (mode) {
            case "1" -> new HashSet<>();
            case "2" -> new TreeSet<>();
            case "3" -> new TreeSet<>(new PersonComparator());
            default -> {
                System.out.println("Nieprawidłowy tryb. Dostępne tryby: 1, 2, 3.");
                yield null;
            }
        };
    }

    public static Set<Person> createTestData(Set<Person> testSet, String mode) {
        int numRoot = 5;
        int numChildren = 5;
        int numGrandChildren = 5;
        Random rand = new Random();

        for (int i = 1; i <= numRoot; i++) {
            // Używamy konstruktora bez pola id i mode – id będzie generowane automatycznie przez JPA
            Person root = new Person("Root" + i, "Johnson", rand.nextInt(100));

            for (int j = 1; j <= numChildren; j++) {
                Person child = new Person("Child" + j, "Johnson", rand.nextInt(50));

                for (int k = 1; k <= numGrandChildren; k++) {
                    Person grand = new Person("Grand" + k, "Johnson", rand.nextInt(20));
                    child.addChild(grand);
                }
                root.addChild(child);
            }
            testSet.add(root);
        }
        return testSet;
    }

    public static void printPersons(Set<Person> testSet, int space){
        for(Person e : testSet){
            for(int i=0; i<space; i++){
                System.out.print("     ");
            }
            System.out.println(e);

            if(e.getChildren() != null && !e.getChildren().isEmpty()){
                printPersons(e.getChildren(), space + 1);
            }
        }
    }

    private static int computeStatistics(Person Person, Map<Person, Integer> stats) {
        int count = 0;
        for(Person child : Person.getChildren()){
            count += 1 + computeStatistics(child, stats);
        }
        stats.put(Person, count);
        return count;
    }
}