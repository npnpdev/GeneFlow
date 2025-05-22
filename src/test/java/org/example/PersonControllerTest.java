package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PersonControllerTest {
    private PersonRepository repository;
    private PersonController controller;
    private PersonDTO dto;
    private String dtoJson;

    @BeforeEach
    void setUp() {
        repository = mock(PersonRepository.class);
        controller = new PersonController(repository);
        dto = new PersonDTO(1, "Jan", "Kowalski", 35);
        dtoJson = dto.toJson();
    }

    @Test
    void saveValidShouldReturnDoneAndCallRepo() {
        // when: repository.save does not throw
        String result = controller.save(dtoJson);

        // then
        assertThat(result).isEqualTo("done");
        verify(repository).save(dto);
    }

    @Test
    void saveDuplicateShouldReturnBadRequest() {
        // given
        doThrow(new IllegalArgumentException()).when(repository).save(dto);

        // when
        String result = controller.save(dtoJson);

        // then
        assertThat(result).isEqualTo("bad request");
        verify(repository).save(dto);
    }

    @Test
    void findExistingShouldReturnJson() {
        // given
        when(repository.findById(1)).thenReturn(Optional.of(dto));

        // when
        String result = controller.find("1");

        // then
        assertThat(result).isEqualTo(dtoJson);
        verify(repository).findById(1);
    }

    @Test
    void findNonExistingShouldReturnNotFound() {
        // given
        when(repository.findById(99)).thenReturn(Optional.empty());

        // when
        String result = controller.find("99");

        // then
        assertThat(result).isEqualTo("not found");
        verify(repository).findById(99);
    }

    @Test
    void findInvalidIdShouldReturnNotFound() {
        // when: invalid format
        String result = controller.find("abc");

        // then
        assertThat(result).isEqualTo("not found");
        verify(repository, never()).findById(anyInt());
    }

    @Test
    void deleteExistingShouldReturnDoneAndCallRepo() {
        // when: delete does not throw
        String result = controller.delete("1");

        // then
        assertThat(result).isEqualTo("done");
        verify(repository).deleteById(1);
    }

    @Test
    void deleteNonExistingShouldReturnNotFound() {
        // given
        doThrow(new IllegalArgumentException()).when(repository).deleteById(1);

        // when
        String result = controller.delete("1");

        // then
        assertThat(result).isEqualTo("not found");
        verify(repository).deleteById(1);
    }

    @Test
    void deleteInvalidIdShouldReturnNotFound() {
        // when: invalid format
        String result = controller.delete("abc");

        // then
        assertThat(result).isEqualTo("not found");
        verify(repository, never()).deleteById(anyInt());
    }
}