package sk.upjs.ics.orders;

import lombok.Data;
import sk.upjs.ics.discounts.Discount;
import sk.upjs.ics.dogs.Dog;
import sk.upjs.ics.servicetypes.ServiceType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;



@Data
public class Order {
    private Long id;
    private LocalDateTime dateTime;
    private Set<ServiceType> services;
    private Dog dog;
    private Discount discount;

    public static Order fromResultSet(ResultSet rs) throws SQLException {
        return fromResultSet(rs, "");
    }

    public static Order fromResultSet(ResultSet rs, String aliasPrefix) throws SQLException {
        var id = rs.getLong(aliasPrefix + "id");
        if (rs.wasNull()) {
            return null;
        }

        var order = new Order();
        order.setId(id);
        order.setDateTime(rs.getTimestamp(aliasPrefix + "date_time").toLocalDateTime());

        var dogsId = rs.getObject(aliasPrefix + "dogs_id", Long.class);
        if (dogsId != null) {
            var dog = new Dog();
            dog.setId(dogsId);
            order.setDog(dog);
        } else {
            order.setDog(null);
        }

        var discountsId = rs.getObject(aliasPrefix + "discounts_id", Long.class);
        if (discountsId != null) {
            var discount = new Discount();
            discount.setId(discountsId);
            order.setDiscount(discount);
        } else {
            order.setDiscount(null);
        }

        order.setServices(new HashSet<>());

        return order;
    }
}
