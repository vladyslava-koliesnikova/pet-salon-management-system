package sk.upjs.ics;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import sk.upjs.ics.users.PasswordHasher;
import sk.upjs.ics.users.Role;
import sk.upjs.ics.users.UserDao;
import sk.upjs.ics.users.UserModel;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class AddEmployeeController {

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
    private Button addButton;

    @FXML
    void addButtonAction(ActionEvent event) {
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

        // Overenie, či e-mail existuje - metoda findByEmail()
        if(userDao.findByEmail(email) == null) {

            // Ak neexistuje, vloženie nového používateľa
            var user = model.getUser();

            user.setName(firstName);
            user.setSurname(lastName);
            user.setRole(Role.EMPLOYEE);
            user.setEmail(email);

            String passwordHash = hasher.hashPassword(password);
            user.setPassword(passwordHash);

            user.setBirthDate(birthDate);

            userDao.create(user);

            addButton.getScene().getWindow().hide();

        } else {
            // Ak existuje, vypise hlasku
            var alert = new Alert(Alert.AlertType.ERROR, "User with that email already exists!");
            alert.showAndWait();
            return;
        }
    }
}
