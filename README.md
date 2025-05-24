# GeneFlow

[English](#english-version) | [Polski](#wersja-polska)

---

## English Version

### Project Description

**GeneFlow** is a modular Java application for modeling and analyzing the intergenerational transfer of experience in hierarchical structures. It brings together multithreaded computation, network communication, object‑relational mapping, and automated testing in a unified Apache Maven environment.

### Key Features

* **Data Structure Modeling**: recursive family or organizational trees
* **Multithreading**: Monte Carlo engine with configurable thread count
* **Component Architecture**: separation of computation, data, network, and presentation logic
* **Client–Server Communication**: TCP application supporting multiple clients
* **ORM**: JPA/Hibernate with H2 database, full CRUD and JPQL queries
* **Testing**: JUnit, Mockito, and AssertJ for repositories and business logic

### Intelligent Computation: Legacy Index Engine

The **Legacy Index** engine computes a synthetic “experience” measure by:

1. **Weighted average** of ages in the tree, where weight decreases by generation
2. **Monte Carlo simulation** to adjust the baseline result

Can be run in sequential or multithreaded mode.

### Technologies

* **Java SE**: Collections (Map, Set), I/O, serialization, CLI arguments
* **Apache Maven**: dependency management and artifact building
* **Concurrency**: `Thread`, `Runnable`, `synchronized`, performance timing
* **Networking**: TCP `Socket`, one thread per client, event logging
* **JPA / Hibernate**: entity annotations, 1\:N relationships, `persistence.xml`
* **Database**: H2 (in-memory)
* **Testing**: JUnit 5, Mockito, AssertJ

### Performance Analysis

Reports the impact of thread count and Monte Carlo iterations on execution time and precision:

* Thread scalability (e.g., optimal at \~12 threads)
* Trade‑off between speed and accuracy

### Project Structure

```text
src/
  main/
    java/
      CalculationThread.java
      Client.java
      ClientHandler.java
      DefaultResultFormatter.java
      Family.java
      InMemoryPersonRepository.java
      Main.java
      MainDB.java
      Person.java
      PersonComparator.java
      PersonController.java
      PersonDAO.java
      PersonDTO.java
      PersonRepository.java
      QueryService.java
      ResultFormatter.java
      ResultManager.java
      Server.java
      TaskManager.java
  test/
    java/
      (unit tests for the above components)
pom.xml            → Maven configuration  
README.md          → documentation  
```

### Example Results

* **1 thread** (3375 elements, 10 000 iterations): 27.3 s
* **12 threads**: 4.2 s, stability ±0.005

---

## Wersja polska

### Opis projektu

**GeneFlow** to modularna aplikacja napisana w Javie do modelowania i analizy przekazywania doświadczenia międzypokoleniowego w strukturach hierarchicznych. Projekt łączy w sobie obliczenia wielowątkowe, komunikację sieciową, ORM oraz automatyczne testowanie w ramach Apache Maven.

### Kluczowe funkcje

* **Modelowanie struktur danych**: rekurencyjne drzewa rodzinne lub organizacyjne
* **Obliczenia wielowątkowe**: silnik Monte Carlo z konfigurowalną liczbą wątków
* **Architektura komponentowa**: rozdzielenie logiki obliczeń, danych, sieci i prezentacji
* **Komunikacja klient–serwer**: aplikacja TCP obsługująca wielu klientów
* **ORM**: JPA/Hibernate z bazą H2, pełen CRUD i zapytania JPQL
* **Testowanie**: JUnit, Mockito i AssertJ

### Inteligentne obliczenia: Legacy Index Engine

Silnik **Legacy Index** oblicza syntetyczną miarę „doświadczenia”:

1. **Ważona średnia** wieku w drzewie, gdzie waga spada wraz z pokoleniem
2. **Symulacja Monte Carlo** dla korekty wyniku bazowego

Możliwość uruchomienia w trybie sekwencyjnym lub wielowątkowym.

### Technologie

* **Java SE**: kolekcje (Map, Set), I/O, serializacja, argumenty CLI
* **Apache Maven**: zarządzanie zależnościami i budowa artefaktów
* **Współbieżność**: `Thread`, `Runnable`, `synchronized`, pomiar czasu
* **Sieć**: `Socket` TCP, wątek na klienta, logowanie zdarzeń
* **JPA / Hibernate**: adnotacje encji, relacje 1\:N, `persistence.xml`
* **Baza danych**: H2 (in-memory)
* **Testy**: JUnit 5, Mockito, AssertJ

### Analiza wydajności

Raportuje wpływ liczby wątków i iteracji Monte Carlo na czas wykonania i precyzję:

* Skalowalność wątków (np. optymalnie \~12 wątków)
* Kompromis między szybkością a dokładnością

### Struktura projektu

```text
src/
  main/
    java/
      CalculationThread.java
      Client.java
      ClientHandler.java
      DefaultResultFormatter.java
      Family.java
      InMemoryPersonRepository.java
      Main.java
      MainDB.java
      Person.java
      PersonComparator.java
      PersonController.java
      PersonDAO.java
      PersonDTO.java
      PersonRepository.java
      QueryService.java
      ResultFormatter.java
      ResultManager.java
      Server.java
      TaskManager.java
  test/
    java/
      (testy jednostkowe dla powyższych komponentów)
pom.xml            → konfiguracja Maven  
README.md          → dokumentacja  
```

### Przykładowe wyniki

* **1 wątek** (3375 elementów, 10 000 iteracji): 27,3 s
* **12 wątków**: 4,2 s, stabilność ±0,005

---

## Autor / Author

Igor Tomkowicz
📧 [npnpdev@gmail.com](mailto:npnpdev@gmail.com)
GitHub: [npnpdev](https://github.com/npnpdev)
LinkedIn: [https://www.linkedin.com/in/igor-tomkowicz-a5760b358/](https://www.linkedin.com/in/igor-tomkowicz-a5760b358/)

---

## Licencja / License

MIT License. Zobacz plik [LICENSE](LICENSE) po szczegóły.
