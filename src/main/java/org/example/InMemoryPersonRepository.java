package org.example;

import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

/** Implementacja PersonRepository - trzymamy dane w HashMapie */
public class InMemoryPersonRepository implements PersonRepository {
    private final Map<Integer, PersonDTO> storage = new HashMap<>();

    @Override
    public void save(PersonDTO dto) {
        int id = dto.getId();
        if (storage.containsKey(id)) {
            throw new IllegalArgumentException("Entity with id " + id + " already exists");
        }
        storage.put(id, dto);
    }

    @Override
    public Optional<PersonDTO> findById(int id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public void deleteById(int id) {
        if (!storage.containsKey(id)) {
            throw new IllegalArgumentException("Entity with id " + id + " not found");
        }
        storage.remove(id);
    }
}