# GeneFlow

> Modularna aplikacja w Javie do analizy przepływu doświadczenia międzypokoleniowego przy użyciu obliczeń wielowątkowych, komunikacji sieciowej i trwałego przechowywania danych.

---

## Spis treści

* [Opis projektu](#opis-projektu)
* [Kluczowe funkcje](#kluczowe-funkcje)
* [Inteligentne obliczenia: Legacy Index Engine](#inteligentne-obliczenia-legacy-index-engine)
* [Technologie](#technologie)
* [Analiza wydajności](#analiza-wydajności)
* [Struktura projektu](#struktura-projektu)
* [Jak uruchomić](#jak-uruchomić)
* [Przykładowe wyniki](#przykładowe-wyniki)
* [Kontakt](#kontakt)
* [Licencja](#licencja)

---

## Opis projektu

**GeneFlow** to zaawansowana, modularna aplikacja napisana w Javie, która modeluje i analizuje przekazywanie doświadczenia międzypokoleniowego w strukturach hierarchicznych. Projekt integruje techniki obliczeń wielowątkowych, komunikacji sieciowej, obiektowo-relacyjnego mapowania danych i automatyzowanego testowania w jednym, spójnym środowisku Apache Maven.

---

## Kluczowe funkcje

* **Modelowanie struktur danych**: rekurencyjne drzewa rodzinne lub organizacyjne.
* **Obliczenia wielowątkowe**: silnik Monte Carlo z konfiguracją liczby wątków.
* **Architektura komponentowa**: rozdzielenie logiki obliczeń, danych, sieci oraz prezentacji.
* **Komunikacja klient–serwer**: aplikacja TCP obsługująca wielu klientów.
* **ORM**: JPA/Hibernate z bazą H2, pełen CRUD i zapytania JPQL.
* **Testowanie**: JUnit, Mockito i AssertJ dla repozytoriów i logiki.

---

## Inteligentne obliczenia: Legacy Index Engine

Silnik **Legacy Index** oblicza syntetyczną miarę „doświadczenia”:

1. **Ważona średnia** wieku w drzewie, gdzie waga spada wraz z pokoleniem.
2. **Symulacja Monte Carlo** dla korekty bazowego wyniku.

Możliwość uruchomienia w trybie sekwencyjnym lub wielowątkowym (parametr `--threads`).

---

## Technologie

* **Java SE**: kolekcje (Map, Set), I/O, serializacja, argumenty CLI.
* **Apache Maven**: zarządzanie zależnościami i budowa artefaktów.
* **Współbieżność**: `Thread`, `Runnable`, `synchronized`, pomiar czasu.
* **Sieć**: `Socket` TCP, wątek na klienta, logowanie zdarzeń.
* **JPA / Hibernate**: adnotacje encji, relacje 1\:N, `persistence.xml`.
* **Baza danych**: H2 (in-memory).
* **Testy**: JUnit 5, Mockito, AssertJ.

---

## Analiza wydajności

Narzędzia raportują wpływ liczby wątków i iteracji Monte Carlo na czas i dokładność:

* Skalowalność wątków (np. optymalnie \~12 wątków).
* Kompromis między czasem a precyzją.

---

## Struktura projektu

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
pom.xml            -> konfiguracja Maven
README.md          -> dokumentacja
```

## Przykładowe wyniki

* **1 wątek** (3375 elementów, 10k iteracji): 27.3s
* **12 wątków**: 4.2s, stabilność ±0.005

---

## Kontakt

📧 E-mail: [npnpdev@gmail.com](mailto:npnpdev@gmail.com)
GitHub: [npnpdev](https://github.com/npnpdev)
LinkedIn: https://www.linkedin.com/in/igor-tomkowicz-a5760b358/
---

## Licencja

Ten projekt jest dostępny na licencji MIT. Zobacz plik [LICENSE](LICENSE) po szczegóły.
