package sk.upjs.ics.dogs;

import sk.upjs.ics.exceptions.NotFoundException;
import sk.upjs.ics.orders.OrderDao;
import sk.upjs.ics.users.UserDao;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class MemoryDogDao implements DogDao {

    private final List<Dog> dogs;
    private long nextId;

    public MemoryDogDao(List<Dog> dogs) {
        this.dogs = dogs;
        this.nextId = dogs.stream()
                .map(Dog::getId)
                .max(Comparator.naturalOrder())
                .orElse(0L) + 1;
    }

    @Override
    public int loadFromCsv(UserDao userDao, OrderDao orderDao, File file) {
        var owners = userDao
                .findAll()
                .stream()
                .collect(Collectors.toMap(s -> s.getId(), s -> s));

        var orders = orderDao
                .findAll()
                .stream()
                .collect(Collectors.toMap(s -> s.getId(), s -> s));

        var existingIds = dogs.stream()
                .map(Dog::getId)
                .collect(Collectors.toSet());

        var dogs = new ArrayList<Dog>();
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

            var dog = new Dog();
            dog.setId(id);

            dog.setName(parts[1]);

            var sexString = parts[2];
            switch (sexString) {
                case "M":
                    dog.setSex(Sex.M);
                    break;
                case "F":
                    dog.setSex(Sex.F);
                    break;
                default:
                    dog.setSex(Sex.UNKNOWN);
                    break;
            }

            dog.setBirthDate(LocalDate.parse(parts[3]));
            dog.setOwner(owners.get(Long.parseLong(parts[4])));
/***
            var madeOrders = Arrays.stream(parts[10].split("_"))
                    .filter(s -> !s.isEmpty())
                    .map(Long::parseLong)
                    .map(orders::get)
                    .collect(Collectors.toSet());

            dog.setOrders(madeOrders);
***/
            dog.setOrders(new HashSet<>());
            dogs.add(dog);
        }

        this.dogs.addAll(dogs);
        return dogs.size();
    }

    @Override
    public List<Dog> findAll() {
        return dogs;
    }

    @Override
    public Dog create(Dog dog) {
        if (dog == null) {
            throw new IllegalArgumentException("Dog cannot be null");
        }

        if (dog.getId() != null) {
            throw new IllegalArgumentException("Dog ID must be null");
        }

        dog.setId(nextId++);
        dogs.add(dog);
        return dog;
    }

    @Override
    public Dog update(Dog dog) {
        if (dog == null) {
            throw new IllegalArgumentException("Dog cannot be null");
        }

        if (dog.getId() == null) {
            throw new IllegalArgumentException("Dog ID cannot be null");
        }

        var existing = dogs.stream()
                .filter(a -> a.getId().equals(dog.getId()))
                .findAny()
                .orElseThrow(() -> new NotFoundException("Dog not found"));

        existing.setName(dog.getName());
        existing.setSex(dog.getSex());
        existing.setBirthDate(dog.getBirthDate());
        existing.setOwner(dog.getOwner());
        existing.setOrders(dog.getOrders());
        return existing;
    }

    @Override
    public void delete(Long id) {
        for (var i = 0; i < dogs.size(); i++) {
            if (dogs.get(i).getId().equals(id)) {
                dogs.remove(i);
                break;
            }
        }
    }

    @Override
    public Dog findById(Long dogId) {
        return dogs.stream()
                .filter(dog -> dog.getId().equals(dogId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Dog not found with ID: " + dogId));
    }

    @Override
    public List<Dog> getDogsByOwnerId(Long ownerId) {
        if (ownerId == null) {
            throw new IllegalArgumentException("Owner ID cannot be null");
        }

        return dogs.stream()
                .filter(dog -> dog.getOwner() != null && dog.getOwner().getId().equals(ownerId))
                .collect(Collectors.toList());
    }
}
