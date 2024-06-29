package me.wiceh.companies.inventories;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import me.wiceh.companies.Companies;
import me.wiceh.companies.objects.Company;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static net.kyori.adventure.text.Component.text;

public class CashRegisterInventories {

    private final Companies plugin;

    public CashRegisterInventories(Companies plugin) {
        this.plugin = plugin;
    }

    public void openFirst(Player player, Company company) {
        Inventory inventory = Bukkit.createInventory(null, 27, "Cassa (" + company.getName() + ")");

        ItemStack stampaScontrino = ItemBuilder.from(Material.PAPER)
                .name(text("§7Stampa Scontrino"))
                .build();

        ItemStack cronologiaScontrini = ItemBuilder.from(Material.PAPER)
                .name(text("§7Cronologia Scontrini"))
                .build();

        inventory.setItem(12, stampaScontrino);
        inventory.setItem(14, cronologiaScontrini);

        player.openInventory(inventory);
    }

    public void openSecond(Player player) {
        Company company = plugin.getCashRegisterUtils().getCompanyMap().get(player);
        if (company == null) return;

        PaginatedGui gui = Gui.paginated()
                .title(text("Cassa (" + company.getName() + ")"))
                .rows(3)
                .pageSize(18)
                .create();

        GuiItem BACK = ItemBuilder.from(Material.GLASS_PANE)
                .name(text("§cIndietro"))
                .model(68)
                .asGuiItem(event -> openFirst(player, company));

        GuiItem GLASS = ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE)
                .name(text("§f"))
                .asGuiItem();

        GuiItem PREVIOUS_PAGE = ItemBuilder.from(Material.GLASS_PANE)
                .name(text("§ePagina precedente"))
                .model(72)
                .asGuiItem(event -> gui.previous());

        GuiItem NEXT_PAGE = ItemBuilder.from(Material.GLASS_PANE)
                .name(text("§ePagina successiva"))
                .model(73)
                .asGuiItem(event -> gui.next());

        gui.setItem(18, BACK);
        gui.setItem(19, GLASS);
        gui.setItem(20, GLASS);
        gui.setItem(21, GLASS);
        gui.setItem(22, GLASS);
        gui.setItem(23, GLASS);
        gui.setItem(24, GLASS);
        gui.setItem(25, PREVIOUS_PAGE);
        gui.setItem(26, NEXT_PAGE);

        List<Player> nearbyPlayers = Bukkit.getOnlinePlayers().stream().filter(nearbyPlayer -> nearbyPlayer.getLocation().distance(player.getLocation()) < 10).collect(Collectors.toList());

        for (Player nearbyPlayer : nearbyPlayers) {
            GuiItem item = ItemBuilder.from(Material.PLAYER_HEAD)
                    .name(text("§6" + nearbyPlayer.getName()))
                    .setSkullOwner(nearbyPlayer)
                    .asGuiItem();

            gui.addItem(item);
        }

        gui.open(player);
    }

    public void openThird(Player player, Player target) {
        new AnvilGUI.Builder()
                .onClick((slot, stateSnapshot) -> {
                    if (slot != AnvilGUI.Slot.OUTPUT) {
                        return Collections.emptyList();
                    }

                    if (stateSnapshot.getText().trim().isEmpty()) {
                        player.sendMessage(text("§cInserisci i prodotti."));
                        return Arrays.asList(AnvilGUI.ResponseAction.close());
                    }

                    String text = stateSnapshot.getText();
                    String products = text.startsWith(" ") ? text.substring(1) : text;
                    openFourth(player, target, products);

                    return Arrays.asList(AnvilGUI.ResponseAction.close());
                })
                .text(" ")
                .title("Inserisci i prodotti")
                .plugin(plugin)
                .open(player);
    }

    public void openFourth(Player player, Player target, String products) {
        new AnvilGUI.Builder()
                .onClick((slot, stateSnapshot) -> {
                    if (slot != AnvilGUI.Slot.OUTPUT) {
                        return Collections.emptyList();
                    }

                    if (stateSnapshot.getText().trim().isEmpty()) {
                        player.sendMessage(text("§cInserisci un prezzo valido."));
                        return Arrays.asList(AnvilGUI.ResponseAction.close());
                    }

                    String text = stateSnapshot.getText();

                    try {
                        Integer.parseInt(text);
                    } catch (NumberFormatException e) {
                        player.sendMessage(text("§cInserisci un prezzo valido."));
                        return Arrays.asList(AnvilGUI.ResponseAction.close());
                    }

                    int price = Integer.parseInt(text);
                    stampaScontrino(player, target, products, price);

                    return Arrays.asList(AnvilGUI.ResponseAction.close());
                })
                .text(" ")
                .title("Inserisci il prezzo")
                .plugin(plugin)
                .open(player);
    }

    private void stampaScontrino(Player player, Player target, String products, int price) {
        Company company = plugin.getCashRegisterUtils().getCompanyMap().get(player);
        if (company == null) return;
        ItemStack scontrino = ItemBuilder.from(Material.PAPER)
                .name(text("§7Scontrino " + company.getName()))
                .lore(
                        text("§fVenditore: §7" + player.getName()),
                        text("§fCliente: §7" + target.getName()),
                        text("§fProdotti: §7" + products),
                        text("§fPrezzo: §7" + price + "€"),
                        text("§fData: §7" + formatDate(LocalDateTime.now()))
                )
                .build();

        target.getInventory().addItem(scontrino);

        // rimuove i soldi al venditore
        // aggiunge lo scontrino alla cronologia degli scontrini dell'azienda
        player.sendMessage(text("§aScontrino stampato con successo."));
    }

    private String formatDate(LocalDateTime date) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return date.format(dateTimeFormatter);
    }
}
