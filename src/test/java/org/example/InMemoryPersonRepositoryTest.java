package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

class InMemoryPersonRepositoryTest {

    private PersonRepository repository;
    private PersonDTO dto1;
    private PersonDTO dto2;

    @BeforeEach
    void setUp() {
        repository = new InMemoryPersonRepository();
        dto1 = new PersonDTO(1, "Jan", "Kowalski", 35);
        dto2 = new PersonDTO(2, "Anna", "Nowak", 28);
    }

    @Test
    void saveNewEntityShouldStoreIt() {
        repository.save(dto1);
        Optional<PersonDTO> found = repository.findById(1);
        assertThat(found).isPresent();
        assertThat(found.get()).extracting(PersonDTO::getId, PersonDTO::getName, PersonDTO::getSurname, PersonDTO::getAge)
                .containsExactly(1, "Jan", "Kowalski", 35);
    }

    @Test
    void saveDuplicateShouldThrowException() {
        repository.save(dto1);
        assertThatThrownBy(() -> repository.save(dto1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void findNonExistingShouldReturnEmpty() {
        Optional<PersonDTO> found = repository.findById(99);
        assertThat(found).isEmpty();
    }

    @Test
    void deleteExistingShouldRemoveIt() {
        repository.save(dto1);
        repository.deleteById(1);
        assertThat(repository.findById(1)).isEmpty();
    }

    @Test
    void deleteNonExistingShouldThrowException() {
        assertThatThrownBy(() -> repository.deleteById(99))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }
}
