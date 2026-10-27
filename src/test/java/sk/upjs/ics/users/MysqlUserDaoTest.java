package sk.upjs.ics.users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.upjs.ics.Factory;
import sk.upjs.ics.TestContainers;
import sk.upjs.ics.dogs.DogDao;
import sk.upjs.ics.exceptions.NotFoundException;

import java.io.File;
import java.time.LocalDate;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

class MysqlUserDaoTest extends TestContainers {

    private UserDao dao;
    private DogDao dogDao;

    @BeforeEach
    void setUp() {
        clearTables();

        dao = Factory.INSTANCE.getUserDao();
        dogDao = Factory.INSTANCE.getDogDao();

    }

    @Test
    void loadFromCsv() {
        //clearTables();

        File csvFile = new File(getClass().getResource("../users.csv").getFile());
        assertNotNull(csvFile, "CSV file must be available for testing.");

        dao.loadFromCsv(dogDao, csvFile);

        var got = dao.findAll();
        assertNotEquals(0, got.size(), "The database should contain users from the CSV file.");
        for (User user : got) {
            assertNotNull(user.getId());
            assertNotNull(user.getName());
            assertNotNull(user.getSurname());
            assertNotNull(user.getPassword());
            assertNotNull(user.getRole());
            assertNotNull(user.getEmail());
            assertNotNull(user.getBirthDate());
            //assertNotEquals(Role.COSTUMER, user.getRole());
        }
    }


    @Test
    void findAll() {
        clearTables();
        for (int i = 1; i <= 10; i++) {
            User user = new User();
            user.setName("User" + i);
            user.setSurname("Surname" + i);
            user.setPassword("password" + i);
            user.setRole(Role.COSTUMER);
            user.setEmail("user" + i + "@example.com");
            user.setBirthDate(LocalDate.of(1990, 1, 1));
            dao.create(user);
        }
        var got = dao.findAll()
                .stream()
                .sorted(Comparator.comparing(User::getId))
                .toList();

        assertFalse(got.isEmpty());
        assertEquals(10, got.size());

        for (User user : got) {
            assertNotNull(user);
            assertNotNull(user.getId());
            assertNotNull(user.getName());
            assertNotNull(user.getSurname());
            assertNotNull(user.getPassword());
            assertNotNull(user.getRole());
            assertNotNull(user.getEmail());
            assertNotNull(user.getBirthDate());
        }
    }


    @Test
    void findById() {
        User user = new User();
        user.setName("John");
        user.setSurname("Doe");
        user.setPassword("securepass");
        user.setRole(Role.COSTUMER);
        user.setEmail("john.doe@example.com");
        user.setBirthDate(LocalDate.parse("1990-05-12"));

        User savedUser = dao.create(user);

        var got = dao.findById(savedUser.getId());
        assertNotNull(got);
        assertEquals(savedUser.getId(), got.getId());
    }


}


