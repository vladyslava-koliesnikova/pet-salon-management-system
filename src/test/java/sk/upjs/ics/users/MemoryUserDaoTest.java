package sk.upjs.ics.users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.upjs.ics.discounts.MemoryDiscountDao;
import sk.upjs.ics.dogs.Dog;
import sk.upjs.ics.dogs.MemoryDogDao;
import sk.upjs.ics.orders.MemoryOrderDao;
import sk.upjs.ics.servicetypes.MemoryServiceTypeDao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MemoryUserDaoTest {
    private MemoryUserDao dao;
    private List<User> storage;

    @BeforeEach
    void setUp() {
        storage = new ArrayList<>();
        dao = new MemoryUserDao(storage);
    }

    private void fillStorage() {
        var user = new User();
        user.setId(1L);
        storage.add(user);

        user = new User();
        user.setId(2L);
        storage.add(user);
    }

    @Test
    void fromResources() {
        var dao = new MemoryUserDao(new ArrayList<>());
        var dogDao = new MemoryDogDao(new ArrayList<>());
        var discountDao = new MemoryDiscountDao(new ArrayList<>());
        var serviceTypeDao = new MemoryServiceTypeDao(new ArrayList<>());
        var orderDao = new MemoryOrderDao(new ArrayList<>());

        discountDao.loadFromCsv(new File(getClass().getResource("../discounts.csv").getFile()));
        serviceTypeDao.loadFromCsv(new File(getClass().getResource("../serviceTypes.csv").getFile()));
        orderDao.loadFromCsv(serviceTypeDao, dogDao, discountDao, new File(getClass().getResource("../orders.csv").getFile()));
        dogDao.loadFromCsv(dao, orderDao, new File(getClass().getResource("../dogs.csv").getFile()));

        dao.loadFromCsv(dogDao, new File(getClass().getResource("../users.csv").getFile()));

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

        var user = new User();
        user.setId(1L);

        assertThrows(IllegalArgumentException.class, () -> dao.create(user));
        assertTrue(storage.isEmpty());

        user.setId(null);
        dao.create(user);

        assertFalse(storage.isEmpty());
        assertEquals(1, storage.size());
        assertEquals(1L, storage.get(0).getId());
    }

    @Test
    void update() {
        assertThrows(IllegalArgumentException.class, () -> dao.update(null));
        assertThrows(IllegalArgumentException.class, () -> dao.update(new User()));

        var user = new User();
        user.setId(1L);
        storage.add(user);

        user = new User();
        user.setId(1L);

        dao.update(user);

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