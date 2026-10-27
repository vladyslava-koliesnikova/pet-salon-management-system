package sk.upjs.ics.users;

import lombok.Data;
import sk.upjs.ics.dogs.Dog;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private Long id;
    private Role role;
    private String name;
    private String surname;
    private String password;
    private String email;
    private LocalDate birthDate;
    private Set<Dog> dogs;

    @Override
    public String toString() {
        return "User with id: " + id + " and name: " + name;
    }

    public static User fromResultSet(ResultSet rs) throws SQLException {
        return fromResultSet(rs, "");
    }

    public static User fromResultSet(ResultSet rs, String aliasPrefix) throws SQLException {
        var id = rs.getLong(aliasPrefix + "id");
        if (rs.wasNull()) {
            return null;
        }

        var user = new User();
        user.setId(id);
        user.setRole(Role.valueOf(rs.getString(aliasPrefix + "role")));
        user.setName(rs.getString(aliasPrefix + "first_name"));
        user.setSurname(rs.getString(aliasPrefix + "last_name"));
        user.setPassword(rs.getString(aliasPrefix + "password"));
        user.setEmail(rs.getString(aliasPrefix + "email"));
        user.setBirthDate(rs.getDate(aliasPrefix + "birth_date").toLocalDate());
        user.setDogs(new HashSet<>());
        return user;
    }
}
