package sk.upjs.ics.users;

import sk.upjs.ics.dogs.DogDao;
import sk.upjs.ics.exceptions.NotFoundException;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class MemoryUserDao implements UserDao {

    private final List<User> users;
    private long nextId;

    public MemoryUserDao(List<User> users) {
        this.users = users;
        this.nextId = users.stream()
                .map(User::getId)
                .max(Comparator.naturalOrder())
                .orElse(0L) + 1;
    }

    @Override
    public int loadFromCsv(DogDao dogDao, File file) {
        var existingIds = users.stream()
                .map(User::getId)
                .collect(Collectors.toSet());

        var dogs = dogDao.findAll()
                .stream()
                .collect(Collectors.toMap(s -> s.getId(), s -> s));

        var users = new ArrayList<User>();
        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }

        while (scanner.hasNextLine()) {
            var line = scanner.nextLine();
            if (line.isBlank()) {
                continue;
            }

            var parts = line.split(",");
            if (parts.length < 8) {
                continue;
            }

            var id = Long.parseLong(parts[0]);
            if (existingIds.contains(id)) {
                continue;
            }

            var user = new User();
            user.setId(id);

            var roleString = parts[1].trim();
            switch (roleString.toUpperCase()) {
                case "COSTUMER":
                    user.setRole(Role.COSTUMER);
                    break;
                case "EMPLOYEE":
                    user.setRole(Role.EMPLOYEE);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid role: " + roleString);
            }

            user.setName(parts[2].trim());
            user.setSurname(parts[3].trim());
            user.setPassword(parts[4].trim());
            user.setEmail(parts[5].trim());
            user.setBirthDate(LocalDate.parse(parts[6].trim()));
            /***
            var dogIds = parts[7].trim();
            if (!dogIds.isEmpty()) {
                var dogSet = Arrays.stream(dogIds.split("_"))
                        .filter(s -> !s.isEmpty())
                        .map(Long::parseLong)
                        .map(dogs::get)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());
                user.setDogs(dogSet);
            } else {
                user.setDogs(new HashSet<>()); // Empty set if no dogs
            }
            ***/
            var dogIds = parts[7].trim();
            if (!dogIds.isEmpty() && !dogIds.equals("\"\"")) {  // Ošetrí prázdne hodnoty aj "" v CSV
                var dogSet = Arrays.stream(dogIds.split("_"))
                        .filter(s -> !s.isEmpty() && !s.equals("\"\"")) // Odstráni prázdne hodnoty
                        .map(Long::parseLong)
                        .map(dogs::get)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());
                user.setDogs(dogSet);
            } else {
                user.setDogs(new HashSet<>()); // Prázdna množina psov
            }

            users.add(user);
        }

        this.users.addAll(users);
        return users.size();
    }

    @Override
    public List<User> findAll() {
        return users;
    }

    @Override
    public User create(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        if (user.getId() != null) {
            throw new IllegalArgumentException("User ID must be null");
        }

        user.setId(nextId++);
        users.add(user);
        return user;
    }

    @Override
    public User update(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        if (user.getId() == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        var existing = users.stream()
                .filter(a -> a.getId().equals(user.getId()))
                .findAny()
                .orElseThrow(() -> new NotFoundException("User not found"));

        existing.setName(user.getName());
        existing.setSurname(user.getSurname());
        existing.setPassword(user.getPassword());
        existing.setEmail(user.getEmail());
        existing.setBirthDate(user.getBirthDate());
        existing.setRole(user.getRole());
        existing.setDogs(user.getDogs());

        return existing;
    }

    @Override
    public void delete(Long id) {
        for (var i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(id)) {
                users.remove(i);
                break;
            }
        }
    }

    @Override
    public User findByEmail(String email) {
        for (var i = 0; i < users.size(); i++) {
            if (users.get(i).getEmail().equals(email)) {
                return users.get(i);
            }
        }
        return null;
    }

    @Override
    public User findById(Long l) {
        return users.stream()
                .filter(user -> user.getId().equals(l))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("User with ID " + l + " not found"));
    }

    @Override
    public List<User> getAllClients() {
        return List.of();
    }

    @Override
    public boolean authenticate(String password, User user) {
        return Objects.equals(password, user.getPassword());
    }
}