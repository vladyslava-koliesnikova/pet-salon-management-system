package sk.upjs.ics;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import lombok.Setter;
import sk.upjs.ics.discounts.DiscountDao;
import sk.upjs.ics.dogs.DogDao;
import sk.upjs.ics.orders.Order;
import sk.upjs.ics.discounts.Discount;
import sk.upjs.ics.orders.OrderDao;
import sk.upjs.ics.servicetypes.ServiceType;
import sk.upjs.ics.servicetypes.ServiceTypeDao;
import sk.upjs.ics.users.UserDao;

import java.time.LocalDate;

public class ServiceTypeController {

    private final DogDao dogDao = Factory.INSTANCE.getDogDao();
    private final OrderDao orderDao = Factory.INSTANCE.getOrderDao();
    private final DiscountDao discountDao = Factory.INSTANCE.getDiscountDao();
    private final UserDao userDao = Factory.INSTANCE.getUserDao();
    private final ServiceTypeDao serviceTypeDao = Factory.INSTANCE.getServiceTypeDao();

    @FXML
    private Button confirmButton;

    @FXML
    private CheckBox groomingCheckBox;

    @FXML
    private CheckBox bathingCheckBox;

    @FXML
    private CheckBox nailTrimmingCheckBox;

    @Setter
    private Order currentOrder;

    @FXML
    void confirmButtonAction(ActionEvent event) {

        if (groomingCheckBox.isSelected()){
            ServiceType serviceType = new ServiceType();
            serviceType.setId(1);
            serviceType.setName("Grooming");
            serviceType.setPrice(25);
            currentOrder.getServices().add(serviceType);
        }

        if (bathingCheckBox.isSelected()){
            ServiceType serviceType = new ServiceType();
            serviceType.setId(2);
            serviceType.setName("Bathing");
            serviceType.setPrice(15);
            currentOrder.getServices().add(serviceType);
        }
        if (nailTrimmingCheckBox.isSelected()){
            ServiceType serviceType = new ServiceType();
            serviceType.setId(3);
            serviceType.setName("Nail Trimming");
            serviceType.setPrice(10);
            currentOrder.getServices().add(serviceType);
        }

        double price = calculatePrice(currentOrder);
        String strPrice = String.valueOf(price);

        if(currentOrder.getDiscount() != null){
            String discountName = String.valueOf(currentOrder.getDiscount().getType());
            var alert = new Alert(Alert.AlertType.CONFIRMATION, "Price:" + strPrice + " Discount: " + discountName);
            alert.showAndWait();
        } else {
            var alert = new Alert(Alert.AlertType.CONFIRMATION, strPrice);
            alert.showAndWait();
        }

        orderDao.update(currentOrder);

        confirmButton.getScene().getWindow().hide();
    }

    private double calculatePrice(Order order){
        double price = 0;
        for(ServiceType service : order.getServices()) {
            price += service.getPrice();
        }

        for (Discount discount : discountDao.findAll()) {
            LocalDate discountStartDate = discount.getStartDate();
            LocalDate discountEndDate = discount.getEndDate();
            LocalDate orderDate = order.getDateTime().toLocalDate();

            // Kontrola, či dátum objednávky je medzi start_date a end_date discountu
            if ((orderDate.isEqual(discountStartDate) || orderDate.isAfter(discountStartDate)) &&
                    (orderDate.isEqual(discountEndDate) || orderDate.isBefore(discountEndDate))) {
                double discountPercent = discount.getDiscount_percent();
                price = price - (price * discountPercent / 100);
                order.setDiscount(discount);
                break;
            }
        }

        return price;
    }
}
