package me.wiceh.companies.listeners;

import me.wiceh.companies.Companies;
import me.wiceh.companies.inventories.CashRegisterInventories;
import me.wiceh.companies.objects.Company;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Optional;

public class CashRegisterListeners implements Listener {

    private final Companies plugin;

    public CashRegisterListeners(Companies plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCashRegisterBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (!plugin.getCashRegisterUtils().isCashRegister(block)) return;
        plugin.getCashRegisterUtils().removeCashRegister(block);
    }

    @EventHandler
    public void onCashRegisterInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            if (block == null) return;
            if (!plugin.getCashRegisterUtils().isCashRegister(block)) return;
            Optional<Company> optionalCompany = plugin.getCashRegisterUtils().getCompany(block);
            if (!optionalCompany.isPresent()) return;
            Company company = optionalCompany.get();
            String lowerName = company.getName().toLowerCase().replace(" ", "-");
            if (!player.hasPermission("cassa." + lowerName)) return;
            new CashRegisterInventories(plugin).openFirst(player, company);
            plugin.getCashRegisterUtils().getCompanyMap().put(player, company);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (!event.getView().getTitle().startsWith("Cassa (")) return;
        event.setCancelled(true);
        ItemStack item = event.getCurrentItem();
        if (item == null) return;
        if (!item.hasItemMeta()) return;
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasDisplayName()) return;
        String displayName = meta.getDisplayName();
        if (ChatColor.stripColor(displayName).equals("Stampa Scontrino")) {
            new CashRegisterInventories(plugin).openSecond(player);
            return;
        }
        if (item.getType() == Material.PLAYER_HEAD) {
            String name = ChatColor.stripColor(meta.getDisplayName());
            Player target = Bukkit.getPlayerExact(name);
            if (target == null) return;
            new CashRegisterInventories(plugin).openThird(player, target);
        }
    }
}
