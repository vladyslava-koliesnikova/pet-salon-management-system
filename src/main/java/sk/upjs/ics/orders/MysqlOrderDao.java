
package sk.upjs.ics.orders;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import sk.upjs.ics.discounts.Discount;
import sk.upjs.ics.discounts.DiscountDao;
import sk.upjs.ics.dogs.Dog;
import sk.upjs.ics.dogs.DogDao;
import sk.upjs.ics.servicetypes.ServiceType;
import sk.upjs.ics.servicetypes.ServiceTypeDao;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

public class MysqlOrderDao implements OrderDao {

    private final JdbcOperations jdbcOperations;

    public MysqlOrderDao(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    private final String selectOrdersQuery =
            "SELECT o.id, o.date_time, o.dogs_id, o.discounts_id, " +
                    "dog.id AS dog_id, dog.name AS dog_name, " +
                    "dog.sex AS dog_sex, dog.birth_date AS dog_birth_date, " +
                    "dog.users_id AS dog_users_id, " +
                    "di.id AS di_id, di.name AS di_name, " +
                    "di.discount_percent AS di_discount_percent, " +
                    "di.start_date AS di_start_date, di.end_date AS di_end_date, " +
                    "st.id AS st_id, st.name AS st_name, st.price AS st_price " +
                    "FROM orders o " +
                    "LEFT JOIN dogs dog ON dog.id = o.dogs_id " +
                    "LEFT JOIN orders_has_service_types ohst " +
                    "ON o.id = ohst.orders_id " +
                    "LEFT JOIN discounts di ON di.id = o.discounts_id " +
                    "LEFT JOIN service_types st " +
                    "ON st.id = ohst.service_types_id";

    private final ResultSetExtractor<List<Order>> resultSetExtractor = rs -> {

        Map<Long, Order> processedOrders = new LinkedHashMap<>();
        Map<Long, ServiceType> processedServices = new HashMap<>();
        Map<Long, Dog> processedDogs = new HashMap<>();
        Map<Long, Discount> processedDiscounts = new HashMap<>();

        while (rs.next()) {

            Long orderId = rs.getLong("id");

            Order order = processedOrders.get(orderId);

            if (order == null) {
                order = Order.fromResultSet(rs);
                processedOrders.put(orderId, order);
            }

            // Load service
            Long serviceId = rs.getObject("st_id", Long.class);

            if (serviceId != null) {
                ServiceType service = processedServices.get(serviceId);

                if (service == null) {
                    service = ServiceType.fromResultSet(rs, "st_");
                    processedServices.put(serviceId, service);
                }

                if (service != null) {
                    order.getServices().add(service);
                }
            }

            // Load dog
            Long dogId = rs.getObject("dog_id", Long.class);

            if (dogId != null) {
                Dog dog = processedDogs.get(dogId);

                if (dog == null) {
                    dog = Dog.fromResultSet(rs, "dog_");
                    if (dog != null) {
                        processedDogs.put(dogId, dog);
                    }
                }

                order.setDog(dog);
            } else {
                order.setDog(null);
            }

            // Load discount
            Long discountId = rs.getObject("di_id", Long.class);

            if (discountId != null) {
                Discount discount = processedDiscounts.get(discountId);

                if (discount == null) {
                    discount = Discount.fromResultSet(rs, "di_");
                    if (discount != null) {
                        processedDiscounts.put(discountId, discount);
                    }
                }

                order.setDiscount(discount);
            } else {
                order.setDiscount(null);
            }
        }

        return new ArrayList<>(processedOrders.values());
    };

    @Override
    public int loadFromCsv(
            ServiceTypeDao serviceTypeDao,
            DogDao dogDao,
            DiscountDao discountDao,
            File file
    ) {
        var memoryDao = new MemoryOrderDao(new ArrayList<>());

        int count = memoryDao.loadFromCsv(
                serviceTypeDao, dogDao, discountDao, file
        );

        for (Order order : memoryDao.findAll()) {

            jdbcOperations.update(
                    "INSERT INTO orders " +
                            "(id, discounts_id, date_time, dogs_id) " +
                            "VALUES (?, ?, ?, ?)",
                    order.getId(),
                    order.getDiscount() != null
                            ? order.getDiscount().getId() : null,
                    order.getDateTime(),
                    order.getDog() != null
                            ? order.getDog().getId() : null
            );

            saveServices(order.getId(), order.getServices());
        }

        return count;
    }

    @Override
    public List<Order> findAll() {
        return jdbcOperations.query(
                selectOrdersQuery,
                resultSetExtractor
        );
    }

    @Override
    public List<Order> findAllSortedByDate() {
        return jdbcOperations.query(
                selectOrdersQuery + " ORDER BY o.date_time DESC",
                resultSetExtractor
        );
    }

    @Override
    public Order findById(Long id) {
        List<Order> orders = jdbcOperations.query(
                selectOrdersQuery + " WHERE o.id = ?",
                resultSetExtractor,
                id
        );

        return orders.isEmpty() ? null : orders.get(0);
    }

    @Override
    public Order create(Order order) {

        if (order == null) {
            throw new IllegalArgumentException("Order is null.");
        }

        if (order.getId() != null) {
            throw new IllegalArgumentException(
                    "Order id is already set."
            );
        }

        var keyHolder = new GeneratedKeyHolder();

        jdbcOperations.update(connection -> {

            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO orders " +
                            "(discounts_id, date_time, dogs_id) " +
                            "VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );

            ps.setObject(
                    1,
                    order.getDiscount() != null
                            ? order.getDiscount().getId() : null
            );

            ps.setObject(2, order.getDateTime());

            ps.setObject(
                    3,
                    order.getDog() != null
                            ? order.getDog().getId() : null
            );

            return ps;

        }, keyHolder);

        Number generatedKey = keyHolder.getKey();

        if (generatedKey == null) {
            throw new IllegalStateException(
                    "Failed to generate order ID."
            );
        }

        Long id = generatedKey.longValue();

        saveServices(id, order.getServices());

        return findById(id);
    }

    @Override
    public Order update(Order order) {

        if (order == null) {
            throw new IllegalArgumentException("Order is null.");
        }

        if (order.getId() == null) {
            throw new IllegalArgumentException(
                    "Order id is not set."
            );
        }

        jdbcOperations.update(
                "UPDATE orders SET " +
                        "date_time = ?, dogs_id = ?, discounts_id = ? " +
                        "WHERE id = ?",
                order.getDateTime(),
                order.getDog() != null
                        ? order.getDog().getId() : null,
                order.getDiscount() != null
                        ? order.getDiscount().getId() : null,
                order.getId()
        );

        jdbcOperations.update(
                "DELETE FROM orders_has_service_types " +
                        "WHERE orders_id = ?",
                order.getId()
        );

        saveServices(order.getId(), order.getServices());

        return findById(order.getId());
    }

    private void saveServices(Long orderId, Set<ServiceType> services) {

        if (services == null) {
            return;
        }

        for (ServiceType service : services) {

            if (service == null) {
                throw new IllegalArgumentException(
                        "Service or service ID is null."
                );
            }

            jdbcOperations.update(
                    "INSERT INTO orders_has_service_types " +
                            "(orders_id, service_types_id) VALUES (?, ?)",
                    orderId,
                    service.getId()
            );
        }
    }

    @Override
    public void delete(Long id) {

        jdbcOperations.update(
                "DELETE FROM orders_has_service_types " +
                        "WHERE orders_id = ?",
                id
        );

        jdbcOperations.update(
                "DELETE FROM orders WHERE id = ?",
                id
        );
    }

    @Override
    public List<Order> getUpcomingVisits(Long userId) {

        return jdbcOperations.query(
                selectOrdersQuery +
                        " WHERE o.date_time > NOW() " +
                        "AND dog.users_id = ? " +
                        "ORDER BY o.date_time ASC",
                resultSetExtractor,
                userId
        );
    }

    @Override
    public List<Order> getPastVisits(Long userId) {

        return jdbcOperations.query(
                selectOrdersQuery +
                        " WHERE o.date_time <= NOW() " +
                        "AND dog.users_id = ? " +
                        "ORDER BY o.date_time DESC",
                resultSetExtractor,
                userId
        );
    }

    @Override
    public List<Order> getAllOrders() {

        return jdbcOperations.query(
                selectOrdersQuery + " ORDER BY o.date_time ASC",
                resultSetExtractor
        );
    }

    @Override
    public List<Order> getAllPastBookings() {

        return jdbcOperations.query(
                selectOrdersQuery +
                        " WHERE o.date_time <= NOW() " +
                        "ORDER BY o.date_time DESC",
                resultSetExtractor
        );
    }

    @Override
    public List<Order> getAllUpcomingBookings() {

        return jdbcOperations.query(
                selectOrdersQuery +
                        " WHERE o.date_time > NOW() " +
                        "ORDER BY o.date_time ASC",
                resultSetExtractor
        );
    }

    @Override
    public List<Order> getOrdersByDate(Date date) {

        String query =
                "SELECT o.id, o.date_time, o.dogs_id, o.discounts_id " +
                        "FROM orders o " +
                        "WHERE DATE(o.date_time) = ? " +
                        "ORDER BY o.date_time ASC";

        return jdbcOperations.query(
                query,
                (rs, rowNum) -> Order.fromResultSet(rs),
                new java.sql.Date(date.getTime())
        );
    }
}
