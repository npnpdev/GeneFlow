package org.example;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "person")
public class Person implements Comparable<Person>, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String surname;
    private int age;

    // Relacja self-referencing: osoba może mieć dzieci (jednokierunkowa relacja jeden-do-wielu)
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Set<Person> children = new HashSet<>();

    // Konstruktor domyślny wymagany przez JPA
    public Person() {
    }

    public Person(String name, String surname, int age) {
        this.name = name;
        this.surname = surname;
        this.age = age;
    }

    public void addChild(Person child) {
        children.add(child);
    }

    public int getAge() {
        return age;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public Set<Person> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return "[Id: " + id + ", Name: " + name + ", Surname: " + surname + ", Age: " + age + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person p = (Person) o;
        return id == p.id &&
                age == p.age &&
                Objects.equals(name, p.name) &&
                Objects.equals(surname, p.surname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, surname, age);
    }

    @Override
    public int compareTo(Person p) {
        return Integer.compare(this.id, p.id);
    }
}
