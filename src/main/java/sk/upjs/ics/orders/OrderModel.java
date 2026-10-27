package sk.upjs.ics.orders;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import sk.upjs.ics.discounts.Discount;
import sk.upjs.ics.dogs.Dog;
import sk.upjs.ics.servicetypes.ServiceType;

import java.time.LocalDateTime;

public class OrderModel {

    private Long id = null;

    private final Property<LocalDateTime> dateTimeProperty = new SimpleObjectProperty<>();
    private final Property<Dog> dogProperty = new SimpleObjectProperty<>();
    private final Property<Discount> discountProperty = new SimpleObjectProperty<>();
    private final ObservableSet<ServiceType> serviceSet = FXCollections.observableSet();

    public void setEditMode(Order editedOrder) {
        this.id = editedOrder.getId();
        dateTimeProperty.setValue(editedOrder.getDateTime());
        dogProperty.setValue(editedOrder.getDog());
        discountProperty.setValue(editedOrder.getDiscount());

        serviceSet.clear();
        serviceSet.addAll(editedOrder.getServices());
    }

    public void addService(ServiceType serviceType) {
        serviceSet.add(serviceType);
    }

    public void removeService(ServiceType serviceType) {
        serviceSet.remove(serviceType);
    }

    public ObservableSet<ServiceType> getServices() {
        return serviceSet;
    }

    public Order getOrder() {
        Order order = new Order();
        order.setId(id);
        order.setDateTime(dateTimeProperty.getValue());
        order.setDog(dogProperty.getValue());
        order.setDiscount(discountProperty.getValue());
        order.setServices(serviceSet);
        return order;
    }

    public Property<Dog> dogProperty() {
        return dogProperty;
    }

    public Property<Discount> discountProperty() {
        return discountProperty;
    }

    public Property<LocalDateTime> dateTimeProperty() {
        return dateTimeProperty;
    }

    public boolean isEditMode() {
        return id != null;
    }
}
