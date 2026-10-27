
package sk.upjs.ics;

import org.springframework.jdbc.core.JdbcOperations;

public class StatisticsDao {

    private final JdbcOperations jdbcOperations;

    public StatisticsDao(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    public int getNumberOfCustomers() {
        Integer result = jdbcOperations.queryForObject(
                "SELECT COUNT(*) FROM users WHERE role = 'COSTUMER'",
                Integer.class
        );
        return result != null ? result : 0;
    }

    public int getNumberOfOrders() {
        Integer result = jdbcOperations.queryForObject(
                "SELECT COUNT(*) FROM orders WHERE dogs_id IS NOT NULL",
                Integer.class
        );
        return result != null ? result : 0;
    }

    public String getMostPopularService() {
        String sql = """
                SELECT st.name, COUNT(*) AS total
                FROM orders_has_service_types ost
                JOIN service_types st
                    ON st.id = ost.service_types_id
                JOIN orders o
                    ON o.id = ost.orders_id
                WHERE o.dogs_id IS NOT NULL
                GROUP BY st.id, st.name
                ORDER BY total DESC, st.name ASC
                LIMIT 1
                """;

        return jdbcOperations.query(sql, rs -> {
            if (rs.next()) {
                return rs.getString("name")
                        + " (" + rs.getInt("total") + " bookings)";
            }
            return "No services booked yet.";
        });
    }

    public double getAverageCustomersPerMonth() {
        String sql = """
                SELECT AVG(monthly_customers)
                FROM (
                    SELECT COUNT(DISTINCT d.users_id) AS monthly_customers
                    FROM orders o
                    JOIN dogs d ON d.id = o.dogs_id
                    GROUP BY YEAR(o.date_time), MONTH(o.date_time)
                ) AS monthly_stats
                """;

        Double result = jdbcOperations.queryForObject(
                sql,
                Double.class
        );

        return result != null ? result : 0.0;
    }

    public String getBusiestWeekday() {
        String sql = """
                SELECT DAYNAME(date_time) AS weekday,
                       COUNT(*) AS total
                FROM orders
                WHERE dogs_id IS NOT NULL
                GROUP BY DAYOFWEEK(date_time), DAYNAME(date_time)
                ORDER BY total DESC, DAYOFWEEK(date_time) ASC
                LIMIT 1
                """;

        return jdbcOperations.query(sql, rs -> {
            if (rs.next()) {
                return rs.getString("weekday")
                        + " (" + rs.getInt("total") + " visits)";
            }
            return "No visits found.";
        });
    }
}
