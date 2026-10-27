package sk.upjs.ics.servicetypes;

import lombok.Data;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
public class ServiceType {
    private long id;
    private String name;
    private double price;

    public static ServiceType fromResultSet(ResultSet rs) throws SQLException {
        return fromResultSet(rs, "");
    }

    public static ServiceType fromResultSet(ResultSet rs, String aliasPrefix) throws SQLException {
        var id = rs.getLong(aliasPrefix + "id");
        if (rs.wasNull()) {
            return null;
        }

        var serviceType = new ServiceType();
        serviceType.setId(id);
        serviceType.setPrice(rs.getDouble(aliasPrefix + "price"));
        serviceType.setName(rs.getString(aliasPrefix + "name"));

        return serviceType;
    }
}
