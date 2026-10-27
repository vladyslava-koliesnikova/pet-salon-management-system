package sk.upjs.ics.orders;

import sk.upjs.ics.discounts.DiscountDao;
import sk.upjs.ics.dogs.DogDao;
import sk.upjs.ics.servicetypes.ServiceTypeDao;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Set;

public interface OrderDao {
    int loadFromCsv(ServiceTypeDao serviceTypeDao, DogDao dogDao, DiscountDao discountDao, File file);

    List<Order> findAll();

    List<Order> findAllSortedByDate();

    Order findById(Long id);

    Order create(Order order);

    Order update(Order order);

    void delete(Long id);

    List<Order> getUpcomingVisits(Long userId);

    List<Order> getPastVisits(Long userId);

    List<Order> getAllOrders();

    List<Order> getAllPastBookings();

    List<Order> getAllUpcomingBookings();
    List<Order> getOrdersByDate(Date date);

}
