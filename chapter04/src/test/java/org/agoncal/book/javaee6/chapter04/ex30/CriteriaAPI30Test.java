package org.agoncal.book.javaee6.chapter04.ex30;

import org.agoncal.book.javaee6.chapter04.AbstractPersistentTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;

import static org.junit.Assert.assertEquals;

/**
 * @author Antonio Goncalves
 *         APress Book - Beginning Java EE 6 with Glassfish 3
 *         http://www.apress.com/
 *         http://www.antoniogoncalves.org
 *         --
 */
public class CriteriaAPI30Test extends AbstractPersistentTest {

    // ======================================
    // =              Constants             =
    // ======================================
    private static final int ALL = 6;
    private Customer30 customer01;
    private Customer30 customer02;
    private Customer30 customer03;
    private Customer30 customer04;
    private Customer30 customer05;
    private Customer30 customer06;

    // ======================================
    // =          Lifecycle Methods         =
    // ======================================

    @Before
    public void persistCustomers() {
        tx = em.getTransaction();

        customer01 = new Customer30("Antony", "Balla", "tballa@mail.com", 14);
        Address30 address01 = new Address30("Procession St", "Paris", "75015", "FR");
        customer01.setAddress(address01);

        customer02 = new Customer30("Vincent", "Johnson", "vj@mail.com", 45);
        Address30 address02 = new Address30("Ritherdon Rd", "London", "8QE", "UK");
        customer02.setAddress(address02);

        customer03 = new Customer30("Sebastian", "Twenty", "seb@yamail.com", 58);
        Address30 address03 = new Address30("Inacio Alfama", "Lisbon", "A54", "PT");
        customer03.setAddress(address03);

        customer04 = new Customer30("Frederic", "Riou", "fred@carmail.com", 41);
        Address30 address04 = new Address30("Jardins", "Sao Paulo", "345678", "BR");
        customer04.setAddress(address04);

        customer05 = new Customer30("Vincent", "Twenty", "vd@yahoo.com", 14);
        Address30 address05 = new Address30("Coffey", "Perth", "654F543", "AU");
        customer05.setAddress(address05);

        customer06 = new Customer30("David", "Chene", "dch@yahoo.com", 89);
        Address30 address06 = new Address30("Harbour Bridge", "Sydney", "JHG3", "AU");
        customer06.setAddress(address06);

        // Persist the object
        tx.begin();
        em.persist(customer01);
        em.persist(customer02);
        em.persist(customer03);
        em.persist(customer04);
        em.persist(customer05);
        em.persist(customer06);
        tx.commit();
    }

    @After
    public void removeCustomers() {

        // Remove objects
        tx.begin();
        em.remove(customer01);
        em.remove(customer02);
        em.remove(customer03);
        em.remove(customer04);
        em.remove(customer05);
        em.remove(customer06);
        tx.commit();
    }

    // ======================================
    // =              Unit tests            =
    // ======================================

    @Test
    public void shouldFindVincentCustomers() throws Exception {

        // select c from Customer c where c.firstName = 'Vincent'
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Customer30> query = builder.createQuery(Customer30.class);
        Root<Customer30> c = query.from(Customer30.class);
        query.select(c).where(builder.equal(c.get("firstName"), "Vincent"));
        assertEquals(2, em.createQuery(query).getResultList().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailBecauseFirstnameIsSpelledIncorrectly() throws Exception {

        // select c from Customer c where c.firstName = 'Vincent'
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Customer30> query = builder.createQuery(Customer30.class);
        Root<Customer30> c = query.from(Customer30.class);
        query.select(c).where(builder.equal(c.get("firstname"), "Vincent"));
        assertEquals(2, em.createQuery(query).getResultList().size());
    }

    @Test
    public void shouldFindVincentCustomersWithMetaModel() throws Exception {

        // select c from Customer c where c.firstName = 'Vincent'
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Customer30> query = builder.createQuery(Customer30.class);
        Root<Customer30> c = query.from(Customer30.class);
        query.select(c).where(builder.equal(c.get(Customer30_.firstName), "Vincent"));
        assertEquals(2, em.createQuery(query).getResultList().size());
    }

    @Test
    public void shouldFindVincentCustomersWithMetaModelAndEntityType() throws Exception {

        // select c from Customer c where c.firstName = 'Vincent'
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Customer30> query = builder.createQuery(Customer30.class);
        Root<Customer30> c = query.from(Customer30.class);
        EntityType<Customer30> c_ = c.getModel();
        query.select(c).where(builder.equal(c.get(c_.getSingularAttribute("firstName")), "Vincent"));
        assertEquals(2, em.createQuery(query).getResultList().size());
    }

    @Test
    public void shouldFindCustomersOlderThan40() throws Exception {
        // select c from Customer c where c.age > 40
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Customer30> query = builder.createQuery(Customer30.class);
        Root<Customer30> c = query.from(Customer30.class);
        query.select(c).where(builder.greaterThan(c.get("age").as(Integer.class), 40));
        assertEquals(4, em.createQuery(query).getResultList().size());
    }

    @Test
    public void shouldFindCustomersOlderThan40WithMetaModel() throws Exception {
        // select c from Customer c where c.age > 40
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Customer30> query = builder.createQuery(Customer30.class);
        Root<Customer30> c = query.from(Customer30.class);
        query.select(c).where(builder.greaterThan(c.get(Customer30_.age), 40));
        assertEquals(4, em.createQuery(query).getResultList().size());
    }

    @Test
    public void shouldFindAllWithADynamicQuery() throws Exception {

        // Criteria builder
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Customer30> criteriaQuery;
        TypedQuery<Customer30> query;
        Root<Customer30> customer;

        // select c from Customer c
        criteriaQuery = criteriaBuilder.createQuery(Customer30.class);
        criteriaQuery.from(Customer30.class);
        assertEquals(ALL, em.createQuery(criteriaQuery).getResultList().size());

        // select c from Customer c (setMaxResults(3))
        query = em.createQuery(criteriaQuery);
        query.setMaxResults(3);
        assertEquals(3, query.getResultList().size());

        // select c from Customer c where c.firstName = 'Vincent'
        criteriaQuery = criteriaBuilder.createQuery(Customer30.class);
        customer = criteriaQuery.from(Customer30.class);
        criteriaQuery.select(customer).where(criteriaBuilder.equal(customer.get("firstName"), "Vincent"));
        assertEquals(2, em.createQuery(criteriaQuery).getResultList().size());

        // select c from Customer c where c.firstName = 'Vincent' (using meta-model)
        String fName = "Vincent";
        criteriaQuery = criteriaBuilder.createQuery(Customer30.class);
        customer = criteriaQuery.from(Customer30.class);
        criteriaQuery.select(customer).where(criteriaBuilder.equal(customer.get(Customer30_.firstName), fName));
        assertEquals(2, em.createQuery(criteriaQuery).getResultList().size());

        // select c from Customer c where c.firstName = 'Vincent' (using meta-model & parameter)
        criteriaQuery = criteriaBuilder.createQuery(Customer30.class);
        customer = criteriaQuery.from(Customer30.class);
        criteriaQuery.select(customer).where(criteriaBuilder.equal(customer.get(Customer30_.firstName), "Vincent"));
        assertEquals(2, em.createQuery(criteriaQuery).getResultList().size());

        // select c from Customer c where c.firstName = 'Vincent' (using a predicate)
        criteriaQuery = criteriaBuilder.createQuery(Customer30.class);
        customer = criteriaQuery.from(Customer30.class);
        Predicate predicate = criteriaBuilder.equal(customer.get("firstName"), "Vincent");
        criteriaQuery.select(customer).where(predicate);
        assertEquals(2, em.createQuery(criteriaQuery).getResultList().size());

        // select c from Customer c where c.address.country = 'AU'
        criteriaQuery = criteriaBuilder.createQuery(Customer30.class);
        customer = criteriaQuery.from(Customer30.class);
        criteriaQuery.select(customer).where(criteriaBuilder.equal(customer.get("address").get("country"), "AU"));
        assertEquals(2, em.createQuery(criteriaQuery).getResultList().size());

        // select c from Customer c where c.address.country = 'AU' (using meta-model)
        criteriaQuery = criteriaBuilder.createQuery(Customer30.class);
        customer = criteriaQuery.from(Customer30.class);
        criteriaQuery.select(customer).where(criteriaBuilder.equal(customer.get(Customer30_.address).get(Address30_.country), "AU"));
        assertEquals(2, em.createQuery(criteriaQuery).getResultList().size());

        // select c from Customer c where c.age > 40
        criteriaQuery = criteriaBuilder.createQuery(Customer30.class);
        customer = criteriaQuery.from(Customer30.class);
        criteriaQuery.select(customer).where(criteriaBuilder.greaterThan(customer.get("age").as(Integer.class), 40));
        assertEquals(4, em.createQuery(criteriaQuery).getResultList().size());

        // select c from Customer c where c.age > 40 (using meta-model)
        criteriaQuery = criteriaBuilder.createQuery(Customer30.class);
        customer = criteriaQuery.from(Customer30.class);
        criteriaQuery.select(customer).where(criteriaBuilder.greaterThan(customer.get(Customer30_.age), 40));
        assertEquals(4, em.createQuery(criteriaQuery).getResultList().size());

        // select c from Customer c where c.age between 40 and 50
        criteriaQuery = criteriaBuilder.createQuery(Customer30.class);
        customer = criteriaQuery.from(Customer30.class);
        criteriaQuery.select(customer).where(criteriaBuilder.between(customer.get("age").as(Integer.class), 40, 50));
        assertEquals(2, em.createQuery(criteriaQuery).getResultList().size());

        // select c from Customer c where c.age between 40 and 50 (using meta-model)
        criteriaQuery = criteriaBuilder.createQuery(Customer30.class);
        customer = criteriaQuery.from(Customer30.class);
        criteriaQuery.select(customer).where(criteriaBuilder.between(customer.get(Customer30_.age), 40, 50));
        assertEquals(2, em.createQuery(criteriaQuery).getResultList().size());
    }
}