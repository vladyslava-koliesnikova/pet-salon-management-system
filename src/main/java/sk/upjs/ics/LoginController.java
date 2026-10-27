package sk.upjs.ics;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sk.upjs.ics.discounts.DiscountDao;
import sk.upjs.ics.dogs.DogDao;
import sk.upjs.ics.orders.OrderDao;
import sk.upjs.ics.servicetypes.ServiceTypeDao;
import sk.upjs.ics.users.Role;
import sk.upjs.ics.users.User;
import sk.upjs.ics.users.UserDao;

import java.io.IOException;
import java.util.Objects;

public class LoginController {

    private final DogDao dogDao = Factory.INSTANCE.getDogDao();
    private final OrderDao orderDao = Factory.INSTANCE.getOrderDao();
    private final DiscountDao discountDao = Factory.INSTANCE.getDiscountDao();
    private final UserDao userDao = Factory.INSTANCE.getUserDao();
    private final ServiceTypeDao serviceTypeDao = Factory.INSTANCE.getServiceTypeDao();

    @FXML
    private TextField emailTextField;

    @FXML
    private Button loginButton;

    @FXML
    private PasswordField passwordTextField;

    @FXML
    private Button signupButton;

    @FXML
    void signupButtonAction(ActionEvent event) {

        var loader = new FXMLLoader(getClass().getResource("CreateAccount.fxml"));

        Parent pane;
        try {
            pane = loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Error loading CreateAccount.fxml", e);
        }

        var scene = new Scene(pane);
        var stage = new Stage();

        stage.setTitle("Create account");

        stage.setScene(scene);

        stage.show();

        signupButton.getScene().getWindow().hide();
    }

    @FXML
    void loginButtonAction(ActionEvent event) {
        String email = emailTextField.getText();
        String password = passwordTextField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            var alert = new Alert(Alert.AlertType.ERROR, "All fields are required!");
            alert.showAndWait();
            return;
        }

        // zistime, ci email je v databaze
        // findByEmail() vracia typ User
        User user = userDao.findByEmail(email);
        if(user != null) {
            // zistime ci password je spravne

            if(userDao.authenticate(password, user)) {
                // treba zistit rolu
                // podla toho, ktoru rolu ma, sa zobrazi ine okno
                if(user.getRole() == Role.COSTUMER) {
                    loadCostumerWindow(user);
                } else if(user.getRole() == Role.EMPLOYEE) {
                    loadEmployeeWindow();
                }
            } else {
                var alert = new Alert(Alert.AlertType.ERROR, "Incorrect password!");
                alert.showAndWait();
            }
        } else {
            var alert = new Alert(Alert.AlertType.ERROR, "Invalid email!");
            alert.showAndWait();
        }
    }

    private void loadCostumerWindow(User user) {

        var loader = new FXMLLoader(getClass().getResource("CostumerWindow.fxml"));

        Parent pane;
        try {
            pane = loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Error loading CostumerWindow.fxml", e);
        }

        CostumerWindowController controller = loader.getController();
        controller.setCurrentUser(user);

        var scene = new Scene(pane);
        var stage = new Stage();

        stage.setTitle("Costumer");

        stage.setScene(scene);

        stage.show();

        loginButton.getScene().getWindow().hide();
    }

    private void loadEmployeeWindow() {

        var loader = new FXMLLoader(getClass().getResource("EmployeeWindow.fxml"));

        Parent pane;
        try {
            pane = loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Error loading EmployeeWindow.fxml", e);
        }

        var scene = new Scene(pane);
        var stage = new Stage();

        stage.setTitle("Employee");

        stage.setScene(scene);

        stage.show();

        loginButton.getScene().getWindow().hide();
    }

}

