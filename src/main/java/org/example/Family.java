package org.example;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "family")
public class Family {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String familyName;

    // Relacja jeden-do-wielu: jedna rodzina ma wielu członków
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id")
    private Set<Person> members = new HashSet<>();

    // Konstruktor domyślny
    public Family() {
    }

    public Family(String familyName) {
        this.familyName = familyName;
    }

    public Long getId() {
        return id;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public Set<Person> getMembers() {
        return members;
    }

    public void addMember(Person person) {
        members.add(person);
    }

    @Override
    public String toString() {
        return "Family [id=" + id + ", familyName=" + familyName + ", members=" + members + "]";
    }
}
