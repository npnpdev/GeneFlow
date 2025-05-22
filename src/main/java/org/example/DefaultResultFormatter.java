package org.example;

public class DefaultResultFormatter implements ResultFormatter {

    @Override
    public String format(Person person, double averageAge, long processingTime) {
        return String.format("Person: %s %s, averageAge: %.2f, Processing Time: %d ms",
                person.getName(),
                person.getSurname(),
                averageAge,
                processingTime);
    }

    @Override
    public void printResults(ResultManager resultManager) {
        resultManager.getResults().forEach(System.out::println);
        System.out.println();
    }
}
