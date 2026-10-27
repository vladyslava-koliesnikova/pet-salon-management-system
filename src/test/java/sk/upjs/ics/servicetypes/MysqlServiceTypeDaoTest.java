package sk.upjs.ics.servicetypes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcOperations;
import sk.upjs.ics.Factory;
import sk.upjs.ics.TestContainers;

import static org.junit.jupiter.api.Assertions.*;

public class MysqlServiceTypeDaoTest extends TestContainers {
    private static final long TEST_SERVICE_TYPE_ID = 1L;
    private MysqlServiceTypeDao dao;
    private JdbcOperations jdbcOperations;

    @BeforeEach
    void setUp() {
        jdbcOperations = Factory.INSTANCE.getMysqlJdbcOperations();
        dao = new MysqlServiceTypeDao(jdbcOperations);
    }

    @Test
    void findAll() {
        var serviceTypes = dao.findAll();
        assertFalse(serviceTypes.isEmpty());
        assertEquals(3, serviceTypes.size());
    }

    @Test
    void findById() {
        var serviceType = dao.findById(TEST_SERVICE_TYPE_ID);
        assertServiceType(serviceType, TEST_SERVICE_TYPE_ID, "Grooming", 25.00);
    }

    private void assertServiceType(ServiceType serviceType, long expectedId, String expectedName, double expectedPrice) {
        assertNotNull(serviceType);
        assertEquals(expectedId, serviceType.getId());
        assertEquals(expectedName, serviceType.getName());
        assertEquals(expectedPrice, serviceType.getPrice());
    }
}

