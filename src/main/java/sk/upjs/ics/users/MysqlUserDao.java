package sk.upjs.ics.users;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import sk.upjs.ics.dogs.Dog;
import sk.upjs.ics.dogs.DogDao;
import sk.upjs.ics.exceptions.NotFoundException;

import java.io.File;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MysqlUserDao implements UserDao {

    private final JdbcOperations jdbcOperations;

    private final ResultSetExtractor<List<User>> resultSetExtractor = rs -> {
        var users = new ArrayList<User>();
        var processed = new HashMap<Long, User>();

        while (rs.next()) {
            var id = rs.getLong("id");
            var user = processed.get(id);

            if (user == null) {
                user = User.fromResultSet(rs);
                processed.put(id, user);
                users.add(user);
            }

            var dog = Dog.fromResultSet(rs, "dog_");
            if (dog != null) {
                user.getDogs().add(dog);
            }
        }

        return users;
    };

    private final String selectUserQuery = "SELECT us.id, us.role, us.first_name, us.last_name, us.password, us.email, us.birth_date, " +
            "dog.id as dog_id, dog.name as dog_name, dog.sex as dog_sex, dog.birth_date as dog_birth_date, dog.users_id as dog_users_id " +
            "FROM users us " +
            "LEFT JOIN dogs dog ON dog.users_id = us.id";

    private static final String INSERT_USER_QUERY =
            "INSERT INTO users (role, first_name, last_name, password, email, birth_date) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_USER_QUERY =
            "UPDATE users SET role = ?, first_name = ?, last_name = ?, password = ?, email = ?, birth_date = ? WHERE id = ?";
    private static final String DELETE_USER_QUERY =
            "DELETE FROM users WHERE id = ?";
    private static final String DELETE_DOG_QUERY =
            "DELETE FROM dogs WHERE users_id = ?";

    public MysqlUserDao(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public int loadFromCsv(DogDao dogDao, File file) {
        var memoryDao = new MemoryUserDao(new ArrayList<>());
        int loadedCount = memoryDao.loadFromCsv(dogDao, file);

        for (User user : memoryDao.findAll()) {
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                throw new IllegalArgumentException("Password cannot be empty for user " + user.getName() + " " + user.getSurname());
            }

            jdbcOperations.update(
                    INSERT_USER_QUERY,
                    user.getRole().toString(),
                    user.getName(),
                    user.getSurname(),
                    user.getPassword(),
                    user.getEmail(),
                    Date.valueOf(user.getBirthDate())
            );
            if (user.getDogs() != null && !user.getDogs().isEmpty()) {
                for (Dog dog : user.getDogs()) {
                    dogDao.create(dog);
                }
            }
        }
        return loadedCount;
    }

    @Override
    public List<User> findAll() {
        return jdbcOperations.query(selectUserQuery, resultSetExtractor);
    }

    @Override
    public User create(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User is null.");
        }

        if (user.getId() != null) {
            throw new IllegalArgumentException("User id is already set.");
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty.");
        }
        String sql = "INSERT INTO users (role, first_name, last_name, password, email, birth_date) VALUES (?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcOperations.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getRole().toString());
            ps.setString(2, user.getName());
            ps.setString(3, user.getSurname());
            ps.setString(4, user.getPassword());
            ps.setString(5, user.getEmail());
            ps.setDate(6, Date.valueOf(user.getBirthDate()));
            return ps;
        }, keyHolder);

        Number generatedId = keyHolder.getKey();
        if (generatedId != null) {
            user.setId(generatedId.longValue());
        } else {
            throw new RuntimeException("Failed to retrieve generated key for User.");
        }

        return user;
    }

    @Override
    public User update(User user) {
        int updatedRows = jdbcOperations.update(
                UPDATE_USER_QUERY,
                user.getRole().toString(),
                user.getName(),
                user.getSurname(),
                user.getPassword(),
                user.getEmail(),
                Date.valueOf(user.getBirthDate()),
                user.getId()
        );

        if (updatedRows == 0) {
            throw new NotFoundException("User with id " + user.getId() + " not found.");
        }
        return findById(user.getId());
    }

    @Override
    public void delete(Long id) {
        int deletedRows = jdbcOperations.update(DELETE_USER_QUERY, id);
        jdbcOperations.update(DELETE_DOG_QUERY, id);
        if (deletedRows == 0) {
            throw new NotFoundException("User with id " + id + " not found.");
        }
    }

    @Override
    public User findById(Long id) {
        var users = jdbcOperations.query(
                selectUserQuery + " WHERE us.id = ?",
                resultSetExtractor,
                id
        );
        if (users == null || users.isEmpty()) {
            throw new NotFoundException("User with id " + id + " not found.");
        }
        return users.get(0);
    }

    @Override
    public List<User> getAllClients() {
        return jdbcOperations.query(
                selectUserQuery + " WHERE us.role = ?",
                resultSetExtractor,
                Role.COSTUMER.toString()
        );
    }

    @Override
    public User findByEmail(String email) {
        var query = selectUserQuery + " WHERE us.email = ?";
        var users = jdbcOperations.query(query, resultSetExtractor, email);
        if (users == null || users.isEmpty()) {
            return null;
        }
        return users.get(0);
    }

    @Override
    public boolean authenticate(String password, User user) {
        return BCrypt.checkpw(password, user.getPassword());
    }

}

