package sk.upjs.ics.servicetypes;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MysqlServiceTypeDao implements ServiceTypeDao {
    private final JdbcOperations jdbcOperations;
    private final String selectAllServiceTypesQuery = """
                SELECT id, name, price
                FROM service_types
            """;
    private final ResultSetExtractor<List<ServiceType>> resultSetExtractor = rs -> {
        List<ServiceType> serviceTypes = new ArrayList<>();
        while (rs.next()) {
            serviceTypes.add(ServiceType.fromResultSet(rs));
        }
        return serviceTypes;
    };


    public MysqlServiceTypeDao(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public int loadFromCsv(File file) {
        int recordsLoaded = 0;
        try (Scanner scanner = new Scanner(file)) {
            scanner.nextLine();

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.isBlank()) {
                    continue;
                }

                String[] parts = line.split(",");


                ServiceType serviceType = new ServiceType();
                serviceType.setId(Long.parseLong(parts[0]));
                serviceType.setName(parts[1]);
                serviceType.setPrice(Double.parseDouble(parts[2]));

                jdbcOperations.update(
                        "INSERT INTO service_types (id, name, price) VALUES (?, ?, ?)",
                        serviceType.getId(),
                        serviceType.getName(),
                        serviceType.getPrice()
                );
                recordsLoaded++;
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("CSV file not found: " + file.getAbsolutePath(), e);
        }
        return recordsLoaded;
    }

    @Override
    public List<ServiceType> findAll() {

        return jdbcOperations.query(selectAllServiceTypesQuery, resultSetExtractor);
    }

    @Override
    public ServiceType findById(Long id) {
        String query = "SELECT id, name, price FROM service_types WHERE id = ?";
        return jdbcOperations.query(query, resultSetExtractor, id).stream().findFirst().orElse(null);
    }

} 
