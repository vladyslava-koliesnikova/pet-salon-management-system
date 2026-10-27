package sk.upjs.ics;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Setter;
import sk.upjs.ics.discounts.DiscountDao;
import sk.upjs.ics.dogs.Dog;
import sk.upjs.ics.dogs.DogDao;
import sk.upjs.ics.orders.Order;
import sk.upjs.ics.orders.OrderDao;
import sk.upjs.ics.servicetypes.ServiceTypeDao;
import sk.upjs.ics.users.User;
import sk.upjs.ics.users.UserDao;

import java.io.IOException;

public class CostumerWindowController {

    private final DogDao dogDao = Factory.INSTANCE.getDogDao();
    private final OrderDao orderDao = Factory.INSTANCE.getOrderDao();
    private final DiscountDao discountDao = Factory.INSTANCE.getDiscountDao();
    private final UserDao userDao = Factory.INSTANCE.getUserDao();
    private final ServiceTypeDao serviceTypeDao = Factory.INSTANCE.getServiceTypeDao();

    @FXML
    private Button upcomingVisitsButton;

    @FXML
    private Button pastVisitsButton;

    @FXML
    private Button myDogsButton;

    @FXML
    private Button addDogButton;

    @FXML
    private Button bookVisitButton;

    @FXML
    private Button signOffButton;

    @FXML
    private Button removeButton;

    @FXML
    private ListView<Object> orderDoglistView;

    @Setter
    private User currentUser;

    @FXML
    void signOffButtonAction(ActionEvent event) {

        var loader = new FXMLLoader(getClass().getResource("WelcomeScreen.fxml"));

        Parent pane;
        try {
            pane = loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Error loading WelcomeScreen.fxml", e);
        }

        signOffButton.getScene().getWindow().hide();

        var scene = new Scene(pane);
        var stage = new Stage();

        stage.setTitle("Log in");

        stage.setScene(scene);

        stage.show();
    }

    @FXML
    void addDogButtonAction(ActionEvent event) {

        var loader = new FXMLLoader(getClass().getResource("AddDogWindow.fxml"));

        Parent pane;
        try {
            pane = loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Error loading AddDogWindow.fxml", e);
        }

        DogController controller = loader.getController();
        controller.setCurrentUser(currentUser);

        var scene = new Scene(pane);
        var stage = new Stage();

        stage.setTitle("Add dog");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(addDogButton.getScene().getWindow());

        stage.setScene(scene);

        stage.show();
    }

    @FXML
    void bookVisitButtonAction(ActionEvent event) {
        System.out.println("Current user in controller: " + currentUser);
        var loader = new FXMLLoader(getClass().getResource("OrderWindow.fxml"));

        Parent pane;
        try {
            pane = loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Error loading OrderWindow.fxml", e);
        }

        OrderController controller = loader.getController();
        controller.setCurrentUser(currentUser);
        controller.initializeWithUser();

        var scene = new Scene(pane);
        var stage = new Stage();

        stage.setTitle("Order");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(bookVisitButton.getScene().getWindow());

        stage.setScene(scene);

        stage.show();
    }

    @FXML
    void upcomingVisitsButtonAction(ActionEvent event) {
        orderDoglistView.getItems().clear();
        var upcomingVisits = orderDao.getUpcomingVisits(currentUser.getId());
        orderDoglistView.getItems().addAll(upcomingVisits);
        removeButton.setDisable(false);
    }

    @FXML
    void pastVisitsButtonAction(ActionEvent event) {
        orderDoglistView.getItems().clear();
        var pastVisits = orderDao.getPastVisits(currentUser.getId());
        orderDoglistView.getItems().addAll(pastVisits);
        removeButton.setDisable(true);
    }

    @FXML
    void myDogsButtonAction(ActionEvent event) {
        orderDoglistView.getItems().clear();
        var myDogs = dogDao.getDogsByOwnerId(currentUser.getId());
        orderDoglistView.getItems().addAll(myDogs);
        removeButton.setDisable(false);
    }

    @FXML
    void removeButtonAction(ActionEvent event) {
        var selectedItem = orderDoglistView.getSelectionModel().getSelectedItem();

        if (selectedItem == null) {
            System.out.println("No item selected!");
            return;
        }

        if (selectedItem instanceof Order) {
            orderDao.delete(((Order) selectedItem).getId());
        } else if (selectedItem instanceof Dog) {
            dogDao.delete(((Dog) selectedItem).getId());
        }

        orderDoglistView.getItems().remove(selectedItem);
    }
}
