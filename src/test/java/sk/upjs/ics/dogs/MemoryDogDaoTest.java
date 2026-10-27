package sk.upjs.ics.dogs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import sk.upjs.ics.users.MemoryUserDao;
import sk.upjs.ics.orders.MemoryOrderDao;
import sk.upjs.ics.discounts.MemoryDiscountDao;
import sk.upjs.ics.servicetypes.MemoryServiceTypeDao;

import static org.junit.jupiter.api.Assertions.*;

class MemoryDogDaoTest {

    private MemoryDogDao dao;
    private List<Dog> storage;

    @BeforeEach
    void setUp() {
        storage = new ArrayList<>();
        dao = new MemoryDogDao(storage);
    }

    private void fillStorage() {
        var dog = new Dog();
        dog.setId(1L);
        storage.add(dog);

        dog = new Dog();
        dog.setId(2L);
        storage.add(dog);
    }

    @Test
    void fromResources() {
        var dao = new MemoryDogDao(new ArrayList<>());
        var userDao = new MemoryUserDao(new ArrayList<>());
        var discountDao = new MemoryDiscountDao(new ArrayList<>());
        var serviceTypeDao = new MemoryServiceTypeDao(new ArrayList<>());
        var orderDao = new MemoryOrderDao(new ArrayList<>());

        userDao.loadFromCsv(dao, new File(getClass().getResource("../users.csv").getFile()));
        discountDao.loadFromCsv(new File(getClass().getResource("../discounts.csv").getFile()));
        serviceTypeDao.loadFromCsv(new File(getClass().getResource("../serviceTypes.csv").getFile()));
        orderDao.loadFromCsv(serviceTypeDao, dao, discountDao, new File(getClass().getResource("../orders.csv").getFile()));

        dao.loadFromCsv(userDao, orderDao, new File(getClass().getResource("../dogs.csv").getFile()));

        var got = dao.findAll();
        assertNotEquals(0, got.size());
    }

    @Test
    void findAll() {
        assertTrue(dao.findAll().isEmpty());

        fillStorage();

        var got = dao.findAll();
        assertFalse(got.isEmpty());
        assertEquals(2, got.size());
        assertEquals(1L, got.get(0).getId());
        assertEquals(2L, got.get(1).getId());
    }

    @Test
    void create() {
        assertThrows(IllegalArgumentException.class, () -> dao.create(null));

        var dog = new Dog();
        dog.setId(1L);

        assertThrows(IllegalArgumentException.class, () -> dao.create(dog));
        assertTrue(storage.isEmpty());

        dog.setId(null);
        dao.create(dog);

        assertFalse(storage.isEmpty());
        assertEquals(1, storage.size());
        assertEquals(1L, storage.get(0).getId());
    }

    @Test
    void update() {
        assertThrows(IllegalArgumentException.class, () -> dao.update(null));
        assertThrows(IllegalArgumentException.class, () -> dao.update(new Dog()));

        var dog = new Dog();
        dog.setId(1L);
        storage.add(dog);

        dog = new Dog();
        dog.setId(1L);

        dao.update(dog);

        assertEquals(1, storage.size());
        assertEquals(1L, storage.get(0).getId());
    }

    @Test
    void delete() {
        assertTrue(dao.findAll().isEmpty());
        fillStorage();
        assertFalse(dao.findAll().isEmpty());
        dao.delete(1L);
        assertEquals(1, dao.findAll().size());
        dao.delete(2L);
        assertTrue(dao.findAll().isEmpty());
    }
}