package com.github.WatermanMC.WaterHomes.commands;

import com.github.WatermanMC.WaterHomes.managers.ConfigManager;
import com.github.WatermanMC.WaterHomes.WaterHomes;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class WaterHomesCommand implements CommandExecutor {

    private final WaterHomes plugin;
    private final ConfigManager configManager;
    private MiniMessage minimessage;

    public WaterHomesCommand(WaterHomes plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.minimessage = MiniMessage.miniMessage();
        plugin.getCommand("waterhomes").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String label,
                             @NotNull String[] args) {

        if (!sender.hasPermission("waterhomes.admin")) {
            String msg = configManager.getMessage("prefix") + configManager.getMessage("nopermission");
            sender.sendMessage(minimessage.deserialize(msg));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(minimessage.deserialize(configManager.getMessage("prefix") + "<red>Usage: /waterhomes <reload|info>"));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload" -> {
                boolean success = configManager.reloadConfigs();

                if (success) {
                    String msg = configManager.getMessage("prefix") + configManager.getMessage("reloaded");
                    sender.sendMessage(minimessage.deserialize(msg));
                } else {
                    sender.sendMessage(minimessage.deserialize("<red>Plugin reload failed. Please check your console for errors."));
                }
                return true;
            }
            case "info" -> {
                sender.sendMessage(minimessage.deserialize("<aqua><b>WaterHomes"));
                sender.sendMessage(minimessage.deserialize("<gray>A simple home plugin for your server!"));
                sender.sendMessage(minimessage.deserialize("<gray>Version: <white>v" + plugin.getPluginMeta().getVersion()));
                sender.sendMessage(minimessage.deserialize("<gray>Author: <white>" + plugin.getPluginMeta().getAuthors()));
                sender.sendMessage(minimessage.deserialize("<gray>Commands: <gold>/waterhomes /sethome /home /homelist /delhome"));
                return true;
            }
            default -> {
                sender.sendMessage(minimessage.deserialize(configManager.getMessage("prefix") + "<red>Usage: /waterhomes <reload|info>"));
                return true;
            }
        }
    }
}