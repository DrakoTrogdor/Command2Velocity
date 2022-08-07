package com.drakotrogdor.command2velocity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIConfig;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.EntitySelector;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.arguments.StringArgument;

public final class Command2VelocityPaper extends JavaPlugin implements Listener { // PluginMessageListener
    public HashMap<String, Long> cooldowns = new HashMap<String, Long>();
    FileConfiguration config = this.getConfig();

    public void sendPlayerToServer(CommandSender sender, Player player, String server){
        boolean sendOther;
        String targetedPlayer;
        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        // Determine the player being sent
        if ((sender instanceof Player) && (player != null)  && (sender.getName().equalsIgnoreCase(player.getName()))) {
            // The sender and the player are the same.
            sendOther = false;
            targetedPlayer = sender.getName();
        }
        else if ((sender instanceof Player) && (player == null)) {
            // There is no player, but the sender is an instance of a player.
            sendOther = false;
            targetedPlayer = sender.getName();
        }
        else if (player == null) {
            // There is no player, and the previous conditions rule out the sender being a player, therefore exit function
            this.getLogger().warning("No player to send to server \"" + server + "\".");
            return;
        }
        else {
            // The sender is either null or not a player, and the player is not null, therefore the sender is not the player being targeted.
            sendOther = true;
            targetedPlayer = player.getName();
        }

        // Now that the target is determined, check for a cooldown and create one if needed
        int cooldownTime = 10;      // Get number of seconds for the cooldown
        boolean portalIsOnCooldown; // True is portal is on cooldown for this target, otherwise false
        // Check if the targetedPlayer is in the cooldowns Hashmap
        if (cooldowns.containsKey(targetedPlayer)) {
            long secondsLeft = ((cooldowns.get(targetedPlayer) / 1000) + cooldownTime) - (System.currentTimeMillis() / 1000);
            if (secondsLeft>0) {
                // Cooldown is still active
                portalIsOnCooldown = true;
            }
            else {
                // Cooldown has expired
                cooldowns.remove(targetedPlayer); // Remove the target from the cooldowns Hashmap
                portalIsOnCooldown = false;
            }
        }
        else {
            // Cooldown not found
            portalIsOnCooldown = false;
        }

        if (portalIsOnCooldown) {
            // Cooldown is still active therefore notify the sender
            if (sendOther) {
                if (sender instanceof Player) {
                    sender.sendMessage("Player " + targetedPlayer + " cannot be sent to the server \"" + server + "\" as they are still on cooldown.");
                }
/*
                // This can be added back if a cooldown is added for the message due to repeater command blocks spamming the logs
                else {
                    this.getLogger().warning("Player " + targetedPlayer + " cannot be sent to the server \"" + server + "\" as they are still on cooldown.");
                }
*/
            }
            else {
                sender.sendMessage("You cannot be sent to the server \"" + server + "\" as you are still on cooldown.");
            }
        }
        else {
            // There is no cooldown for sending the targeted player, therefore continue with setting up the send command.
            // Set up the player to be sent based on if they are sending themselves or are being sent by someone or something else
            if (sendOther) {
                this.getLogger().info("Sending player \"" + targetedPlayer + "\" (SELF) to server \"" + server + "\".");
                out.writeUTF("Connect");
            }
            else {
                this.getLogger().info("Sending player \"" + targetedPlayer + "\" to server \"" + server + "\".");
                out.writeUTF("ConnectOther");
                out.writeUTF(targetedPlayer);
            }

            // Set up the server to be sent to
            out.writeUTF(server);  // Server's name, as it appears in the velocity config

            // Finally, send message to BungeeCord
            /*
             Requires:
                velocity.toml
                    [advanced]
                    bungee-plugin-message-channel = true
            */
            player.sendPluginMessage(this, "BungeeCord", out.toByteArray());

            // Start Cooldown since the target was sent to a new server
            cooldowns.put(targetedPlayer, System.currentTimeMillis());
        }
    }

    @Override
    public void onLoad() {
        // Plugin loading logic
        CommandAPI.onLoad(new CommandAPIConfig().silentLogs(true)); //Required for shadowed CommandAPI

        this.getLogger().info("Creating command: command2velocity");
        List<Argument<?>> cmdServerArguments = new ArrayList<>();
        cmdServerArguments.add(new EntitySelectorArgument("player", EntitySelector.ONE_PLAYER));
        cmdServerArguments.add(new StringArgument("server"));

        new CommandAPICommand("command2velocity")
            .withAliases("c2v")
            .withPermission("command.command2velocity")
            .withSubcommand(new CommandAPICommand("server")
                .withAliases("srv","serv")
                .withArguments(new StringArgument("server"))
                .executesPlayer((sender, args) -> {
                    String server = (String) args[0];
                    sendPlayerToServer(sender, null, server);
                })
            )
            .withSubcommand(new CommandAPICommand("server")
                .withAliases("srv","serv")
                .withArguments(cmdServerArguments)
                .executes((sender, args) -> {
                    Player player = (Player) args[0];
                    String server = (String) args[1];
                    sendPlayerToServer(sender, player, server);
                })
            )
            .register();
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        CommandAPI.onEnable(this); //Required for shadowed CommandAPI

        // Load configuration
        config.addDefault("ForceSpawn",false);
        config.addDefault("SpawnX",-1);
        config.addDefault("SpawnY",-1);
        config.addDefault("SpawnZ",-1);
        config.options().copyDefaults(true);
        saveConfig();

        this.getLogger().info("Registering outgoing plugin channel with BungeeCord");
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        this.getLogger().info("Registering event listeners");
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        this.getLogger().info("Unregistering outgoing plugin channel with BungeeCord");
        getServer().getMessenger().unregisterOutgoingPluginChannel(this);
    }

    @EventHandler
    public void onPlayerJoin (PlayerJoinEvent event){
        // Handle player join to ensure they are not sent to a server right away, in case they are still in portal.
        Player player = event.getPlayer();
        if (getConfig().getBoolean("ForceSpawn")){
            double configSpawnX = getConfig().getInt("SpawnX");
            double configSpawnY = getConfig().getInt("SpawnY");
            double configSpawnZ = getConfig().getInt("SpawnZ");
            if (((configSpawnX == -1) && (configSpawnY == -1) && (configSpawnZ == -1)) || (configSpawnY < 0 ))  {
                Location worldSpawn = getServer().getWorlds().get(0).getSpawnLocation();
                configSpawnX = (int)worldSpawn.getX() + 0.5;
                configSpawnY = (int)worldSpawn.getY() + 1;
                configSpawnZ = (int)worldSpawn.getZ() + 0.5;
            }
            player.teleport(new Location(getServer().getWorld("world"),configSpawnX,configSpawnY,configSpawnZ));
        }
        else {
            this.getLogger().info("Player " + player.getName() + " has joined server, starting 10 second portal cooldown.");
            cooldowns.put(player.getName(), System.currentTimeMillis());
        }
        //getServer().getConsoleSender().sendMessage("Player, " + player.getName() + ", starting 10 second portal cooldown.");
    }
}