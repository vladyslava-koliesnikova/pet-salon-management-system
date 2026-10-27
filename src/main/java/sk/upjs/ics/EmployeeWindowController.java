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
import sk.upjs.ics.discounts.DiscountDao;
import sk.upjs.ics.dogs.Dog;
import sk.upjs.ics.dogs.DogDao;
import sk.upjs.ics.orders.Order;
import sk.upjs.ics.orders.OrderDao;
import sk.upjs.ics.servicetypes.ServiceTypeDao;
import sk.upjs.ics.users.UserDao;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javafx.stage.FileChooser;
import javafx.scene.control.Alert;
import sk.upjs.ics.servicetypes.ServiceType;

import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class EmployeeWindowController {

    private final DogDao dogDao = Factory.INSTANCE.getDogDao();
    private final OrderDao orderDao = Factory.INSTANCE.getOrderDao();
    private final DiscountDao discountDao = Factory.INSTANCE.getDiscountDao();
    private final UserDao userDao = Factory.INSTANCE.getUserDao();
    private final ServiceTypeDao serviceTypeDao = Factory.INSTANCE.getServiceTypeDao();
    @FXML
    private ListView<Object> orderDoglistView;
    @FXML
    private Button addEmployeeButton;

    @FXML
    private Button clientsButton;

    @FXML
    private Button signOffButton;

    @FXML
    private Button upcomingBookingsButton;

    @FXML
    private Button pastBookingsButton;

    @FXML
    private Button removeSelectedButton;

    @FXML
    private Button showStatisticsButton;

    @FXML
    private ListView<Order> orderListView;

    @FXML
    private Button generateOrdersButton;

    @FXML
    private Button showEmployeesButton;

    @FXML
    private Button allOrdersButton;

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
    void addEmployeeButtonAction(ActionEvent event) {

        var loader = new FXMLLoader(getClass().getResource("ADDEmployeeWindow.fxml"));

        Parent pane;
        try {
            pane = loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Error loading AddEmployeeWindow.fxml", e);
        }

        var scene = new Scene(pane);
        var stage = new Stage();

        stage.setTitle("Add employee");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(addEmployeeButton.getScene().getWindow());

        stage.setScene(scene);

        stage.show();
    }

    @FXML
    void showStatistiscButtonAction(ActionEvent event) {

        var loader = new FXMLLoader(getClass().getResource("StatisticsWindow.fxml"));

        Parent pane;
        try {
            pane = loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Error loading StatisticsWindow.fxml", e);
        }

        var scene = new Scene(pane);
        var stage = new Stage();

        stage.setTitle("Statistics");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(showStatisticsButton.getScene().getWindow());

        stage.setScene(scene);

        stage.show();
    }

    @FXML
    void upcomingBookingsButtonAction(ActionEvent event) {
        orderListView.getItems().clear();
        List<Order> upcomingBookings = orderDao.getAllUpcomingBookings();
        orderListView.getItems().addAll(upcomingBookings);
    }

    @FXML
    void pastBookingsButtonAction(ActionEvent event) {
        orderListView.getItems().clear();
        List<Order> pastBookings = orderDao.getAllPastBookings();
        orderListView.getItems().addAll(pastBookings);
    }

    @FXML
    void allOrdersButtonAction(ActionEvent event) {
        orderListView.getItems().clear();
        List<Order> allOrders = orderDao.getAllOrders();
        orderListView.getItems().addAll(allOrders);
    }

    @FXML
    void removeButtonAction(ActionEvent event) {
        Order selectedOrder = orderListView
                .getSelectionModel()
                .getSelectedItem();

        if (selectedOrder == null) {
            var alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.WARNING,
                    "Please select an order to remove."
            );
            alert.showAndWait();
            return;
        }

        var confirmation = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.CONFIRMATION,
                "Are you sure you want to remove this booking?",
                javafx.scene.control.ButtonType.YES,
                javafx.scene.control.ButtonType.NO
        );

        var result = confirmation.showAndWait();

        if (result.isPresent()
                && result.get() == javafx.scene.control.ButtonType.YES) {
            orderDao.delete(selectedOrder.getId());
            orderListView.getItems().remove(selectedOrder);
        }
    }


    @FXML
    void clientsButtonAction(ActionEvent event) {

        List<sk.upjs.ics.users.User> clients = userDao.getAllClients();

        javafx.scene.control.ListView<String> clientsListView =
                new javafx.scene.control.ListView<>();

        for (sk.upjs.ics.users.User client : clients) {

            StringBuilder information = new StringBuilder();

            information.append(client.getName())
                    .append(" ")
                    .append(client.getSurname())
                    .append(" | Email: ")
                    .append(client.getEmail());

            if (client.getDogs() != null && !client.getDogs().isEmpty()) {
                information.append("\nDogs: ");

                for (Dog dog : client.getDogs()) {
                    information.append(dog.getName()).append(", ");
                }

                information.setLength(information.length() - 2);
            } else {
                information.append("\nDogs: None");
            }

            clientsListView.getItems().add(information.toString());
        }

        clientsListView.setPlaceholder(
                new javafx.scene.control.Label("No clients found.")
        );

        clientsListView.setCellFactory(list -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
                setWrapText(true);
                setPrefHeight(empty ? 35 : 65);
            }
        });

        javafx.scene.layout.VBox root =
                new javafx.scene.layout.VBox(10, clientsListView);

        root.setPadding(new javafx.geometry.Insets(15));

        Stage stage = new Stage();
        stage.setTitle("Clients");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(clientsButton.getScene().getWindow());
        stage.setScene(new Scene(root, 600, 400));
        stage.show();
    }


    @FXML
    void showEmployeesButtonAction(ActionEvent event) {

        List<sk.upjs.ics.users.User> employees = userDao.findAll();

        javafx.scene.control.ListView<String> employeesListView =
                new javafx.scene.control.ListView<>();

        for (sk.upjs.ics.users.User employee : employees) {
            if (employee.getRole() == sk.upjs.ics.users.Role.EMPLOYEE) {
                employeesListView.getItems().add(
                        employee.getName() + " " + employee.getSurname()
                                + " | Email: " + employee.getEmail()
                );
            }
        }

        employeesListView.setPlaceholder(
                new javafx.scene.control.Label("No employees found.")
        );

        javafx.scene.layout.VBox root =
                new javafx.scene.layout.VBox(10, employeesListView);

        root.setPadding(new javafx.geometry.Insets(15));

        Stage stage = new Stage();
        stage.setTitle("Employees");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(showEmployeesButton.getScene().getWindow());
        stage.setScene(new Scene(root, 600, 400));
        stage.show();
    }


    @FXML
    void exportBookingsButtonAction(ActionEvent event) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export bookings");
        fileChooser.setInitialFileName("petsalon_bookings.csv");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "CSV files (*.csv)", "*.csv"
                )
        );

        File file = fileChooser.showSaveDialog(
                generateOrdersButton.getScene().getWindow()
        );

        if (file == null) {
            return;
        }

        try {
            List<Order> orders = orderDao.getAllOrders();

            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            try (BufferedWriter writer = Files.newBufferedWriter(
                    file.toPath(),
                    StandardCharsets.UTF_8
            )) {
                // UTF-8 BOM helps Excel recognize the encoding.
                writer.write('\uFEFF');

                writer.write("Order ID;Date;Dog;Services;Total price (EUR)");
                writer.newLine();

                for (Order order : orders) {

                    String dogName = order.getDog() != null
                            && order.getDog().getName() != null
                            ? order.getDog().getName()
                            : "";

                    List<String> serviceNames = new ArrayList<>();
                    double totalPrice = 0;

                    if (order.getServices() != null) {
                        for (ServiceType service : order.getServices()) {
                            if (service == null) {
                                continue;
                            }

                            serviceNames.add(service.getName());
                            totalPrice += service.getPrice();
                        }
                    }

                    String services = String.join(", ", serviceNames);

                    String date = order.getDateTime() != null
                            ? order.getDateTime().format(formatter)
                            : "";

                    writer.write(
                            csv(order.getId()) + ";" +
                                    csv(date) + ";" +
                                    csv(dogName) + ";" +
                                    csv(services) + ";" +
                                    String.format(java.util.Locale.US,
                                            "%.2f", totalPrice)
                    );

                    writer.newLine();
                }
            }

            Alert success = new Alert(
                    Alert.AlertType.INFORMATION,
                    "Bookings exported successfully!\n"
                            + file.getAbsolutePath()
            );
            success.setHeaderText("Export completed");
            success.showAndWait();

        } catch (Exception e) {
            Alert error = new Alert(
                    Alert.AlertType.ERROR,
                    "Export failed: " + e.getMessage()
            );
            error.setHeaderText("Export error");
            error.showAndWait();
            e.printStackTrace();
        }
    }

    private String csv(Object value) {
        String text = value == null ? "" : value.toString();
        return "\"" + text.replace("\"", "\"\"") + "\"";
    }



}
