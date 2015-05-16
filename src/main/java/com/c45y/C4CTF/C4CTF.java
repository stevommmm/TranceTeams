package com.c45y.C4CTF;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Wool;
import org.bukkit.plugin.java.JavaPlugin;


public class C4CTF extends JavaPlugin implements Listener {
    
    private TeamManager teamManager;

    @Override
    public void onEnable() {
        this.getConfig().options().copyDefaults(true);
        this.getConfig().addDefault("misc", 1);
        
        this.teamManager = new TeamManager(this);
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if( event.getPlayer().hasPermission("ctf.op")) {
            return;
        }
        
        if (!event.getPlayer().hasPlayedBefore()) {
            // Assign team
        } else {
            // Place head block
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {

    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        
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
            
            // Decide what team we are interacting with
            ItemStack itemInHand = player.getItemInHand();
            if( itemInHand == null || itemInHand.getType() != Material.WOOL) {
                player.sendMessage("You must have the teams wool block in your hand when running commands!");
                return true;
            }
            Wool wool = (Wool) itemInHand.getData();
            
            if (args[0].equalsIgnoreCase("create")) {
                this.teamManager.addTeam(wool);
            } 
            else if (args[0].equalsIgnoreCase("setspawn")) {
                ColorTeam team = this.teamManager.getTeam(wool);
                if (team == null) {
                    player.sendMessage("Invalid team, do you need to \"/ctf create\" it first?");
                    return true;
                }
                team.setSpawn(player.getLocation());
            }
            else if (args[0].equalsIgnoreCase("setasset")) {
                ColorTeam team = this.teamManager.getTeam(wool);
                if (team == null) {
                    player.sendMessage("Invalid team, do you need to \"/ctf create\" it first?");
                    return true;
                }
                Location loc = player.getTargetBlock(new HashSet<Material>(), 4).getLocation();
                if (loc == null) {
                    player.sendMessage("Please have a block in your crosshairs!");
                    return true;
                }
                team.setAsset(loc);
            }
            return true;
        }
        return false;
    }
}
