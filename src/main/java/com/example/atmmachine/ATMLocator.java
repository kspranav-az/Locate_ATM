package com.example.atmmachine;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ATMLocator extends Application {
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Error loading MySQL JDBC driver", e);
        }
    }

    private ComboBox<String> cityComboBox;
    private ComboBox<String> bankComboBox;
    private TextArea resultTextArea;

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/atm_database";
    private static final String USER = "root";
    private static final String PASSWORD = "2004";

    Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    private final ObservableList<String> banks = FXCollections.observableArrayList(getBanks());
    private final ObservableList<String> cities = FXCollections.observableArrayList(getCities());

    public ATMLocator() throws SQLException {
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("ATM Search Form");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 20, 20));

        Label titleLabel = new Label("ATM Search Form");
        titleLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-padding: 0 0 10 0;");
        GridPane.setConstraints(titleLabel, 0, 0, 2, 1);

        Label cityLabel = new Label("Current City:");
        cityComboBox = new ComboBox<>(cities);
        cityComboBox.setPromptText("Select City");

        Label bankLabel = new Label("Bank Name:");
        bankComboBox = new ComboBox<>(banks);
        bankComboBox.setPromptText("Select Bank");

        Button searchButton = new Button("Search");
        searchButton.getStyleClass().add("btn-primary");
        searchButton.setOnAction(e -> searchATMCenters());

        resultTextArea = new TextArea();
        resultTextArea.setEditable(false);
        resultTextArea.setPrefHeight(150); // Adjust the height as needed

        GridPane.setConstraints(cityLabel, 0, 1);
        GridPane.setConstraints(cityComboBox, 1, 1);
        GridPane.setConstraints(bankLabel, 0, 2);
        GridPane.setConstraints(bankComboBox, 1, 2);
        GridPane.setConstraints(searchButton, 0, 3, 2, 1);
        GridPane.setConstraints(resultTextArea, 0, 4, 2, 1);

        grid.getChildren().addAll(titleLabel, cityLabel, cityComboBox, bankLabel, bankComboBox, searchButton, resultTextArea);

        Scene scene = new Scene(grid, 400, 400);
        scene.getStylesheets().add("org/kordamp/bootstrapfx/bootstrapfx.css"); // Add Bootstrap styles
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void searchATMCenters() {
        String selectedCity = cityComboBox.getValue();
        String selectedBank = bankComboBox.getValue();

        if (selectedCity == null || selectedBank == null) {
            resultTextArea.setText("Please select both city and bank.");
            return;
        }

        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
            ATMBranch[] atmBranches = retrieveDataFromDatabase(connection, selectedCity, selectedBank);

            if (atmBranches != null && atmBranches.length > 0) {
                StringBuilder resultText = new StringBuilder("ATM Centers in " + selectedCity + " for " + selectedBank + ":\n");
                for (ATMBranch branch : atmBranches) {
                    resultText.append("Branch Name: ").append(branch.getBranchName()).append("\n");
                    resultText.append("Address: ").append(branch.getAddress().getStreet()).append(", ")
                            .append(branch.getAddress().getCity().getCityName()).append(", ")
                            .append(branch.getAddress().getCity().getState()).append("\n\n");
                }
                resultTextArea.setText(resultText.toString());

                Result resultStage = new Result();
                resultStage.start(new Stage(), selectedCity, selectedBank, connection);
            } else {
                resultTextArea.setText("No ATM Centers found in " + selectedCity + " for " + selectedBank + ".");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ATMBranch[] retrieveDataFromDatabase(Connection connection, String city, String bank) throws SQLException {
        String retrieveQuery = "SELECT * FROM ATMBranch " +
                "JOIN Address ON ATMBranch.address_id = Address.address_id " +
                "JOIN City ON Address.city_id = City.city_id " +
                "JOIN STATE USING (State_id)" +
                "WHERE City.city_name = ?";

        try (PreparedStatement statement = connection.prepareStatement(retrieveQuery)) {
            statement.setString(1, city);

            try (ResultSet resultSet = statement.executeQuery()) {
                List<ATMBranch> atmBranches = new ArrayList<>();

                while (resultSet.next()) {
                    City branchCity = new City(resultSet.getString("city_name"), resultSet.getString("state_name"));

                    Address branchAddress = new Address(
                            resultSet.getString("street"),
                            resultSet.getString("zip_code"),
                            branchCity
                    );

                    ATMBranch branch = new ATMBranch(
                            resultSet.getInt("branch_id"),
                            resultSet.getString("branch_name"),
                            branchAddress
                    );

                    atmBranches.add(branch);
                }

                return atmBranches.toArray(new ATMBranch[0]);
            }
        }
    }

    private List<String> getBanks() throws SQLException {
        List<String> banks = new ArrayList<>();
        String query = "SELECT Distinct branch_name" +
                " FROM atm_database.atmbranch";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                banks.add(resultSet.getString("branch_name"));
            }
        } catch (SQLException ignored) {
        }
        return banks;
    }

    private List<String> getCities() throws SQLException {
        List<String> cities = new ArrayList<>();
        String query = "SELECT Distinct city_name" +
                " FROM city";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cities.add(resultSet.getString("city_name"));
            }
        } catch (SQLException ignored) {
        }
        return cities;
    }
}
