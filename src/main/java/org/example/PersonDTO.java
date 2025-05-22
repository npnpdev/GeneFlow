package org.example;

import java.util.Objects;

public class PersonDTO {
    private int id;
    private String name;
    private String surname;
    private int age;

    public PersonDTO() {
    }

    public PersonDTO(int id, String name, String surname, int age) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.age = age;
    }

    // Konwersja z encji Person do DTO
    public static PersonDTO fromEntity(Person person) {
        return new PersonDTO(
                person.getId(),
                person.getName(),
                person.getSurname(),
                person.getAge()
        );
    }

    // Konwersja z DTO do encji Person
    public Person toEntity() {
        Person p = new Person();
        // p.setId(this.id);
        p = new Person(this.name, this.surname, this.age);
        return p;
    }

    // Konwersja DTO do JSON'a
    public String toJson() {
        return "{"
                + "\"id\":" + id + ","
                + "\"name\":\"" + name + "\","
                + "\"surname\":\"" + surname + "\","
                + "\"age\":" + age
                + "}";
    }

    // Konwersja JSON'a do DTO
    public static PersonDTO fromJson(String json) {
        // Usuwamy nawiasy klamrowe i białe znaki
        String content = json.trim();
        if (content.startsWith("{") && content.endsWith("}")) {
            content = content.substring(1, content.length() - 1);
        }
        String[] parts = content.split(",");
        int id = 0;
        String name = null;
        String surname = null;
        int age = 0;
        for (String part : parts) {
            String[] kv = part.split(":", 2);
            String key = kv[0].trim().replace("\"", "");
            String value = kv[1].trim().replace("\"", "");
            switch (key) {
                case "id": id = Integer.parseInt(value); break;
                case "name": name = value; break;
                case "surname": surname = value; break;
                case "age": age = Integer.parseInt(value); break;
            }
        }
        return new PersonDTO(id, name, surname, age);
    }

    // Gettery i settery
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return toJson();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PersonDTO that)) return false;
        return id == that.id
                && age == that.age
                && Objects.equals(name, that.name)
                && Objects.equals(surname, that.surname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, surname, age);
    }
}
