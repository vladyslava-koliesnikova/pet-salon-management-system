package sk.upjs.ics;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import lombok.Setter;
import sk.upjs.ics.discounts.DiscountDao;
import sk.upjs.ics.dogs.Dog;
import sk.upjs.ics.dogs.DogDao;
import sk.upjs.ics.dogs.DogModel;
import sk.upjs.ics.dogs.Sex;
import sk.upjs.ics.orders.Order;
import sk.upjs.ics.orders.OrderDao;
import sk.upjs.ics.orders.OrderModel;
import sk.upjs.ics.servicetypes.ServiceTypeDao;
import sk.upjs.ics.users.User;
import sk.upjs.ics.users.UserDao;
import sk.upjs.ics.users.UserModel;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;


public class OrderController {

    private final DogDao dogDao = Factory.INSTANCE.getDogDao();
    private final OrderDao orderDao = Factory.INSTANCE.getOrderDao();
    private final DiscountDao discountDao = Factory.INSTANCE.getDiscountDao();
    private final UserDao userDao = Factory.INSTANCE.getUserDao();
    private final ServiceTypeDao serviceTypeDao = Factory.INSTANCE.getServiceTypeDao();
    private final OrderModel model = new OrderModel();

    @FXML
    private Button closeButton;

    @FXML
    private ComboBox<Dog> selectDogComboBox;

    @FXML
    private DatePicker datePicker;

    @FXML
    private ListView<Order> orderListView;

    @Setter
    private User currentUser;

    // Metóda na aktualizáciu dostupných termínov
    private void updateAvailableTimes() {

        Dog selectedDog = selectDogComboBox.getValue();
        LocalDate selectedDate = datePicker.getValue();

        // Ak nie je vybraný pes alebo dátum, vymaže sa obsah ListView
        if (selectedDog == null || selectedDate == null) {
            orderListView.getItems().clear();
            return;
        }

        java.sql.Date selectedDateAsDate = java.sql.Date.valueOf(selectedDate);

        List<Order> availableTimes = orderDao.getOrdersByDate(selectedDateAsDate);

        LocalDateTime now = LocalDateTime.now();

        // Filtrovanie objednávok, kde dog == null a dátum je v budúcnosti
        List<Order> filteredTimes = availableTimes.stream()
                .filter(order -> order.getDog() == null)  // Zobraziť len objednávky s dog == null
                .filter(order -> order.getDateTime().isAfter(now))  // Zobraziť len budúce objednávky
                .toList();

        orderListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Order order, boolean empty) {
                super.updateItem(order, empty);
                if (empty || order == null) {
                    setText(null);
                } else {
                    String formattedDateTime = order.getDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                    setText(formattedDateTime);
                }
            }
        });

        orderListView.getItems().setAll(filteredTimes);
    }

    @FXML
    void orderListViewMouseClicked(MouseEvent event) {
        if (event.getClickCount() == 2) {
            var selectedOrder = orderListView.getSelectionModel().getSelectedItem();
            selectedOrder.setDog(selectDogComboBox.getValue());

            var loader = new FXMLLoader(getClass().getResource("ServiceTypeWindow.fxml"));

            Parent pane;
            try {
                pane = loader.load();
            } catch (IOException e) {
                throw new RuntimeException("Error loading ServiceType.fxml", e);
            }

            ServiceTypeController controller = loader.getController();
            controller.setCurrentOrder(selectedOrder);

            var scene = new Scene(pane);
            var stage = new Stage();

            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(orderListView.getScene().getWindow());

            stage.setTitle("Costumer");

            stage.setScene(scene);

            stage.show();

        }
    }

    @FXML
    void closeButtonAction(ActionEvent event) {
        closeButton.getScene().getWindow().hide();
    }


    @FXML
    public void initialize() {

        selectDogComboBox.setConverter(new StringConverter<>() {

            @Override
            public String toString(Dog dog) {
                if (dog == null) {
                    return "";
                }

                return dog.getName();
            }

            @Override
            public Dog fromString(String string) {
                for (Dog dog : selectDogComboBox.getItems()) {
                    if (dog.getName().equals(string)) {
                        return dog;
                    }
                }
                return null;
            }
        });

        datePicker.setValue(LocalDate.now());
        selectDogComboBox.valueProperty().bindBidirectional(model.dogProperty());

        datePicker.valueProperty().addListener((obs, oldDate, newDate) -> updateAvailableTimes());
    }

    // tato metoda sa zavola po nastaveni currentUser
    public void initializeWithUser() {
        if (currentUser == null) {
            throw new IllegalStateException("Current user is not set!");
        }

        // Naplnenie ComboBox položkami na základe currentUser
        Set<Dog> dogs = currentUser.getDogs();
        selectDogComboBox.getItems().addAll(dogs);
    }

}
