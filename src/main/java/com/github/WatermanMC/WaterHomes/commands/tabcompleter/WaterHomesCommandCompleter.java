package com.github.WatermanMC.WaterHomes.commands.tabcompleter;

import com.github.WatermanMC.WaterHomes.WaterHomes;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class WaterHomesCommandCompleter implements TabCompleter {

    private final WaterHomes plugin;

    public WaterHomesCommandCompleter(WaterHomes plugin) {
        this.plugin = plugin;
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

            List<String> completer = List.of("info", "reload");

            return completer.stream().filter(sub -> sub
                    .startsWith(input))
                    .collect(java.util.stream.Collectors.toList());
        }

        return new ArrayList<>();
    }
}