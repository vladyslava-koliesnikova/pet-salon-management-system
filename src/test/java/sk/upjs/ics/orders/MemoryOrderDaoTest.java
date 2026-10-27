package sk.upjs.ics.orders;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.upjs.ics.discounts.MemoryDiscountDao;
import sk.upjs.ics.dogs.MemoryDogDao;
import sk.upjs.ics.servicetypes.MemoryServiceTypeDao;
import sk.upjs.ics.users.MemoryUserDao;
import sk.upjs.ics.users.User;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MemoryOrderDaoTest {

    private MemoryOrderDao dao;
    private List<Order> storage;

    @BeforeEach
    void setUp() {
        storage = new ArrayList<>();
        dao = new MemoryOrderDao(storage);
    }

    private void fillStorage() {
        var order = new Order();
        order.setId(1L);
        order.setDateTime(LocalDateTime.parse("2023-10-01T12:00"));
        storage.add(order);

        order = new Order();
        order.setId(2L);
        order.setDateTime(LocalDateTime.parse("2024-06-14T07:00"));

        storage.add(order);
    }

    @Test
    void fromResources() {
        var dao = new MemoryOrderDao(new ArrayList<>());
        var dogDao = new MemoryDogDao(new ArrayList<>());
        var userDao = new MemoryUserDao(new ArrayList<>());
        var discountDao = new MemoryDiscountDao(new ArrayList<>());
        var serviceTypeDao = new MemoryServiceTypeDao(new ArrayList<>());

        discountDao.loadFromCsv(new File(getClass().getResource("../discounts.csv").getFile()));
        serviceTypeDao.loadFromCsv(new File(getClass().getResource("../serviceTypes.csv").getFile()));
        dogDao.loadFromCsv(userDao, dao, new File(getClass().getResource("../dogs.csv").getFile()));
        userDao.loadFromCsv(dogDao, new File(getClass().getResource("../users.csv").getFile()));

        dao.loadFromCsv(serviceTypeDao, dogDao, discountDao, new File(getClass().getResource("../orders.csv").getFile()));

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
    void findAllSortedByDate() {
        assertTrue(dao.findAllSortedByDate().isEmpty());

        fillStorage();

        var got = dao.findAllSortedByDate();
        assertFalse(got.isEmpty());
        assertEquals(2, got.size());
        assertEquals(2L, got.get(0).getId());
        assertEquals(1L, got.get(1).getId());
    }

    @Test
    void create() {
        assertThrows(IllegalArgumentException.class, () -> dao.create(null));

        var order = new Order();
        order.setId(1L);
        order.setDateTime(LocalDateTime.parse("2023-10-01T12:00"));

        assertThrows(IllegalArgumentException.class, () -> dao.create(order));
        assertTrue(storage.isEmpty());

        order.setId(null);
        dao.create(order);

        assertFalse(storage.isEmpty());
        assertEquals(1, storage.size());
        assertEquals(1L, storage.get(0).getId());
    }

    @Test
    void update() {
        assertThrows(IllegalArgumentException.class, () -> dao.update(null));
        assertThrows(IllegalArgumentException.class, () -> dao.update(new Order()));

        var order = new Order();
        order.setId(1L);
        order.setDateTime(LocalDateTime.parse("2023-10-01T12:00"));
        storage.add(order);

        order = new Order();
        order.setId(1L);
        order.setDateTime(LocalDateTime.parse("2024-06-14T07:00"));

        dao.update(order);

        assertEquals(1, storage.size());
        assertEquals(1L, storage.get(0).getId());
        assertEquals(LocalDateTime.parse("2024-06-14T07:00"), storage.get(0).getDateTime());
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