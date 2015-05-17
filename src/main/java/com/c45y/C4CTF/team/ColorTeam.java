/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.c45y.C4CTF.team;

import com.c45y.C4CTF.C4CTF;
import com.c45y.C4CTF.util.ColorMap;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Wool;

/**
 *
 * @author c45y
 */
public class ColorTeam {
    private C4CTF plugin;
    private Wool color;
    
    public ColorTeamPlayerHandler playerHandler;
    public ColorTeamConfiguration config;
    public ColorTeamScoreboard scoreboard;
    
    public ColorTeam(C4CTF plugin, Wool color) {
        this.plugin = plugin;
        this.color = color;
        this.playerHandler = new ColorTeamPlayerHandler(plugin, this);
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
    
    // Locations   
    public boolean isAssetBlock(Block block) {
        return block.getLocation().getX() == this.config.getAsset().getX() &&
               block.getLocation().getY() == this.config.getAsset().getY() &&
               block.getLocation().getZ() == this.config.getAsset().getZ();
    }
    public void respawnAssetBlock() {
        respawnAssetBlock(this.plugin.getServer().getWorlds().get(0));
    }
    public void respawnAssetBlock(World world) {
        Block block = world.getBlockAt(this.config.getAsset());
        block.setType(this.color.getItemType());
        block.setData(this.color.getData());
    }
    public void scheduleRespawnAssetBlock(long delay) {
        this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
            @Override
            public void run() {
                respawnAssetBlock();
                broadcast("Team " + getName() + " asset has respawned!");
            }
        }, delay);
    }
    
    public void spawnPlayer(Player player) {
        if( this.config.containsPlayer(player)) {
            player.teleport(this.config.getSpawn());
            player.getInventory().setHelmet(new ItemStack(Material.WOOL, 1, this.color.getData()));
        }
    }
    
    public int countPlayers() {
        return this.config.getPlayers().size();
    }
    
    // Team persistence
    
    
}
