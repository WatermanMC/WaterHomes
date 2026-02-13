package com.github.WatermanMC.WaterHomes.commands.tabcompleter;

import com.github.WatermanMC.WaterHomes.managers.HomeManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HomeCommandCompleter implements TabCompleter {

    private final HomeManager homeManager;

    public HomeCommandCompleter(HomeManager homeManager) {
        this.homeManager = homeManager;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender,
                                      @NotNull Command command,
                                      @NotNull String label,
                                      @NotNull String[] args) {

        if (!(sender instanceof Player)) return null;

        Player player = (@NotNull Player) sender;

        if (args.length == 1) {
            String input = args[0].toLowerCase();

            return homeManager.getPlayerHomes(player.getUniqueId()).keySet().stream()
                    .filter(name -> name.toLowerCase().startsWith(input))
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }
}