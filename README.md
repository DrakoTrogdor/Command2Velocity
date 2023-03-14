# Command2Velocity
## Can you describe what the code does?

*Answer from ChatGPT v4 (I'm just to lazy to write it myself)*

This code is a Bukkit/Spigot plugin for Minecraft servers using the CommandAPI library. The plugin is designed to allow players to be sent to another server within a network of servers, such as a BungeeCord or Velocity proxy network. The plugin offers a command to send players to different servers and implements a cooldown mechanism to prevent players from being sent too frequently.

Here's a brief overview of the code:

1. The `Command2VelocityPaper` class extends `JavaPlugin` and implements `Listener`. It contains the main functionality of the plugin.

2. `cooldowns` HashMap is used to store player cooldowns, preventing players from being sent to another server too frequently.

3. `sendPlayerToServer()` method is responsible for sending a player (or the command sender) to another server. It checks if the player is on cooldown, sends the player to the specified server if there is no active cooldown, and starts a new cooldown after the player has been sent.

4. `onLoad()` method is called when the plugin is being loaded. It sets up the CommandAPI, creates the command `command2velocity`, and registers its subcommands for sending players to different servers.

5. `onEnable()` method is called when the plugin is enabled. It initializes the CommandAPI, loads the configuration, registers the outgoing plugin channel with BungeeCord, and registers the event listener.

6. `onDisable()` method is called when the plugin is disabled. It unregisters the outgoing plugin channel with BungeeCord.

7. `onPlayerJoin()` method is an event handler for when a player joins the server. It either forces the player to spawn at a specific location or starts a 10-second portal cooldown for the player.

In summary, this plugin allows server administrators to send players to different servers within their network using a custom command while implementing a cooldown mechanism to prevent abuse. It also provides configuration options for server administrators to manage the plugin's behavior.
