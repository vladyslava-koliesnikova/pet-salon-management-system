package sk.upjs.ics.dogs;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import sk.upjs.ics.exceptions.NotFoundException;
import sk.upjs.ics.orders.Order;
import sk.upjs.ics.orders.OrderDao;
import sk.upjs.ics.users.User;
import sk.upjs.ics.users.UserDao;

import java.io.File;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

public class MysqlDogDao implements DogDao {
    private final JdbcOperations jdbcOperations;

    private final ResultSetExtractor<List<Dog>> resultSetExtractor = rs -> {
        var dogs = new ArrayList<Dog>();
        var processedDogs = new HashMap<Long, Dog>();
        var processedOwners = new HashMap<Long, User>();

        while (rs.next()) {
            var dogId = rs.getLong("dog_id");
            var dog = processedDogs.get(dogId);

            if (dog == null) {
                dog = Dog.fromResultSet(rs, "dog_");
                processedDogs.put(dogId, dog);
                dogs.add(dog);
            }

            var ownerId = rs.getLong("users_id");
            User owner = null;
            if (!rs.wasNull()) {
                owner = processedOwners.get(ownerId);
                if (owner == null) {
                    owner = User.fromResultSet(rs, "users_");
                    processedOwners.put(ownerId, owner);
                }
            }
            dog.setOwner(owner);

            var orderId = rs.getLong("ord_id");
            Order order = null;
            if (!rs.wasNull()) {
                order = Order.fromResultSet(rs, "ord_");
                if (order != null) {
                    dog.getOrders().add(order);
                }
            }
        }
        return dogs;
    };

    private final String selectDogQuery = "SELECT " +
            "us.id AS users_id, us.role AS users_role, us.first_name AS users_first_name, " +
            "us.last_name AS users_last_name, us.password AS users_password, us.email AS users_email, " +
            "us.birth_date AS users_birth_date, " +

            "dog.id AS dog_id, dog.name AS dog_name, dog.sex AS dog_sex, " +
            "dog.birth_date AS dog_birth_date, dog.users_id AS dog_users_id, " +

            "ord.id AS ord_id, ord.discounts_id AS ord_discounts_id, ord.date_time AS ord_date_time, " +
            "ord.dogs_id AS ord_dogs_id " +

            "FROM dogs dog " +
            "LEFT JOIN users us ON dog.users_id = us.id " +
            "LEFT JOIN orders ord ON dog.id = ord.dogs_id";


    public MysqlDogDao(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public int loadFromCsv(UserDao userDao, OrderDao orderDao, File file) {
        var memoryDao = new MemoryDogDao(new ArrayList<>());

        var numLoadedItems = memoryDao.loadFromCsv(userDao, orderDao, file);
        var dogs = memoryDao.findAll();

        for (var dog : dogs) {
            jdbcOperations.update(
                    "INSERT INTO dogs (id, name, sex, birth_date, users_id) VALUES (?, ?, ?, ?, ?)",
                    dog.getId(),
                    dog.getName(),
                    dog.getSex(),
                    dog.getBirthDate(),
                    dog.getOwner().getId()
            );
        }

        return numLoadedItems;
    }

    @Override
    public List<Dog> findAll() {
        return jdbcOperations.query(selectDogQuery, resultSetExtractor);
    }

    @Override
    public Dog create(Dog dog) {

        if (dog == null) {
            throw new IllegalArgumentException("Dog is null.");
        }

        if (dog.getId() != null) {
            throw new IllegalArgumentException("Dog id is already set.");
        }

        String sql = "INSERT INTO dogs (name, birth_date, sex, users_id) VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcOperations.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, dog.getName().toString());
            ps.setDate(2, Date.valueOf(dog.getBirthDate()));
            ps.setString(3, dog.getSex().toString());
            ps.setLong(4, dog.getOwner().getId());
            return ps;
        }, keyHolder);

        Number generatedId = keyHolder.getKey();
        if (generatedId != null) {
            dog.setId(generatedId.longValue());
        } else {
            throw new RuntimeException("Failed to retrieve generated key for User.");
        }

        return dog;
    }

    @Override
    public Dog update(Dog dog) {
        if (dog == null) {
            throw new IllegalArgumentException("Dog is null.");
        }

        if (dog.getId() == null) {
            throw new IllegalArgumentException("Dog id is not set.");
        }
        jdbcOperations.update(
                "UPDATE dogs SET name = ?, sex = ?, birth_date = ?, users_id = ? WHERE id = ?",
                dog.getName(),
                dog.getSex().toString(),
                dog.getBirthDate(),
                dog.getOwner().getId(),
                dog.getId()
        );
        return findById(dog.getId());
    }

    @Override
    public void delete(Long id) {
        jdbcOperations.update("DELETE FROM orders WHERE dogs_id = ?", id);
        jdbcOperations.update("DELETE FROM dogs WHERE id = ?", id);
    }

    public void deleteAll() {
        jdbcOperations.update("DELETE FROM orders_has_service_types");
        jdbcOperations.update("DELETE FROM orders");
        jdbcOperations.update("DELETE FROM dogs");

    }

    @Override
    public Dog findById(Long id) {
        var query = selectDogQuery + " WHERE dog.id = ?";
        var dogs = jdbcOperations.query(query, resultSetExtractor, id);
        if (dogs == null || dogs.isEmpty()) {
            throw new NotFoundException("Dog with id " + id + " not found");
        }
        return dogs.get(0);
    }

    @Override
    public List<Dog> getDogsByOwnerId(Long ownerId) {
        String sql = selectDogQuery + " WHERE dog.users_id = ?";
        return jdbcOperations.query(sql, resultSetExtractor, ownerId);
    }

}

