package com.c45y.C4CTF;

import com.c45y.C4CTF.team.ColorTeam;
import com.c45y.C4CTF.team.TeamManager;
import java.util.HashSet;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Wool;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;


public class C4CTF extends JavaPlugin implements Listener {
    
    public TeamManager teamManager;
    private Scoreboard scoreboard;
    public Objective scoreboardObjective;

    @Override
    public void onEnable() {
        this.getConfig().options().copyDefaults(true);
        this.getConfig().addDefault("respawnDelay", 60);
        this.getConfig().addDefault("assetHardness", 10);
        this.saveConfig();
        
        this.scoreboard = this.getServer().getScoreboardManager().getNewScoreboard();
        this.scoreboardObjective = this.scoreboard.registerNewObjective("sidebar", "dummy");
        this.scoreboardObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        this.scoreboardObjective.setDisplayName("Flag Captures");
                
        this.teamManager = new TeamManager(this);
        for (ColorTeam team: this.teamManager.getTeams()) {
            this.getServer().getPluginManager().registerEvents(team.playerHandler, this);
            team.respawnAssetBlock();
        }
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        this.teamManager.persistTeams();
    }
    
    public void updateScoreboard() {
        for (Player p: this.getServer().getOnlinePlayers()) {
            p.setScoreboard(this.scoreboard);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {      
        event.getPlayer().setScoreboard(this.scoreboard);
        
        if( event.getPlayer().hasPermission("ctf.op")) {
            return;
        }
        if (!this.teamManager.inTeam(event.getPlayer())) {
            ColorTeam team = this.teamManager.lowestTeam();
            if (team == null) {
                return; // We havent created any teams yet
            }
            team.config.addPlayer(event.getPlayer());
            team.broadcast(event.getPlayer().getName() + " has joined team " + team.getName());
            team.spawnPlayer(event.getPlayer());
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if( event.getWhoClicked().hasPermission("ctf.op")) {
            return;
        }
        if (event.getSlot() == 39 /* Helmet slot */) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent  event) {
        ColorTeam team = this.teamManager.getTeam(event.getPlayer());
        if (team == null) {
            return;
        }
        event.getPlayer().setDisplayName(team.getChatColor() + event.getPlayer().getName() + ChatColor.RESET);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("ctf")) {
            return true;
        }
        else if (cmd.getName().equalsIgnoreCase("ctfadmin")) {
            if (!(sender instanceof Player)) {
                return true;
            }
            Player player = (Player) sender;
            
            if (args.length == 0) {
                player.sendMessage("Missing required arguements. [create, setspawn, setasset]");
                return true;
            }
            
            if (args[0].equalsIgnoreCase("save")) {
                this.teamManager.persistTeams();
                return true;
            }
            else if (args[0].equalsIgnoreCase("broadcast")) {
                for (ColorTeam team: this.teamManager.getTeams()) {
                    this.getServer().broadcastMessage(team.getChatColor() + "Team " + team.getName() + " has " + team.countPlayers() + " players!");
                    for (OfflinePlayer p: team.config.getPlayers()) {
                        this.getServer().broadcastMessage(team.getChatColor() + " - " + p.getName() + " (" + p.getUniqueId().toString() + ")");
                    }
                }
                return true;
            }
            
            // Decide what team we are interacting with
            ItemStack itemInHand = player.getItemInHand();
            if( itemInHand == null || itemInHand.getType() != Material.WOOL) {
                player.sendMessage("You must have the teams wool block in your hand when running commands!");
                return true;
            }
            Wool wool = (Wool) itemInHand.getData();
            System.out.println(wool.toString());
            
            if (args[0].equalsIgnoreCase("create")) {
                this.teamManager.addTeam(wool);
                player.sendMessage("Team " + wool.getColor().name() + " has has been created!");
            } 
            else if (args[0].equalsIgnoreCase("setspawn")) {
                ColorTeam team = this.teamManager.getTeam(wool);
                if (team == null) {
                    player.sendMessage("Invalid team, do you need to \"/ctf create\" it first?");
                    return true;
                }
                team.config.setSpawn(player.getLocation());
                player.sendMessage("Team " + team.getName() + " has had their spawn set!");
            }
            else if (args[0].equalsIgnoreCase("setasset")) {
                ColorTeam team = this.teamManager.getTeam(wool);
                if (team == null) {
                    player.sendMessage("Invalid team, do you need to \"/ctf create\" it first?");
                    return true;
                }
                Location loc = player.getTargetBlock((HashSet<Byte>) null, 4).getLocation();
                if (loc == null) {
                    player.sendMessage("Please have a block in your crosshairs!");
                    return true;
                }
                team.config.setAsset(loc);
                player.sendMessage("Team " + team.getName() + " has had their asset set!");
            }
            return true;
        }
        return false;
    }
}
