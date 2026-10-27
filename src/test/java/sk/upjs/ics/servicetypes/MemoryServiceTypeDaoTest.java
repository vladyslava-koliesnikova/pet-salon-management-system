package sk.upjs.ics.servicetypes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.upjs.ics.discounts.Discount;
import sk.upjs.ics.discounts.MemoryDiscountDao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MemoryServiceTypeDaoTest {

    private MemoryServiceTypeDao dao;
    private List<ServiceType> storage;

    @BeforeEach
    void setUp() {
        storage = new ArrayList<>();
        dao = new MemoryServiceTypeDao(storage);
    }

    private void fillStorage() {
        var serviceType = new ServiceType();
        serviceType.setId(1L);
        storage.add(serviceType);

        serviceType = new ServiceType();
        serviceType.setId(2L);
        storage.add(serviceType);
    }

    @Test
    void fromResources() {
        var dao = new MemoryServiceTypeDao(new ArrayList<>());
        dao.loadFromCsv(new File(getClass().getResource("../serviceTypes.csv").getFile()));

        var got = dao.findAll();
        assertNotEquals(0, got.size());
    }

    @Test
    void findAll() {
        assertTrue(dao.findAll().isEmpty());

        fillStorage();

        var got = dao.findAll();
        assertEquals(2, got.size());
        assertEquals(1L, got.get(0).getId());
        assertEquals(2L, got.get(1).getId());
    }
}