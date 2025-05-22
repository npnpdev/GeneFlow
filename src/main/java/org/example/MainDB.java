package org.example;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;
import java.util.Scanner;
import org.h2.tools.Server;
import java.sql.SQLException;

public class MainDB {
    public static void main(String[] args) throws SQLException {
        // Uruchomienie serwera webowego H2
        Server webServer = Server.createWebServer("-web", "-webPort", "8082");
        webServer.start();

        // Tworzymy jedną instancję EntityManagerFactory, która będzie używana przez całą aplikację
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("myPU");

        // Przekazujemy emf do DAO oraz QueryService
        PersonDAO personDAO = new PersonDAO(emf);
        QueryService queryService = new QueryService(emf);

        // Inicjalizacja przykładowych danych
        Family family = new Family("Kowalski");
        Person person1 = new Person("Jan", "Kowalski", 35);
        Person person2 = new Person("Marek", "Kowalski", 30);
        family.addMember(person1);
        family.addMember(person2);

        // Zapisujemy przykładowe dane do bazy
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(family);
            em.getTransaction().commit();
            System.out.println("Przykładowa rodzina i osoby zostały zapisane do bazy.");
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Błąd przy zapisie przykładowych danych: " + e.getMessage());
        } finally {
            em.close();
        }

        // Interfejs tekstowy umożliwiający wykonanie operacji
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("\n--- Menu ---");
            System.out.println("1. Dodaj nową osobę");
            System.out.println("2. Usuń osobę");
            System.out.println("3. Wyświetl wszystkie osoby");
            System.out.println("4. Wyświetl ograniczoną liczbę osób (pierwszych N)");
            System.out.println("5. Wyświetl osoby starsze niż 30 lat");
            System.out.println("0. Wyjście");
            System.out.print("Wybierz opcję: ");

            String option = scanner.nextLine();
            switch (option) {
                case "1":
                    System.out.print("Podaj imię: ");
                    String name = scanner.nextLine();
                    System.out.print("Podaj nazwisko: ");
                    String surname = scanner.nextLine();
                    System.out.print("Podaj wiek: ");
                    int age = Integer.parseInt(scanner.nextLine());
                    Person newPerson = new Person(name, surname, age);
                    personDAO.addPerson(newPerson);
                    break;
                case "2":
                    System.out.print("Podaj id osoby do usunięcia: ");
                    int id = Integer.parseInt(scanner.nextLine());
                    personDAO.deletePerson(id);
                    break;
                case "3":
                    List<Person> allPersons = personDAO.getAllPersons();
                    if (allPersons.isEmpty()) {
                        System.out.println("Brak osób w bazie.");
                    } else {
                        System.out.println("Wszystkie osoby w bazie:");
                        allPersons.forEach(System.out::println);
                    }
                    break;
                case "4":
                    System.out.print("Podaj liczbę rekordów do wyświetlenia: ");
                    int limit = Integer.parseInt(scanner.nextLine());
                    List<Person> limitedPersons = personDAO.getPersonsLimited(limit);
                    if (limitedPersons.isEmpty()) {
                        System.out.println("Brak osób w bazie.");
                    } else {
                        limitedPersons.forEach(System.out::println);
                    }
                    break;
                case "5":
                    List<Person> olderThan30 = queryService.findPersonsOlderThan30();
                    if (olderThan30.isEmpty()) {
                        System.out.println("Brak osób starszych niż 30 lat.");
                    } else {
                        System.out.println("Osoby starsze niż 30 lat:");
                        olderThan30.forEach(System.out::println);
                    }
                    break;
                case "0":
                    exit = true;
                    System.out.println("Koniec programu.");
                    break;
                default:
                    System.out.println("Niepoprawna opcja. Wybierz ponownie.");
                    break;
            }
        }
        scanner.close();
        emf.close();
    }
}