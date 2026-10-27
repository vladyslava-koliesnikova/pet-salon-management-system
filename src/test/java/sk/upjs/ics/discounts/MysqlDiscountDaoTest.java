package sk.upjs.ics.discounts;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcOperations;
import sk.upjs.ics.Factory;
import sk.upjs.ics.TestContainers;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MysqlDiscountDaoTest extends TestContainers {

    private MysqlDiscountDao dao;

    @BeforeEach
    void setUp() {
        JdbcOperations jdbcOperations = Factory.INSTANCE.getMysqlJdbcOperations();
        assertNotNull(jdbcOperations);
        dao = new MysqlDiscountDao(jdbcOperations);
        jdbcOperations.update("DELETE FROM orders_has_service_types");
        jdbcOperations.update("DELETE FROM orders");
        jdbcOperations.update("DELETE FROM discounts");
        dao.deleteAll();
    }

    @Test
    void testCreateDiscount() {
        Discount discount = createTestDiscount();

        Discount createdDiscount = dao.create(discount);

        assertNotNull(createdDiscount);
        assertNotNull(createdDiscount.getId());
        assertEquals(discount.getType(), createdDiscount.getType());
        assertEquals(discount.getDiscount_percent(), createdDiscount.getDiscount_percent());
    }

    @Test
    void testFindAll() {
        dao.deleteAll();

        dao.create(createTestDiscount("EASTER", 15.0, LocalDate.of(2024, 03, 20), LocalDate.of(2024, 04, 28)));
        dao.create(createTestDiscount("CHRISTMAS",25.0,LocalDate.of(2024, 12, 01),LocalDate.of(2024, 12, 25)));
        dao.create(createTestDiscount("NEW_YEAR", 20.0, LocalDate.of(2024, 12, 26), LocalDate.of(2025, 01, 02)));

        List<Discount> discounts = dao.findAll();

        assertNotNull(discounts);
        assertEquals(3, discounts.size());
        assertEquals("EASTER", discounts.get(0).getType().name());
        assertEquals("CHRISTMAS", discounts.get(1).getType().name());
        assertEquals("NEW_YEAR", discounts.get(2).getType().name());
    }

    @Test
    void testFindById() {
        Discount expectedDiscount = createTestDiscount("CHRISTMAS", 25.0, LocalDate.of(2024, 12, 01), LocalDate.of(2024, 12, 25));
        Discount createdDiscount = dao.create(expectedDiscount);
        System.out.println("Created discount ID: " + createdDiscount.getId());

        Discount foundDiscount = dao.findById(createdDiscount.getId());

        System.out.println("Found discount: " + foundDiscount);
        assertNotNull(foundDiscount, "Failed to find the discount by ID.");
        assertEquals(expectedDiscount, foundDiscount, "The found discount does not match the expected values.");

    }

    @Test
    void testLoadFromCsv() {
        // Load the CSV file using ClassLoader.getResource()
        var resourceUrl = getClass().getClassLoader().getResource("sk/upjs/ics/discounts.csv");

        // Ensure the CSV file was found
        assertNotNull(resourceUrl, "Resource discounts.csv not found in the classpath. Please make sure it exists in src/test/resources");

        // Convert the resource URL to a File object
        File file = new File(resourceUrl.getFile());

        // Use the DAO to load data from the CSV file into the database
        dao.loadFromCsv(file);

        // Verify that some records were loaded into the database
        var got = dao.findAll();
        assertTrue(got.size() > 0, "No records were loaded from the CSV file.");

    }

    // Helper Method: Creates a sample discount with default values
    private Discount createTestDiscount() {
        return createTestDiscount("EASTER", 15.0, LocalDate.of(2024, 3, 20), LocalDate.of(2024, 4, 28));
    }


    // Helper Method: Creates a discount with the provided parameters
    private Discount createTestDiscount(String type, double discount_percent, LocalDate startDate, LocalDate endDate) {
        Discount discount = new Discount();
        discount.setType(DiscountType.valueOf(type));
        discount.setDiscount_percent(discount_percent);
        discount.setStartDate(startDate);
        discount.setEndDate(endDate);
        return discount;
    }


}