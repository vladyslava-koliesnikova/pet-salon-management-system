package sk.upjs.ics.orders;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcOperations;
import sk.upjs.ics.Factory;
import sk.upjs.ics.TestContainers;
import sk.upjs.ics.discounts.Discount;
import sk.upjs.ics.dogs.Dog;
import sk.upjs.ics.users.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MysqlOrderDaoTest extends TestContainers {
    private MysqlOrderDao dao;

    @BeforeEach
    void setUp() {
        JdbcOperations jdbcOperations = Factory.INSTANCE.getMysqlJdbcOperations();
        dao = new MysqlOrderDao(jdbcOperations);
        jdbcOperations.update("DELETE FROM orders_has_service_types");
        jdbcOperations.update("DELETE FROM orders");
    }

    @Test
    void testCreateOrder() {
        Order order = new Order();
        LocalDateTime expectedDateTime = LocalDateTime.of(2024, 12, 25, 10, 0);
        order.setDateTime(expectedDateTime);

        Dog dog = new Dog();
        dog.setId(1L);
        order.setDog(dog);

        Discount discount = new Discount();
        discount.setId(1L);
        order.setDiscount(discount);
        Order createdOrder = dao.create(order);
        assertNotNull(createdOrder.getId(), "The created order ID should not be null");
        assertEquals(expectedDateTime, createdOrder.getDateTime(), "The date-time of created order should match");
    }

    @Test
    void testFindAll() {
        Order order1 = new Order();
        order1.setDateTime(LocalDateTime.of(2024, 12, 25, 10, 0));
        Dog dog1 = new Dog();
        dog1.setId(1L);
        order1.setDog(dog1);
        Discount discount1 = new Discount();
        discount1.setId(1L);
        order1.setDiscount(discount1);
        dao.create(order1);

        Order order2 = new Order();
        order2.setDateTime(LocalDateTime.of(2024, 12, 26, 10, 0));
        Dog dog2 = new Dog();
        dog2.setId(2L);
        order2.setDog(dog2);
        Discount discount2 = new Discount();
        discount2.setId(2L);
        order2.setDiscount(discount2);
        dao.create(order2);
        List<Order> orders = dao.findAll();
        assertEquals(2, orders.size(), "There should be exactly 2 orders in the database");
    }

    @Test
    void testFindById() {
        Order order = new Order();
        order.setDateTime(LocalDateTime.of(2024, 12, 25, 10, 0));
        Dog dog = new Dog();
        dog.setId(1L);
        order.setDog(dog);
        Discount discount = new Discount();
        discount.setId(1L);
        order.setDiscount(discount);
        Order createdOrder = dao.create(order);

        Order foundOrder = dao.findById(createdOrder.getId());

        assertNotNull(foundOrder, "The found order should not be null");
        assertEquals(createdOrder.getId(), foundOrder.getId(), "The IDs of the created and found orders should match");
    }

    @Test
    void testUpdateOrder() {
        Order order = new Order();
        LocalDateTime initialDateTime = LocalDateTime.of(2024, 12, 24, 10, 0);
        order.setDateTime(initialDateTime);

        Dog dog = new Dog();
        dog.setId(1L);
        order.setDog(dog);

        Discount discount = new Discount();
        discount.setId(1L);
        order.setDiscount(discount);

        Order createdOrder = dao.create(order);
        LocalDateTime updatedDateTime = LocalDateTime.of(2024, 12, 25, 10, 0);

        createdOrder.setDateTime(updatedDateTime);
        dao.update(createdOrder);

        Order updatedOrder = dao.findById(createdOrder.getId());
        assertNotNull(updatedOrder, "The updated order should still exist in the database");
        assertEquals(updatedDateTime, updatedOrder.getDateTime(), "The date-time of the order should be updated");
    }

    @Test
    void testDeleteOrder() {

        Order order = new Order();
        order.setDateTime(LocalDateTime.of(2024, 12, 24, 10, 0));

        Dog dog = new Dog();
        dog.setId(1L);
        order.setDog(dog);

        Discount discount = new Discount();
        discount.setId(1L);
        order.setDiscount(discount);

        Order createdOrder = dao.create(order);


        dao.delete(createdOrder.getId());
        Order deletedOrder = dao.findById(createdOrder.getId());


        assertNull(deletedOrder, "The deleted order should no longer exist in the database");
    }

    @Test
    public void testGetUpcomingVisits() {
        long userId = 1;

        User user = new User();
        user.setId(userId);

        Order upcomingOrder = new Order();
        upcomingOrder.setDateTime(LocalDateTime.now().plusDays(1));

        Dog dog = new Dog();
        dog.setId(1L);
        dog.setOwner(user);
        upcomingOrder.setDog(dog);

        Discount discount = new Discount();
        discount.setId(1L);
        upcomingOrder.setDiscount(discount);

        dao.create(upcomingOrder);

        List<Order> upcomingVisits = dao.getUpcomingVisits(userId);

        assertNotNull(upcomingVisits, "Upcoming visits should not be null");
        assertFalse(upcomingVisits.isEmpty(), "Expected upcoming visits for user ID " + userId);

        for (Order order : upcomingVisits) {
            assertTrue(order.getDateTime().isAfter(LocalDateTime.now()), "Order date and time should be in the future");
        }
    }

    @Test
    public void testGetPastVisits() {
        long userId = 1;
        User user = new User();
        user.setId(userId);
        Order pastOrder = new Order();
        pastOrder.setDateTime(LocalDateTime.now().minusDays(1));

        Dog dog = new Dog();
        dog.setId(1L);
        dog.setOwner(user);
        pastOrder.setDog(dog);

        Discount discount = new Discount();
        discount.setId(1L);
        pastOrder.setDiscount(discount);

        dao.create(pastOrder);

        List<Order> pastVisits = dao.getPastVisits(userId);

        assertNotNull(pastVisits, "Past visits should not be null");
        assertFalse(pastVisits.isEmpty(), "Expected past visits for user ID " + userId);

        for (Order order : pastVisits) {
            assertTrue(order.getDateTime().isBefore(LocalDateTime.now()), "Order date should be in the past");
        }
    }
}
