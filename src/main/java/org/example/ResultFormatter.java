package org.example;

public interface ResultFormatter {

    String format(Person person, double averageAge, long processingTime);
    void printResults(ResultManager resultManager);
}
