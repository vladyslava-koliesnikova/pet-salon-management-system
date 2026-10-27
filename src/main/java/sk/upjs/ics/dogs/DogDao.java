package sk.upjs.ics.dogs;

import sk.upjs.ics.orders.OrderDao;
import sk.upjs.ics.users.UserDao;

import java.io.File;
import java.util.List;

public interface DogDao {
    int loadFromCsv(UserDao userDao, OrderDao orderDao, File file);

    List<Dog> findAll();

    Dog create(Dog dog);

    Dog update(Dog dog);

    void delete(Long id);

    Dog findById(Long id);

    List<Dog> getDogsByOwnerId(Long ownerId);
}
