/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.c45y.C4CTF;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Wool;

/**
 *
 * @author c45y
 */
public class ColorTeam {
    private C4CTF plugin;
    private Wool color;
    private Location spawn;
    private Location asset;
    private List<Player> players;
    
    public ColorTeam(C4CTF plugin, Wool color) {
        this.plugin = plugin;
        this.color = color;
    }
    
    // Locations
    
    public void setAsset(Location location) {
        this.asset = location;
    }
    
    public Location getAsset() {
        return this.asset;
    }
    
    public boolean isAssetBlock(Block block) {
        return block.getLocation().getX() == this.asset.getX() &&
               block.getLocation().getY() == this.asset.getY() &&
               block.getLocation().getZ() == this.asset.getZ();
    }
    
    public void setSpawn(Location location) {
        this.spawn = location;
    }
    
    public Location getSpawn() {
        return this.spawn;
    }
    
    public void respawnPlayer(Player player) {
        if (this.players.contains(player)) {
            player.teleport(this.spawn);
            player.getInventory().setHelmet(new ItemStack(Material.WOOL, 1, this.color.getData()));
        }
    }
    
    public int countPlayers() {
        return this.players.size();
    }
    
    // Team persistence
    
    public void toConfig() {
        String configlocation = "persist." + this.color.getColor().name();
        
        // Save spawn location
        this.plugin.getConfig().set(configlocation + ".spawn.x", this.spawn.getX());
        this.plugin.getConfig().set(configlocation + ".spawn.y", this.spawn.getY());
        this.plugin.getConfig().set(configlocation + ".spawn.z", this.spawn.getZ());
        this.plugin.getConfig().set(configlocation + ".spawn.yaw", this.spawn.getYaw());
        this.plugin.getConfig().set(configlocation + ".spawn.pitch", this.spawn.getPitch());
        
        // Save asset location
        this.plugin.getConfig().set(configlocation + ".asset.x", this.asset.getX());
        this.plugin.getConfig().set(configlocation + ".asset.y", this.asset.getY());
        this.plugin.getConfig().set(configlocation + ".asset.z", this.asset.getZ());
        
        // Save team member player lists
        List<String> plist = new ArrayList<String>();
        for( Player p: this.players){
            plist.add(p.getUniqueId().toString());
        }
        this.plugin.getConfig().set(configlocation + ".players", plist);
        this.plugin.saveConfig();
    }
    
    public void fromConfig() {
        String configlocation = "persist." + this.color.getColor().name();
        
        this.plugin.reloadConfig();
        
        // Load spawn location
        this.spawn = new Location(
            this.plugin.getServer().getWorlds().get(0),
            this.plugin.getConfig().getDouble(configlocation + ".spawn.x"),
            this.plugin.getConfig().getDouble(configlocation + ".spawn.y"),
            this.plugin.getConfig().getDouble(configlocation + ".spawn.z"),
            (float)this.plugin.getConfig().getDouble(configlocation + ".spawn.yaw"),
            (float)this.plugin.getConfig().getDouble(configlocation + ".spawn.pitch")
        );
        
        // Load asset location
        this.asset = new Location(
            this.plugin.getServer().getWorlds().get(0),
            this.plugin.getConfig().getDouble(configlocation + ".asset.x"),
            this.plugin.getConfig().getDouble(configlocation + ".asset.y"),
            this.plugin.getConfig().getDouble(configlocation + ".asset.z")
        );
        
        // Load our players
        List<String> plist = this.plugin.getConfig().getStringList(configlocation + ".players");
        for( String p: plist) {
            this.players.add(this.plugin.getServer().getPlayer(UUID.fromString(p)));
        }
    }
}
