package sk.upjs.ics;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import lombok.Setter;
import sk.upjs.ics.dogs.DogDao;
import sk.upjs.ics.dogs.DogModel;
import sk.upjs.ics.dogs.Sex;
import sk.upjs.ics.users.User;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class DogController {

    private final DogDao dogDao = Factory.INSTANCE.getDogDao();
    private final DogModel model = new DogModel();

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField birthDateTextField;

    @FXML
    private Button addButton;

    @FXML
    private ChoiceBox<Sex> sexChoiceBox;

    @Setter
    private User currentUser;

    @FXML
    void addButtonAction(ActionEvent event) {
        // Získanie hodnôt z formulára
        String name = nameTextField.getText().trim();
        Sex sex = sexChoiceBox.getValue();
        String birthDateString = birthDateTextField.getText().trim();
        LocalDate birthDate;

        try {
            birthDate = LocalDate.parse(birthDateString);
        } catch (DateTimeParseException e) {
            var alert = new Alert(Alert.AlertType.ERROR, "Invalid date format. Please use the correct format (YYYY-MM-DD).");
            alert.showAndWait();
            return;
        }

        // Overenie, že všetky polia sú vyplnené
        if (name.isEmpty()) {
            var alert = new Alert(Alert.AlertType.ERROR, "All fields are required!");
            alert.showAndWait();
            return;
        }

        var dog = model.getDog();

        dog.setName(name);
        dog.setBirthDate(birthDate);
        dog.setSex(sex);

        dog.setOwner(currentUser);
        currentUser.getDogs().add(dog);

        dogDao.create(dog);

        addButton.getScene().getWindow().hide();
    }

    @FXML
    public void initialize() {
        // Pridanie možností do ChoiceBox
        sexChoiceBox.getItems().addAll(Sex.values());

        // Nastavenie predvolenej hodnoty
        sexChoiceBox.setValue(Sex.UNKNOWN);
    }

}
