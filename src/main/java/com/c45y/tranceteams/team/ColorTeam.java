/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.c45y.tranceteams.team;

import com.c45y.tranceteams.TranceTeams;
import com.c45y.tranceteams.util.ColorMap;
import java.util.Collection;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Wool;
import org.bukkit.scoreboard.Team;

/**
 *
 * @author c45y
 */
public class ColorTeam {
    private TranceTeams plugin;
    private Wool color;
    
    public ColorTeamConfiguration config;
    public ColorTeamScoreboard scoreboard;
    private Team scoreboardTeam;
    
    public ColorTeam(TranceTeams plugin, Wool color) {
        this.plugin = plugin;
        this.color = color;
        this.config = new ColorTeamConfiguration(plugin, this);
        
        // Set up scoreboard teams;
        this.scoreboard = new ColorTeamScoreboard(plugin, this);
    }
    
    public Wool getColor() {
        return this.color;
    }
    
    public ChatColor getChatColor() {
        return ColorMap.mapDyeChatColor.get(this.color.getColor());
    }
    
    public void broadcast(String message) {
        this.plugin.getServer().broadcastMessage(this.getChatColor() + message);
    }
    
    public String getName() {
        return this.color.getColor().name();
    }
    
    public void spawnPlayer(Player player) {
        if( this.config.containsPlayer(player)) {
            player.teleport(this.config.getSpawn(), TeleportCause.PLUGIN);
            player.getInventory().setHelmet(new ItemStack(Material.WOOL, 1, this.color.getData()));
            for (ItemStack item: this.config.respawnKit) {
                player.getInventory().addItem(item);
            }
        }
    }
    
    public int countPlayers() {
        return this.config.getPlayers().size();
    }
    
    public Collection<? extends Player> getOnlinePlayers() {
        Collection<? extends Player> onlineCopy = this.plugin.getServer().getOnlinePlayers();
        onlineCopy.retainAll(this.config.getPlayers());
        return onlineCopy;
    } 
}
