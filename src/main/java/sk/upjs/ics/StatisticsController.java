
package sk.upjs.ics;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import org.springframework.jdbc.core.JdbcOperations;

public class StatisticsController {

    @FXML
    private Label resultLabel;

    private StatisticsDao statisticsDao;

    @FXML
    public void initialize() {
        statisticsDao = new StatisticsDao(
                Factory.INSTANCE.getMysqlJdbcOperations()
        );
    }
    @FXML
    private void numberOfCostumersButtonAction() {
        showResult(() ->
                "Number of customers: "
                        + statisticsDao.getNumberOfCustomers()
        );
    }

    @FXML
    private void numberOfOrdersButtonAction() {
        showResult(() ->
                "Number of booked visits: "
                        + statisticsDao.getNumberOfOrders()
        );
    }

    @FXML
    private void mostPopularServiceButtonAction() {
        showResult(() ->
                "Most popular service: "
                        + statisticsDao.getMostPopularService()
        );
    }

    @FXML
    private void employeeWithMostVisitsButtonAction() {
        resultLabel.setText(
                "Not available: bookings are not assigned to employees."
        );
    }

    @FXML
    private void averageNumberOfCostumersButtonAction() {
        showResult(() -> String.format(
                "Average unique customers per active month: %.2f",
                statisticsDao.getAverageCustomersPerMonth()
        ));
    }

    @FXML
    private void weekdayWithMostVisitsButtonAction() {
        showResult(() ->
                "Busiest weekday: "
                        + statisticsDao.getBusiestWeekday()
        );
    }

    private void showResult(StatisticsCalculation calculation) {
        try {
            resultLabel.setText(calculation.calculate());
        } catch (RuntimeException e) {
            resultLabel.setText("Unable to load statistics.");

            Alert alert = new Alert(
                    Alert.AlertType.ERROR,
                    "Database error: " + e.getMessage()
            );
            alert.showAndWait();

            e.printStackTrace();
        }
    }

    @FunctionalInterface
    private interface StatisticsCalculation {
        String calculate();
    }
}
