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
import sk.upjs.ics.users.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class SignupController {

    private final UserDao userDao = Factory.INSTANCE.getUserDao();
    private final UserModel model = new UserModel();
    private final PasswordHasher hasher = new PasswordHasher();

    @FXML
    private TextField emailTextField;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField birthDateTextField;

    @FXML
    private PasswordField passwordTextField;

    @FXML
    private Button loginButton;

    @FXML
    private Button createAccountButton;

    @FXML
    void loginButtonAction(ActionEvent event) {

        var loader = new FXMLLoader(getClass().getResource("WelcomeScreen.fxml"));

        Parent pane;
        try {
            pane = loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Error loading WelcomeScreen.fxml", e);
        }

        loginButton.getScene().getWindow().hide();

        var scene = new Scene(pane);
        var stage = new Stage();

        stage.setTitle("Log in");

        stage.setScene(scene);

        stage.show();
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

        System.out.println("Controller instance: " + controller);

        var scene = new Scene(pane);
        var stage = new Stage();

        stage.setTitle("Costumer");

        stage.setScene(scene);

        stage.show();

        createAccountButton.getScene().getWindow().hide();
    }

    @FXML
    void createAccountButtonAction(ActionEvent event) {
        // Získanie hodnôt z formulára
        String birthDateString = birthDateTextField.getText().trim();
        LocalDate birthDate;

        try {
            birthDate = LocalDate.parse(birthDateString);
        } catch (DateTimeParseException e) {
            var alert = new Alert(Alert.AlertType.ERROR, "Invalid date format. Please use the correct format (e.g., YYYY-MM-DD).");
            alert.showAndWait();
            return;
        }

        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailTextField.getText().trim();
        String password = passwordTextField.getText();

        // Overenie, že všetky polia sú vyplnené
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            var alert = new Alert(Alert.AlertType.ERROR, "All fields are required!");
            alert.showAndWait();
            return;
        }

        // Overenie, či e-mail už existuje
        if (userDao.findByEmail(email) == null) {

            // Ak neexistuje, vytvorenie nového používateľa a nastavenie všetkých jeho vlastností
            var user = model.getUser();

            user.setName(firstName);
            user.setSurname(lastName);
            user.setRole(Role.COSTUMER);
            user.setEmail(email);

            String passwordHash = hasher.hashPassword(password);
            user.setPassword(passwordHash);

            user.setBirthDate(birthDate);

            // Vloženie používateľa do databázy
            userDao.create(user);

            loadCostumerWindow(user);

        } else {
            // Ak existuje, vypise hlasku
            var alert = new Alert(Alert.AlertType.ERROR, "User with that email already exists!");
            alert.showAndWait();
        }
    }
}

