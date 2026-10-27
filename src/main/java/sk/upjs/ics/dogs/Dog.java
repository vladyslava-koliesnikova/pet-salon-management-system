package sk.upjs.ics.dogs;

import lombok.Data;
import sk.upjs.ics.orders.Order;
import sk.upjs.ics.users.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Dog {
    private Long id;
    private String name;
    private Sex sex;
    private LocalDate birthDate;
    private User owner;
    private Set<Order> orders;

    @Override
    public String toString() {
        return "Dog with id: " + id + " and name: " + name;
    }

    public static Dog fromResultSet(ResultSet rs) throws SQLException {
        return fromResultSet(rs, "");
    }

    public static Dog fromResultSet(ResultSet rs, String aliasPrefix) throws SQLException {
        var id = rs.getLong(aliasPrefix + "id");
        if (rs.wasNull()) {
            return null;
        }

        var dog = new Dog();
        dog.setId(id);
        dog.setName(rs.getString(aliasPrefix + "name"));
        dog.setSex(Sex.valueOf(rs.getString(aliasPrefix + "sex")));
        dog.setBirthDate(rs.getDate(aliasPrefix + "birth_date").toLocalDate());

        dog.setOwner(null);
        dog.setOrders(new HashSet<>());
        return dog;
    }
}
