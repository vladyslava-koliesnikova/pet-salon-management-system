package sk.upjs.ics.discounts;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class MemoryDiscountDao implements DiscountDao {

    private final List<Discount> discounts;
    private long nextId;

    public MemoryDiscountDao(List<Discount> discounts) {
        this.discounts = discounts;
        this.nextId = discounts.stream()
                .map(Discount::getId)
                .max(Comparator.naturalOrder())
                .orElse(0L) + 1;
    }

    @Override
    public int loadFromCsv(File file) {
        var existingIds = discounts.stream()
                .map(Discount::getId)
                .collect(Collectors.toSet());

        var discounts = new ArrayList<Discount>();
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

            var discount = new Discount();
            discount.setId(id);

            var typeString = parts[1];
            switch (typeString) {
                case "CHRISTMAS":
                    discount.setType(DiscountType.CHRISTMAS);
                    break;
                case "NEW_YEAR":
                    discount.setType(DiscountType.NEW_YEAR);
                    break;
                case "EASTER":
                    discount.setType(DiscountType.EASTER);
                    break;
            }

            discount.setDiscount_percent(Double.parseDouble(parts[2]));
            discount.setStartDate(LocalDate.parse(parts[3]));
            discount.setEndDate(LocalDate.parse(parts[4]));

            discounts.add(discount);
        }

        this.discounts.addAll(discounts);
        return discounts.size();
    }




    @Override
    public List<Discount> findAll() {
        return discounts;
    }

    @Override
    public Discount findById(Long id) {
        return discounts.stream()
                .filter(discount -> discount.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public Discount create(Discount discount) {

        if (discount == null) {
            throw new IllegalArgumentException("Discount cannot be null");
        }

        long maxId = discounts.stream()
                .mapToLong(Discount::getId)
                .max()
                .orElse(0);

        discount.setId(maxId + 1);
        discounts.add(discount);
        return discount;
    }

    @Override
    public void deleteAll() {
        discounts.clear();
    }
}
