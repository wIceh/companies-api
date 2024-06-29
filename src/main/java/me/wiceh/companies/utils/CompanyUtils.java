package me.wiceh.companies.utils;

import me.wiceh.companies.Companies;
import me.wiceh.companies.objects.Company;
import org.bukkit.OfflinePlayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class CompanyUtils {

    private final Companies plugin;

    public CompanyUtils(Companies plugin) {
        this.plugin = plugin;
    }

    public CompletableFuture<Optional<Company>> createCompany(String name, OfflinePlayer owner) {
        return CompletableFuture.supplyAsync(() -> {
            String query = "INSERT INTO companies(id, name, owner, balance) VALUES (NULL, ?, ?, ?)";

            try (PreparedStatement statement = plugin.getDatabase().getConnection().prepareStatement(query)) {
                statement.setString(1, name);
                statement.setString(2, owner.getUniqueId().toString());
                statement.setDouble(3, 0.0);

                if (statement.executeUpdate() > 0) {
                    ResultSet result = statement.getGeneratedKeys();
                    return Optional.of(new Company(
                            result.getInt(1),
                            name,
                            owner.getUniqueId().toString(),
                            0.0
                    ));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return Optional.empty();
        });
    }

    public Optional<Company> getCompany(String name) {
        String query = "SELECT * FROM companies WHERE name = ?";

        try (PreparedStatement statement = plugin.getDatabase().getConnection().prepareStatement(query)) {
            statement.setString(1, name);

            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return Optional.of(new Company(
                        result.getInt("id"),
                        name,
                        result.getString("owner"),
                        result.getDouble("balance")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public List<Company> getCompanies() {
        String query = "SELECT * FROM companies";
        List<Company> companies = new ArrayList<>();

        try (PreparedStatement statement = plugin.getDatabase().getConnection().prepareStatement(query)) {
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                companies.add(new Company(
                        result.getInt("id"),
                        result.getString("name"),
                        result.getString("owner"),
                        result.getDouble("balance")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return companies;
    }
}
