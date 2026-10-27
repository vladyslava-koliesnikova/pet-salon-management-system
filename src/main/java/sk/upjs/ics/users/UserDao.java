package sk.upjs.ics.users;

import sk.upjs.ics.dogs.DogDao;

import java.io.File;
import java.util.List;

public interface UserDao {
    int loadFromCsv(DogDao dogDao, File file);

    List<User> findAll();

    User create(User user);

    User update(User user);

    void delete(Long id);

    User findByEmail(String email);

    User findById(Long id);

    List<User> getAllClients();

    boolean authenticate(String password, User user);
}
