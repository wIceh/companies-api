package me.wiceh.companies.commands;

import me.wiceh.companies.Companies;
import me.wiceh.companies.objects.Company;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static net.kyori.adventure.text.Component.text;

public class CassaCommand implements CommandExecutor {

    private final Companies plugin;

    public CassaCommand(Companies plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        if (args.length >= 2 && args[0].equalsIgnoreCase("crea")) {
            List<String> nameList = new ArrayList<>(Arrays.asList(args).subList(1, args.length));
            String name = String.join(" ", nameList);

            Optional<Company> optionalCompany = plugin.getCompanyUtils().getCompany(name);
            if (!optionalCompany.isPresent()) {
                player.sendMessage(text("§cNon esiste nessun'azienda con questo nome."));
                return true;
            }
            Company company = optionalCompany.get();

            if (!player.getUniqueId().toString().equals(company.getOwner())) {
                player.sendMessage(text("§cNon sei il direttore di questa azienda."));
                return true;
            }

            Block block = player.getTargetBlock(5);
            if (block != null && block.getType() == Material.PRISMARINE_STAIRS) {
                if (plugin.getCashRegisterUtils().isCashRegister(block)) {
                    player.sendMessage(text("§cQuesta cassa è già impostata."));
                    return true;
                }
                plugin.getCashRegisterUtils().addCashRegister(company, block).thenAccept(result -> {
                    if (result) {
                        player.sendMessage(text("§aCassa impostata con successo."));
                        return;
                    }
                    player.sendMessage(text("§cSi sono riscontrati dei problemi nel impostare questa cassa."));
                });
            } else {
                player.sendMessage(text("§cNon stai guardando una cassa."));
            }
        } else {
            sendUsage(player);
        }

        return true;
    }

    private void sendUsage(Player player) {
        player.sendMessage(text("§f"));
        player.sendMessage(text(" §c§lAiuto Comandi §7(/cassa)"));
        player.sendMessage(text("  §8▪ §e/cassa crea <azienda>"));
        player.sendMessage(text("§f"));
    }
}
