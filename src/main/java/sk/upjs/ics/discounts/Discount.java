package sk.upjs.ics.discounts;

import lombok.Data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

@Data
public class Discount {
    private Long id;
    private DiscountType type;
    private double discount_percent;
    private LocalDate startDate;
    private LocalDate endDate;

    public static Discount fromResultSet(ResultSet rs) throws SQLException {
        return fromResultSet(rs, "");
    }

    public static Discount fromResultSet(ResultSet rs, String aliasPrefix) throws SQLException {
        var id = rs.getLong(aliasPrefix + "id");
        if (rs.wasNull()) {
            return null;
        }

        var discount = new Discount();
        discount.setId(id);
        var nameStr = rs.getString(aliasPrefix + "name");
        try {
            discount.setType(DiscountType.valueOf(nameStr));
        } catch (IllegalArgumentException e) {
            throw new SQLException("Invalid DiscountType in database: " + nameStr, e);
        }
        //discount.setType(DiscountType.valueOf(rs.getString(aliasPrefix + "type")));
        //discount.setPercentage(rs.getDouble(aliasPrefix + "percentage"));
        //discount.setStartDate(rs.getDate(aliasPrefix + "start_date").toLocalDate());
        //discount.setEndDate(rs.getDate(aliasPrefix + "end_date").toLocalDate());

        //return discount;
        discount.setDiscount_percent(rs.getDouble(aliasPrefix + "discount_percent"));

        var startDateSql = rs.getDate(aliasPrefix + "start_date");
        discount.setStartDate(startDateSql != null ? startDateSql.toLocalDate() : null);

        var endDateSql = rs.getDate(aliasPrefix + "end_date");
        discount.setEndDate(endDateSql != null ? endDateSql.toLocalDate() : null);

        return discount;
    }
}
