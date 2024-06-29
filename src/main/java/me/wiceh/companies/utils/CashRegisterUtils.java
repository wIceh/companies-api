package me.wiceh.companies.utils;

import me.wiceh.companies.Companies;
import me.wiceh.companies.objects.Company;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class CashRegisterUtils {

    private final Companies plugin;
    private final Map<Player, Company> companyMap = new HashMap<>();

    public CashRegisterUtils(Companies plugin) {
        this.plugin = plugin;
    }

    public CompletableFuture<Boolean> addCashRegister(Company company, Block block) {
        return CompletableFuture.supplyAsync(() -> {
            String query = "INSERT INTO cash_registers(company, location) VALUES (?, ?)";

            try (PreparedStatement statement = plugin.getDatabase().getConnection().prepareStatement(query)) {
                statement.setString(1, company.getName());
                statement.setString(2, block.getLocation().getX() + "|" + block.getLocation().getY() + "|" + block.getLocation().getZ());

                return statement.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return false;
        });
    }

    public CompletableFuture<Boolean> removeCashRegister(Block block) {
        return CompletableFuture.supplyAsync(() -> {
            String query = "DELETE FROM cash_registers WHERE location = ?";

            try (PreparedStatement statement = plugin.getDatabase().getConnection().prepareStatement(query)) {
                statement.setString(1, block.getLocation().getX() + "|" + block.getLocation().getY() + "|" + block.getLocation().getZ());

                return statement.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return false;
        });
    }

    public boolean isCashRegister(Block block) {
        String query = "SELECT * FROM cash_registers WHERE location = ?";

        try (PreparedStatement statement = plugin.getDatabase().getConnection().prepareStatement(query)) {
            statement.setString(1, block.getLocation().getX() + "|" + block.getLocation().getY() + "|" + block.getLocation().getZ());

            ResultSet result = statement.executeQuery();
            return result.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public Optional<Company> getCompany(Block cashRegister) {
        String query = "SELECT * FROM cash_registers WHERE location = ?";

        try (PreparedStatement statement = plugin.getDatabase().getConnection().prepareStatement(query)) {
            statement.setString(1, cashRegister.getLocation().getX() + "|" + cashRegister.getLocation().getY() + "|" + cashRegister.getLocation().getZ());

            ResultSet result = statement.executeQuery();
            if (result.next()) {
                String company = result.getString("company");
                return plugin.getCompanyUtils().getCompany(company);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public Map<Player, Company> getCompanyMap() {
        return companyMap;
    }
}
