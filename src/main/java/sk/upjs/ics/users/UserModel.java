package sk.upjs.ics.users;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import sk.upjs.ics.dogs.Dog;

import java.time.LocalDate;

public class UserModel {
    private Long id = null;

    private final Property<String> nameProperty = new SimpleStringProperty();
    private final Property<String> surnameProperty = new SimpleStringProperty();
    private final Property<String> emailProperty = new SimpleStringProperty();
    private final Property<String> passwordProperty = new SimpleStringProperty();
    private final Property<LocalDate> birthDateProperty = new SimpleObjectProperty<>();
    private final Property<Role> roleProperty = new SimpleObjectProperty<>();
    private final ObservableSet<Dog> dogSet = FXCollections.observableSet();

    public void setEditMode(User editedUser) {
        this.id = editedUser.getId();
        nameProperty.setValue(editedUser.getName());
        surnameProperty.setValue(editedUser.getSurname());
        emailProperty.setValue(editedUser.getEmail());
        passwordProperty.setValue(editedUser.getPassword());
        birthDateProperty.setValue(editedUser.getBirthDate());
        roleProperty.setValue(editedUser.getRole());

        dogSet.clear();
        dogSet.addAll(editedUser.getDogs());
    }

    public void addDog(Dog dog) {
        dogSet.add(dog);
    }

    public void removeDog(Dog dog) {
        dogSet.remove(dog);
    }

    public ObservableSet<Dog> getDogs() {
        return dogSet;
    }

    public User getUser() {
        User user = new User();
        user.setId(id);
        user.setName(nameProperty.getValue());
        user.setSurname(surnameProperty.getValue());
        user.setEmail(emailProperty.getValue());
        user.setPassword(passwordProperty.getValue());
        user.setBirthDate(birthDateProperty.getValue());
        user.setRole(roleProperty.getValue());
        user.setDogs(dogSet);
        return user;
    }

    public Property<String> nameProperty() {
        return nameProperty;
    }

    public Property<String> surnameProperty() {
        return surnameProperty;
    }

    public Property<String> emailProperty() {
        return emailProperty;
    }

    public Property<String> passwordProperty() {
        return passwordProperty;
    }

    public Property<LocalDate> birthDateProperty() {
        return birthDateProperty;
    }

    public Property<Role> roleProperty() {
        return roleProperty;
    }

    public boolean isEditMode() {
        return id != null;
    }

    public boolean isValid() {
        return nameProperty.getValue() != null && !nameProperty.getValue().isBlank()
                && surnameProperty.getValue() != null && !surnameProperty.getValue().isBlank()
                && emailProperty.getValue() != null && emailProperty.getValue().matches(".+@.+\\..+");
    }

    public boolean isBirthDateValid() {
        LocalDate birthDate = birthDateProperty.getValue();
        return birthDate != null && !birthDate.isAfter(LocalDate.now());
    }

    public boolean isModelValid() {
        return isValid() && isBirthDateValid();
    }

}
