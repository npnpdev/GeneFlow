package org.example;

import java.util.Random;

public class CalculationThread implements Runnable {
    private final TaskManager taskManager;
    private final ResultManager resultManager;
    private final ResultFormatter resultFormatter;

    // Parametry symulacji Monte Carlo
    private static final int MONTE_CARLO_RUNS = 500;
    private static final int DAYS_IN_YEAR = 365;
    private static final int EVENTS_PER_DAY = 5;
    private static final double POSITIVE_EVENT_PROBABILITY = 0.1;
    private static final double POSITIVE_IMPACT = 0.05;  // Wartość dodawana przy zdarzeniu pozytywnym
    private static final double NEGATIVE_IMPACT = 0.005; // Wartość odejmowana przy zdarzeniu negatywnym

    public CalculationThread(TaskManager taskManager, ResultManager resultManager, ResultFormatter resultFormatter) {
        this.taskManager = taskManager;
        this.resultManager = resultManager;
        this.resultFormatter = resultFormatter;
    }

    @Override
    public void run() {
        while (true) {
            Person person = null;
            try {
                person = taskManager.getTask();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            if (person == null) { // sygnał zakończenia (shutdown)
                break;
            }
            long startTime = System.currentTimeMillis();
            // Obliczenie "doszlifowanej" ważonej średniej wieku z wykorzystaniem symulacji Monte Carlo
            double legacyIndex = CalculationThread.computeMonteCarloWeightedAverageAge(person);
            long endTime = System.currentTimeMillis();
            long processingTime = endTime - startTime;

            String result = resultFormatter.format(person, legacyIndex, processingTime);
            resultManager.addResult(result);
        }
    }

    /**
     * Oblicza "doszlifowaną" ważoną średnią wieku z wykorzystaniem symulacji Monte Carlo
     * Najpierw obliczana jest bazowa ważona średnia wieku rekurencyjnie,
     * a następnie wynik jest korygowany przez symulację Monte Carlo
     **/
    public static double computeMonteCarloWeightedAverageAge(Person person) {
        double[] sumAndWeight = computeSumAndWeight(person, 1.0);
        double baseWeightedAverage = sumAndWeight[0] / sumAndWeight[1];

        return runMonteCarloSimulationStatic(baseWeightedAverage);
    }

    /**
     * Statyczna metoda wykonująca symulację Monte Carlo na bazowej wartości
     * Liczba iteracji obliczana jest jako: (wiek * DAYS_IN_YEAR) dni
     * Dla każdego dnia symulowane są zdarzenia:
     * - Z prawdopodobieństwem POSITIVE_EVENT_PROBABILITY następuje zdarzenie pozytywne, które zwiększa wartość
     * - Z prawdopodobieństwem 1-POSITIVE_EVENT_PROBABILITY następuje zdarzenie negatywne, które ją obniża
     **/
    private static double runMonteCarloSimulationStatic(double baseValue) {
        Random random = new Random();
        double sumResults = 0.0;

        // W każdej symulacji wykorzystujemy liczbę dni = (wiek * DAYS_IN_YEAR)
        int days = (int) (baseValue * DAYS_IN_YEAR);

        for (int run = 0; run < MONTE_CARLO_RUNS; run++) {
            double simulationValue = baseValue;
            for (int day = 0; day < days; day++) {
                for (int event = 0; event < EVENTS_PER_DAY; event++) {
                    double chance = random.nextDouble();
                    if (chance < POSITIVE_EVENT_PROBABILITY) {
                        simulationValue += POSITIVE_IMPACT;
                    } else {
                        simulationValue -= NEGATIVE_IMPACT;
                    }
                }
            }
            sumResults += simulationValue;
        }
        return sumResults / MONTE_CARLO_RUNS;
    }

    /**
     * Rekurencyjna metoda obliczająca sumę wieku pomnożoną przez wagę (priorytet) oraz sumę wag
     **/
    private static double[] computeSumAndWeight(Person person, double weight) {
        double sum = person.getAge() * weight;
        double totalWeight = weight;

        if (person.getChildren() != null && !person.getChildren().isEmpty()) {
            for (Person child : person.getChildren()) {
                // Dla każdego pokolenia dzieci waga jest zmniejszana o połowę
                double[] childResult = computeSumAndWeight(child, weight / 2);
                sum += childResult[0];
                totalWeight += childResult[1];
            }
        }
        // Zwracamy tablicę double, gdzie indeks 0 to suma wieku * waga, a indeks 1 to suma wag
        return new double[]{sum, totalWeight};
    }
}
