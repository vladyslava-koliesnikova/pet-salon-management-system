package sk.upjs.ics.orders;

import sk.upjs.ics.discounts.DiscountDao;
import sk.upjs.ics.dogs.DogDao;
import sk.upjs.ics.exceptions.NotFoundException;
import sk.upjs.ics.servicetypes.ServiceTypeDao;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class MemoryOrderDao implements OrderDao {

    private final List<Order> orders;
    private long nextId;

    public MemoryOrderDao(List<Order> orders) {
        this.orders = orders;
        this.nextId = orders.stream()
                .map(Order::getId)
                .max(Comparator.naturalOrder())
                .orElse(0L) + 1;
    }

    @Override
    public int loadFromCsv(ServiceTypeDao serviceTypeDao, DogDao dogDao, DiscountDao discountDao, File file) {
        var existingIds = orders.stream()
                .map(Order::getId)
                .collect(Collectors.toSet());

        var services = serviceTypeDao.findAll()
                .stream()
                .collect(Collectors.toMap(s -> s.getId(), s -> s));

        var dogs = dogDao.findAll()
                .stream()
                .collect(Collectors.toMap(s -> s.getId(), s -> s));

        var discounts = discountDao.findAll()
                .stream()
                .collect(Collectors.toMap(s -> s.getId(), s -> s));

        var orders = new ArrayList<Order>();
        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        scanner.nextLine();
        while (scanner.hasNextLine()) {
            var line = scanner.nextLine();
            if (line.isBlank()) {
                continue;
            }

            var parts = line.split(",");

            var id = Long.parseLong(parts[0]);
            if (existingIds.contains(id)) {
                continue;
            }

            var order = new Order();
            order.setId(id);
            order.setDateTime(LocalDateTime.parse(parts[1]));

            var orderedServices = Arrays.stream(parts[2].split("_"))
                    .filter(s -> !s.isEmpty())
                    .map(Long::parseLong)
                    .map(services::get)
                    .collect(Collectors.toSet());

            order.setServices(orderedServices);
            order.setDog(dogs.get(Long.parseLong(parts[3])));
            order.setDiscount(discounts.get(Long.parseLong(parts[4])));
            orders.add(order);
        }

        this.orders.addAll(orders);
        return orders.size();
    }

    @Override
    public List<Order> findAll() {
        return orders;
    }

    @Override
    public List<Order> findAllSortedByDate() {
        return orders.stream()
                .sorted(Comparator.comparing(Order::getDateTime).reversed())
                .toList();
    }

    @Override
    public Order create(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }

        if (order.getId() != null) {
            throw new IllegalArgumentException("Order ID must be null");
        }

        order.setId(nextId++);
        orders.add(order);
        return order;
    }

    @Override
    public Order update(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }

        if (order.getId() == null) {
            throw new IllegalArgumentException("Order ID cannot be null");
        }

        var existing = orders.stream()
                .filter(a -> a.getId().equals(order.getId()))
                .findAny()
                .orElseThrow(() -> new NotFoundException("Order not found"));

        existing.setDateTime(order.getDateTime());
        existing.setServices(order.getServices());
        existing.setDiscount(order.getDiscount());
        existing.setDog(order.getDog());

        return existing;
    }

    @Override
    public void delete(Long id) {
        for (var i = 0; i < orders.size(); i++) {
            if (orders.get(i).getId().equals(id)) {
                orders.remove(i);
                break;
            }
        }
    }

    @Override
    public List<Order> getUpcomingVisits(Long userId) {
        return orders.stream()
                .filter(order -> order.getDog() != null &&
                        order.getDog().getOwner() != null &&
                        order.getDog().getOwner().getId().equals(userId) &&
                        order.getDateTime().isAfter(LocalDateTime.now()))
                .sorted(Comparator.comparing(Order::getDateTime))
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> getPastVisits(Long userId) {
        return orders.stream()
                .filter(order -> order.getDog() != null &&
                        order.getDog().getOwner() != null &&
                        order.getDog().getOwner().getId().equals(userId) &&
                        order.getDateTime().isBefore(LocalDateTime.now()))
                .sorted(Comparator.comparing(Order::getDateTime).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> getAllOrders() {
        return List.of();
    }

    @Override
    public List<Order> getAllPastBookings() {
        return List.of();
    }

    @Override
    public List<Order> getAllUpcomingBookings() {
        return List.of();
    }

    @Override
    public Order findById(Long id) {
        return orders.stream()
                .filter(order -> order.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Order> getOrdersByDate(Date date) {
        List<Order> orders = new ArrayList<>();

        for (Order order : orders) {
            LocalDateTime orderDateTime = order.getDateTime();

            LocalDate orderDate = orderDateTime.toLocalDate();

            if (orderDate.equals(date)) {
                orders.add(order);
            }
        }

        return orders;
    }
}
