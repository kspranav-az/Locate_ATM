package com.example.atmmachine;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HelloApplication extends Application {
//    @Override
//    public void start(Stage stage) throws IOException {
//        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
//        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
//        stage.setTitle("Hello!");
//        stage.setScene(scene);
//        stage.show();
//    }

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Error loading MySQL JDBC driver", e);
        }
    }
    private ObservableList<String> banks = FXCollections.observableArrayList("Bank A", "ABC Bank", "Bank C");
    private ComboBox<String> cityComboBox;
    private ComboBox<String> bankComboBox;
    private TextArea resultTextArea;

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/atm_database";
    private static final String USER = "root";
    private static final String PASSWORD = "2004";

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

        Label cityLabel = new Label("Current City:");
        cityComboBox = new ComboBox<>();
        // Add your actual city names
        cityComboBox.getItems().addAll("City1", "Mumbai", "City3");
        cityComboBox.setPromptText("Select City");

        Label bankLabel = new Label("Bank Name:");
        bankComboBox = new ComboBox<>(banks);
        bankComboBox.setPromptText("Select Bank");

        Button searchButton = new Button("Search");
        searchButton.setOnAction(e -> searchATMCenters());

        resultTextArea = new TextArea();
        resultTextArea.setEditable(false);

        grid.add(cityLabel, 0, 0);
        grid.add(cityComboBox, 1, 0);
        grid.add(bankLabel, 0, 1);
        grid.add(bankComboBox, 1, 1);
        grid.add(searchButton, 0, 2);
        grid.add(resultTextArea, 0, 3, 2, 1);

        Scene scene = new Scene(grid, 400, 300);
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
            // Perform database search based on selectedCity and selectedBank
            ATMBranch[] atmBranches = retrieveDataFromDatabase(connection, selectedCity, selectedBank);

            // Display the results in the TextArea
            if (atmBranches != null && atmBranches.length > 0) {
                StringBuilder resultText = new StringBuilder("ATM Centers in " + selectedCity + " for " + selectedBank + ":\n");
                for (ATMBranch branch : atmBranches) {
                    resultText.append("Branch Name: ").append(branch.getBranchName()).append("\n");
                    resultText.append("Address: ").append(branch.getAddress().getStreet()).append(", ")
                            .append(branch.getAddress().getCity().getCityName()).append(", ")
                            .append(branch.getAddress().getCity().getState()).append("\n\n");
                }
                resultTextArea.setText(resultText.toString());
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
                    City branchCity = new City(resultSet.getString("city_name"), resultSet.getString("state_id"));

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
}