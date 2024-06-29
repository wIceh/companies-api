package me.wiceh.companies.database;

import me.wiceh.companies.Companies;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private Connection connection;

    public Database(Companies plugin) {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFile().getAbsolutePath());
            plugin.getLogger().info("Connessione al database effettuata con successo.");
            createCompaniesTable();
            createCashRegistersTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createCompaniesTable() {
        String query = "CREATE TABLE IF NOT EXISTS companies(id INTEGER PRIMARY KEY, name VARCHAR(50) NOT NULL, owner VARCHAR(36) NOT NULL, balance DOUBLE NOT NULL)";

        try (Statement statement = connection.createStatement()) {
            statement.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createCashRegistersTable() {
        String query = "CREATE TABLE IF NOT EXISTS cash_registers(company VARCHAR(50) NOT NULL, location VARCHAR(50) NOT NULL PRIMARY KEY)";

        try (Statement statement = connection.createStatement()) {
            statement.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
