package sk.upjs.ics;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import sk.upjs.ics.discounts.DiscountDao;
import sk.upjs.ics.discounts.MysqlDiscountDao;
import sk.upjs.ics.dogs.DogDao;
import sk.upjs.ics.dogs.MysqlDogDao;
import sk.upjs.ics.orders.MysqlOrderDao;
import sk.upjs.ics.orders.OrderDao;
import sk.upjs.ics.servicetypes.MysqlServiceTypeDao;
import sk.upjs.ics.servicetypes.ServiceTypeDao;
import sk.upjs.ics.users.MysqlUserDao;
import sk.upjs.ics.users.UserDao;

public enum Factory {
    INSTANCE;

    private final Object lock = new Object();
    private volatile JdbcOperations jdbcOperations;
    private volatile DogDao dogDao;
    private volatile OrderDao orderDao;
    private volatile ServiceTypeDao serviceTypeDao;
    private volatile UserDao userDao;
    private volatile DiscountDao discountDao;

    public JdbcOperations getMysqlJdbcOperations() {
        if (jdbcOperations == null) {
            synchronized (lock) {
                if (jdbcOperations == null) {
                    var dataSource = new MysqlDataSource();
                    dataSource.setUrl(System.getProperty("DB_JDBC", "jdbc:mysql://localhost:3307/petsalon"));
                    dataSource.setUser(System.getProperty("DB_USER", "root"));
                    dataSource.setPassword(System.getProperty("DB_PASSWORD", "root"));
                    jdbcOperations = new JdbcTemplate(dataSource);
                }
            }
        }
        return jdbcOperations;
    }

    // Vytvára a vracia inštanciu DogDao
    public DogDao getDogDao() {
        if (dogDao == null) {
            synchronized (lock) {
                if (dogDao == null) {
                    dogDao = new MysqlDogDao(getMysqlJdbcOperations());
                }
            }
        }
        return dogDao;
    }

    // Vytvára a vracia inštanciu OrderDao
    public OrderDao getOrderDao() {
        if (orderDao == null) {
            synchronized (lock) {
                if (orderDao == null) {
                    orderDao = new MysqlOrderDao(getMysqlJdbcOperations());
                }
            }
        }
        return orderDao;
    }

    // Vytvára a vracia inštanciu ServiceTypeDao
    public ServiceTypeDao getServiceTypeDao() {
        if (serviceTypeDao == null) {
            synchronized (lock) {
                if (serviceTypeDao == null) {
                    serviceTypeDao = new MysqlServiceTypeDao(getMysqlJdbcOperations());
                }
            }
        }
        return serviceTypeDao;
    }

    // Vytvára a vracia inštanciu UserDao
    public UserDao getUserDao() {
        if (userDao == null) {
            synchronized (lock) {
                if (userDao == null) {
                    userDao = new MysqlUserDao(getMysqlJdbcOperations());
                }
            }
        }
        return userDao;
    }

    // Vytvára a vracia inštanciu DiscountDao
    public DiscountDao getDiscountDao() {
        if (discountDao == null) {
            synchronized (lock) {
                if (discountDao == null) {
                    discountDao = new MysqlDiscountDao(getMysqlJdbcOperations());
                }
            }
        }
        return discountDao;
    }
}
