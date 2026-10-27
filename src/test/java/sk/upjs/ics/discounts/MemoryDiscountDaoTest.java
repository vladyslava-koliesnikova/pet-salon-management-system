package sk.upjs.ics.discounts;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MemoryDiscountDaoTest {

    private MemoryDiscountDao dao;
    private List<Discount> storage;

    @BeforeEach
    void setUp() {
        storage = new ArrayList<>();
        dao = new MemoryDiscountDao(storage);
    }

    private void fillStorage() {
        var discount = new Discount();
        discount.setId(1L);
        storage.add(discount);

        discount = new Discount();
        discount.setId(2L);
        storage.add(discount);
    }

    @Test
    void fromResources() {
        // Load `discount.csv` file
        var resourceUrl = getClass().getClassLoader().getResource("sk/upjs/ics/discounts.csv");

        assertNotNull(resourceUrl, "Resource discount.csv not found in the classpath.");

        File csvFile = new File(resourceUrl.getFile());
        dao.loadFromCsv(csvFile);

        // Verify records were loaded
        var got = dao.findAll();
        assertTrue(got.size() > 0, "No records were loaded from the CSV file.");
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