package org.example;

import java.util.Optional;

/** Interfejs repozytorium encji Person w pamięci */
public interface PersonRepository {
    /** Zapisuje nowy obiekt */
    void save(PersonDTO dto);

    /** Pobiera obiekt na podstawie id */
    Optional<PersonDTO> findById(int id);

    /** Usuwa obiekt o podanym id */
    void deleteById(int id);
}