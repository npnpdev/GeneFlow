package org.example;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

public class QueryService {
    private EntityManagerFactory emf;

    public QueryService(EntityManagerFactory emf) {
        this.emf = emf;
    }

    // 1. Osoby starsze niż 30 lat
    public List<Person> findPersonsOlderThan30() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT p FROM Person p WHERE p.age > 30", Person.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // 2. Pierwszych 10 osób posortowanych według id
    public List<Person> findFirst10PersonsById() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT p FROM Person p ORDER BY p.id", Person.class)
                    .setMaxResults(10)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // 3. Osoby należące do rodziny o danej nazwie
    public List<Person> findPersonsByFamilyName(String familyName) {
        EntityManager em = emf.createEntityManager();
        try {
            String jpql = "SELECT p FROM Family f JOIN f.members p WHERE f.familyName = :familyName";
            return em.createQuery(jpql, Person.class)
                    .setParameter("familyName", familyName)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // 4. Średni wiek wszystkich osób
    public Double findAverageAge() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT AVG(p.age) FROM Person p", Double.class)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }

    // 5. Osoby posortowane według nazwiska i imienia
    public List<Person> findPersonsSortedByNameSurname() {
        EntityManager em = emf.createEntityManager();
        try {
            String jpql = "SELECT p FROM Person p ORDER BY p.surname ASC, p.name ASC";
            return em.createQuery(jpql, Person.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
