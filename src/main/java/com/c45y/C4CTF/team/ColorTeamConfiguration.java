/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.c45y.C4CTF.team;

import com.c45y.C4CTF.C4CTF;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author c45y
 */
public class ColorTeamConfiguration {
    private C4CTF plugin;
    private ColorTeam team;
    
    private Location spawn;
    private Location asset;
    private List<OfflinePlayer> players = new ArrayList<OfflinePlayer>();
    
    public int respawnDelay;
    public int assetHardness;
    public List<ItemStack> respawnKit;
    
    public ColorTeamConfiguration(C4CTF plugin, ColorTeam team) {
        this.team = team;
        this.plugin = plugin;
        this.respawnDelay = plugin.getConfig().getInt("respawnDelay");
        this.assetHardness = plugin.getConfig().getInt("assetHardness");
        this.respawnKit = (List<ItemStack>)plugin.getConfig().get("respawnKit");
    }
    
    public Location getSpawn() {
        return spawn;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
        this.toConfig();
    }

    public Location getAsset() {
        return asset;
    }

    public void setAsset(Location asset) {
        this.asset = asset;
        this.toConfig();
    }
       
    public void addPlayer(OfflinePlayer player) {
        this.players.add(player);
        this.toConfig();
    }
    
    public boolean containsPlayer(OfflinePlayer player) {
        return this.players.contains(player);
    }
    
    public List<OfflinePlayer> getPlayers() {
        return this.players;
    }
    
    public void reset() {
        this.players.clear();
        this.team.scoreboard.setScore(0);
        this.toConfig();
    }
    
    public void toConfig() {
        if (this.spawn == null || this.asset == null) {
            return;
        }
        
        String configlocation = "persist." + this.team.getName();
        this.plugin.getConfig().set(configlocation + ".score", this.team.scoreboard.getScore());
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
        for( OfflinePlayer p: this.players){
            plist.add(p.getUniqueId().toString());
        }
        this.plugin.getConfig().set(configlocation + ".players", plist);
        this.plugin.saveConfig();
    }
    
    public void fromConfig() {
        String configlocation = "persist." + this.team.getName();
        
        this.plugin.reloadConfig();
        
        this.team.scoreboard.setScore(this.plugin.getConfig().getInt(configlocation + ".score"));
        
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
            OfflinePlayer player = (OfflinePlayer) this.plugin.getServer().getPlayer(UUID.fromString(p));
            if (player == null) {
                player = this.plugin.getServer().getOfflinePlayer(UUID.fromString(p));
            }
            this.players.add(player);
        }
    }
}
