package me.wiceh.companies.commands;

import me.wiceh.companies.Companies;
import me.wiceh.companies.objects.Company;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static net.kyori.adventure.text.Component.text;

public class AziendaCommand implements CommandExecutor, TabCompleter {

    private final Companies plugin;

    public AziendaCommand(Companies plugin) {
        this.plugin = plugin;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) return null;

        Player player = (Player) sender;

        if (args.length == 1) {
            List<String> subcommands = new ArrayList<>();
            if (player.hasPermission("azienda.crea"))
                subcommands.add("crea");

            return subcommands;
        }

        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        if (args.length >= 3 && args[0].equalsIgnoreCase("crea")) {
            if (!player.hasPermission("azienda.crea")) {
                player.sendMessage(text("§cNon hai il permesso."));
                return true;
            }

            List<String> nameList = new ArrayList<>(Arrays.asList(args).subList(1, args.length - 1));
            String name = String.join(" ", nameList);

            OfflinePlayer target = Bukkit.getOfflinePlayer(args[args.length - 1]);
            if (!target.hasPlayedBefore()) {
                player.sendMessage(text("§cQuesto player non è mai entrato nel server."));
                return true;
            }

            Optional<Company> optionalCompany = plugin.getCompanyUtils().getCompany(name);
            if (optionalCompany.isPresent()) {
                player.sendMessage(text("§cEsiste già un'azienda con questo nome."));
                return true;
            }

            plugin.getCompanyUtils().createCompany(name, target).thenAccept(result -> {
               if (result.isPresent()) {
                   player.sendMessage(text("§aAzienda creata con successo."));
                   return;
               }
               player.sendMessage(text("§cSi sono riscontrati dei problemi nel creare quest'azienda."));
            });
        } else {
            sendUsage(player);
        }

        return true;
    }

    private void sendUsage(Player player) {
        player.sendMessage(text("§f"));
        player.sendMessage(text(" §c§lAiuto Comandi §7(/azienda)"));
        if (player.hasPermission("azienda.crea"))
            player.sendMessage(text("  §8▪ §e/azienda crea <nome> <direttore>"));
        player.sendMessage(text("§f"));
    }
}
