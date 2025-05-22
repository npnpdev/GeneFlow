package org.example;

import java.util.Optional;

/** Kontroler operujący na danych Person w formie JSON. */
public class PersonController {
    private final PersonRepository repository;

    public PersonController(PersonRepository repository) {
        this.repository = repository;
    }

    /** Zapisuje nowego Person na podstawie JSON'a - "done" jesli ok, "bad request" jeśli duplikat */
    public String save(String personJson) {
        try {
            PersonDTO dto = PersonDTO.fromJson(personJson);
            repository.save(dto);
            return "done";
        } catch (IllegalArgumentException e) {
            return "bad request";
        }
    }

    /** Znajduje Person po id - jeśli OK to json, jeśli nie to "not found" */
    public String find(String idStr) {
        try {
            int id = Integer.parseInt(idStr);
            Optional<PersonDTO> maybe = repository.findById(id);
            return maybe.map(PersonDTO::toJson)
                    .orElse("not found");
        } catch (NumberFormatException e) {
            return "not found";
        }
    }

    /** Usuwa Person o danym id - "done" jeśli OK, lub "not found" */
    public String delete(String idStr) {
        try {
            int id = Integer.parseInt(idStr);
            repository.deleteById(id);
            return "done";
        } catch (IllegalArgumentException e) {
            return "not found";
        }
    }
}