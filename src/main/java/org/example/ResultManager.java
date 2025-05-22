package org.example;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
public class ResultManager implements Serializable{
    private static final String FILE_NAME = "wyniki.txt";
    private final List<String> results = new LinkedList<>();

    // Dodaje wynik do listy
    public synchronized void addResult(String result) {
        results.add(result);
    }

    // Zwraca kopię listy wyników.
    public synchronized List<String> getResults() {
        return new LinkedList<>(results);
    }

    // Metoda zapisująca wyniki do pliku
    public synchronized void writeHeaderAndResults(String analysisType, long totalTime) {
        try (FileWriter fw = new FileWriter(FILE_NAME, true);
             BufferedWriter bw = new BufferedWriter(fw)) {

            // Zapis nagłówka
            bw.write("Analiza " + analysisType + "; całkowity czas działania: " + totalTime + " ms");
            bw.newLine();

            // Zapis wyników
            for (String result : results) {
                bw.write(result);
                bw.newLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
