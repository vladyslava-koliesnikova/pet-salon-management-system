package sk.upjs.ics.dogs;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcOperations;
import sk.upjs.ics.Factory;
import sk.upjs.ics.TestContainers;
import sk.upjs.ics.users.MysqlUserDao;
import sk.upjs.ics.users.User;
import sk.upjs.ics.users.UserDao;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MysqlDogDaoTest extends TestContainers {
    private MysqlDogDao dao;
    private UserDao userDao;

    @BeforeEach
    void setUp() {
        JdbcOperations jdbcOperations = Factory.INSTANCE.getMysqlJdbcOperations();
        dao = new MysqlDogDao(jdbcOperations);
        userDao = new MysqlUserDao(jdbcOperations);
        dao.deleteAll();
    }

    @Test
    void testCreateDog() {
        User owner = createOwner(1L);
        Dog dog = createDog("Buddy", Sex.M, LocalDate.of(2015, 5, 10), owner);

        Dog createdDog = dao.create(dog);
        assertNotNull(createdDog.getId(), "Created dog's ID should not be null.");
        assertEquals(dog.getName(), createdDog.getName(), "Created dog's name should match.");
    }

    @Test
    void testFindAll() {
        User owner = createOwner(1L);


        Dog dog1 = createDog("Bella", Sex.F, LocalDate.of(2016, 3, 15), owner);
        Dog dog2 = createDog("Max", Sex.M, LocalDate.of(2018, 7, 20), owner);

        dao.create(dog1);
        dao.create(dog2);


        List<Dog> dogs = dao.findAll();
        assertEquals(2, dogs.size(), "Should return 2 dogs from the database.");
    }

    @Test
    void testFindById() {
        User owner = createOwner(1L);
        Dog dog = createDog("Charlie", Sex.M, LocalDate.of(2019, 8, 1), owner);

        Dog createdDog = dao.create(dog);

        Dog foundDog = dao.findById(createdDog.getId());
        assertNotNull(foundDog, "Dog should be found by ID.");
        assertEquals(createdDog.getId(), foundDog.getId(), "Found dog's ID should match.");
    }

    @Test
    void testUpdateDog() {
        User owner = createOwner(1L);
        Dog dog = createDog("Daisy", Sex.F, LocalDate.of(2020, 11, 4), owner);

        Dog createdDog = dao.create(dog);


        createdDog.setName("Daisy Updated");
        dao.update(createdDog);


        Dog updatedDog = dao.findById(createdDog.getId());
        assertEquals("Daisy Updated", updatedDog.getName(), "Dog's name should be updated.");
    }

    @Test
    void testDeleteDog() {
        User owner = createOwner(1L);
        Dog dog = createDog("Rocky", Sex.M, LocalDate.of(2021, 3, 6), owner);

        Dog createdDog = dao.create(dog);


        dao.delete(createdDog.getId());

        assertThrows(RuntimeException.class, () -> dao.findById(createdDog.getId()), "Should throw an exception when fetching a deleted dog.");
    }

    // Helper method to create a User
    private User createOwner(Long id) {
        User owner = new User();
        owner.setId(id);
        return owner;
    }

    // Helper method to create a Dog
    private Dog createDog(String name, Sex sex, LocalDate birthDate, User owner) {
        Dog dog = new Dog();
        dog.setName(name);
        dog.setSex(sex);
        dog.setBirthDate(birthDate);
        dog.setOwner(owner);
        return dog;
    }

@Test
public void testGetDogsByOwnerId() {
    User owner = createOwner(1L);

    Dog dog1 = createDog("Buddy", Sex.M, LocalDate.of(2010, 5, 12), owner);
    Dog dog2 = createDog("Bella", Sex.F, LocalDate.of(2015, 5, 10), owner);
    dao.create(dog1);
    dao.create(dog2);

    List<Dog> dogs = dao.getDogsByOwnerId(owner.getId());

    assertFalse(dogs.isEmpty(), "Expected dogs for owner ID " + owner.getId());

    for (Dog dog : dogs) {
        assertEquals(owner.getId(), dog.getOwner().getId(), "Dog should belong to the specified owner.");
    }
}

    @AfterEach
    public void tearDown() {
        dao.deleteAll();
        clearTables();
    }
}