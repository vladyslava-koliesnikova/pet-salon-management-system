package sk.upjs.ics.dogs;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import sk.upjs.ics.orders.Order;
import sk.upjs.ics.users.User;
import java.time.LocalDate;

public class DogModel {
    private Long id = null;

    private final Property<String> nameProperty = new SimpleStringProperty();
    private final Property<Sex> sexProperty = new SimpleObjectProperty<>();
    private final Property<LocalDate> birthDateProperty = new SimpleObjectProperty<>();
    private final Property<User> ownerProperty = new SimpleObjectProperty<>();
    private final ObservableSet<Order> orderSet = FXCollections.observableSet();

    public void setEditMode(Dog editedDog) {
        this.id = editedDog.getId();
        nameProperty.setValue(editedDog.getName());
        sexProperty.setValue(editedDog.getSex());
        birthDateProperty.setValue(editedDog.getBirthDate());
        ownerProperty.setValue(editedDog.getOwner());

        orderSet.clear();
        orderSet.addAll(editedDog.getOrders());
    }

    public void addOrder(Order order) {
        orderSet.add(order);
    }

    public void removeOrder(Order order) {
        orderSet.remove(order);
    }

    public ObservableSet<Order> getOrders() {
        return orderSet;
    }

    public Dog getDog() {
        Dog dog = new Dog();
        dog.setId(id);
        dog.setName(nameProperty.getValue());
        dog.setSex(sexProperty.getValue());
        dog.setBirthDate(birthDateProperty.getValue());
        dog.setOwner(ownerProperty.getValue());
        dog.setOrders(orderSet);
        return dog;
    }

    public Property<String> nameProperty() {
        return nameProperty;
    }

    public Property<Sex> sexProperty() {
        return sexProperty;
    }

    public Property<LocalDate> birthDateProperty() {
        return birthDateProperty;
    }

    public Property<User> ownerProperty() {
        return ownerProperty;
    }

    public boolean isEditMode() {
        return id != null;
    }
}
