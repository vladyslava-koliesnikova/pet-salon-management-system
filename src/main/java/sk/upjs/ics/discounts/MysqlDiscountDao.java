package sk.upjs.ics.discounts;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MysqlDiscountDao implements DiscountDao {

    private static final String SELECT_QUERY = "SELECT id, name, discount_percent, start_date, end_date FROM discounts";
    private final JdbcOperations jdbcOperations;

    private final ResultSetExtractor<List<Discount>> resultSetExtractor = rs -> {
        List<Discount> discounts = new ArrayList<>();
        while (rs.next()) {
            discounts.add(Discount.fromResultSet(rs));
        }
        return discounts;
    };

    public MysqlDiscountDao(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public int loadFromCsv(File file) {
        int loadedCount = 0;

        try (Scanner scanner = new Scanner(file)) {
            if (scanner.hasNextLine()) {
                scanner.nextLine();
            }

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.isBlank()) {
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length < 5) {
                    throw new IllegalArgumentException("Invalid CSV format: " + line);
                }

                long id = Long.parseLong(parts[0].trim());
                String type = parts[1];
                double discount_percent = Double.parseDouble(parts[2].trim());
                LocalDate startDate = LocalDate.parse(parts[3].trim());
                LocalDate endDate = LocalDate.parse(parts[4].trim());


                jdbcOperations.update(
                        "INSERT INTO discounts (id, name, discount_percent, start_date, end_date) VALUES (?, ?, ?, ?, ?)",
                        id, type, discount_percent, startDate, endDate
                );

                loadedCount++;
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found: " + file.getAbsolutePath(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error while loading data from CSV", e);
        }

        return loadedCount;
    }

    @Override
    public List<Discount> findAll() {
        return jdbcOperations.query(SELECT_QUERY, (rs, rowNum) -> Discount.fromResultSet(rs));
    }
    @Override
    public Discount findById(Long id) {
        String query = SELECT_QUERY + " WHERE id = ?";
        try {
            Discount found = jdbcOperations.query(query, ps -> ps.setLong(1, id), rs -> {
                if (rs.next()) {
                    Discount discount = Discount.fromResultSet(rs);
                    return discount;
                }
                return null;
            });

            return found;
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching the Discount with id: " + id, e);
        }
    }

    @Override
    public Discount create(Discount discount) {
        String query = "INSERT INTO discounts (name, discount_percent, start_date, end_date) VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcOperations.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, discount.getType().toString());
            ps.setDouble(2, discount.getDiscount_percent());
            ps.setDate(3, java.sql.Date.valueOf(discount.getStartDate()));
            ps.setDate(4, java.sql.Date.valueOf(discount.getEndDate()));
            return ps;
        }, keyHolder);

        discount.setId(keyHolder.getKey().longValue());
        return discount;
    }
    public void deleteAll() {
        jdbcOperations.update("DELETE FROM discounts");
    }
}


