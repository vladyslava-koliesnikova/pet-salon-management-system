package sk.upjs.ics.discounts;

import java.io.File;
import java.util.List;

public interface DiscountDao {
    int loadFromCsv(File file);

    List<Discount> findAll();

    Discount findById(Long id);

    Discount create(Discount discount);

    void deleteAll();
}
