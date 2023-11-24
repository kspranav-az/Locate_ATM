package com.example.atmmachine;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Result extends Application {
    private TableView<ATMBranch> atmTable;
    private TableColumn<ATMBranch, Integer> branchIdColumn;
    private TableColumn<ATMBranch, String> branchNameColumn;
    private TableColumn<ATMBranch, String> streetColumn;
    private TableColumn<ATMBranch, String> cityColumn;
    private TableColumn<ATMBranch, String> stateColumn;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Create a VBox to hold the title label and the TableView
        VBox vbox = new VBox();

        // Create a title label
        Label titleLabel = new Label("ATM Branches ");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 10px;");

        // Add the title label to the VBox
        vbox.getChildren().add(titleLabel);

        // Initialize TableView and TableColumns
        atmTable = new TableView<>();
        branchIdColumn = new TableColumn<>("Branch ID");
        branchNameColumn = new TableColumn<>("Branch Name");
        streetColumn = new TableColumn<>("Street");
        cityColumn = new TableColumn<>("City");
        stateColumn = new TableColumn<>("State");

        // Set cell value factories for each column
        branchIdColumn.setCellValueFactory(cellData -> cellData.getValue().branchIdProperty().asObject());
        branchNameColumn.setCellValueFactory(cellData -> cellData.getValue().branchNameProperty());
        streetColumn.setCellValueFactory(cellData -> cellData.getValue().getAddress().streetProperty());
        cityColumn.setCellValueFactory(cellData -> cellData.getValue().getAddress().getCity().cityNameProperty());
        stateColumn.setCellValueFactory(cellData -> cellData.getValue().getAddress().getCity().stateProperty());

        // Add columns to the TableView
        atmTable.getColumns().addAll(branchIdColumn, branchNameColumn, streetColumn, cityColumn, stateColumn);

        // Set the TableView to expand to fill available space
        atmTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Add the TableView to the VBox
        vbox.getChildren().add(atmTable);

        // Create the Scene with the VBox
        Scene scene = new Scene(vbox, 600, 400);

        // Set the Scene to the Stage
        primaryStage.setScene(scene);

        // Show the Stage
        primaryStage.show();
    }

    public void start(Stage primaryStage, String selectedCity, String selectedBank, Connection connection) throws SQLException {
        // Now you have the selectedCity and selectedBank information
        System.out.println("Selected City: " + selectedCity);
        System.out.println("Selected Bank: " + selectedBank);

        // Create a VBox to hold the title label and the TableView
        VBox vbox = new VBox();

        // Create a title label
        Label titleLabel = new Label("ATM Branches in " + selectedCity + " for " + selectedBank);
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 10px;");

        // Add the title label to the VBox
        vbox.getChildren().add(titleLabel);

        // Initialize TableView and TableColumns
        atmTable = new TableView<>();
        branchIdColumn = new TableColumn<>("Branch ID");
        branchNameColumn = new TableColumn<>("Branch Name");
        streetColumn = new TableColumn<>("Street");
        cityColumn = new TableColumn<>("City");
        stateColumn = new TableColumn<>("State");

        // Set cell value factories for each column
        branchIdColumn.setCellValueFactory(cellData -> cellData.getValue().branchIdProperty().asObject());
        branchNameColumn.setCellValueFactory(cellData -> cellData.getValue().branchNameProperty());
        streetColumn.setCellValueFactory(cellData -> cellData.getValue().getAddress().streetProperty());
        cityColumn.setCellValueFactory(cellData -> cellData.getValue().getAddress().getCity().cityNameProperty());
        stateColumn.setCellValueFactory(cellData -> cellData.getValue().getAddress().getCity().stateProperty());

        // Add columns to the TableView
        atmTable.getColumns().addAll(branchIdColumn, branchNameColumn, streetColumn, cityColumn, stateColumn);

        // Set the TableView to expand to fill available space
        atmTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Add the TableView to the VBox
        vbox.getChildren().add(atmTable);

        // Populate the TableView with data
        atmTable.getItems().setAll(retrieveDataFromDatabase(connection, selectedCity, selectedBank));

        // Create the Scene with the VBox
        Scene scene = new Scene(vbox, 600, 400);

        // Set the Scene to the Stage
        primaryStage.setScene(scene);

        // Show the Stage
        primaryStage.show();
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
}
