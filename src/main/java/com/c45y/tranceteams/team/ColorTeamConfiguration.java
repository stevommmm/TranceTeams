/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.c45y.tranceteams.team;

import com.c45y.tranceteams.TranceTeams;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Team;

/**
 *
 * @author c45y
 */
public class ColorTeamConfiguration {
    private TranceTeams plugin;
    private ColorTeam team;
    
    private Location spawn;
//    private List<OfflinePlayer> players = new ArrayList<OfflinePlayer>();
    private Team players;
    
    public List<ItemStack> respawnKit;
    
    public boolean countKills;
    
    public ColorTeamConfiguration(TranceTeams plugin, ColorTeam team) {
        this.team = team;
        this.plugin = plugin;
        this.respawnKit = (List<ItemStack>)plugin.getConfig().get("respawnKit");
        
        this.countKills = plugin.getConfig().getBoolean("countKills");
        
        this.players = plugin.scoreboard.registerNewTeam(team.getName());
        this.players.setPrefix(team.getChatColor() + "");
    }
    
    public Location getSpawn() {
        return spawn;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
        this.toConfig();
    }
       
    public void addPlayer(OfflinePlayer player) {
        this.players.addPlayer(player);
        this.toConfig();
    }
    
    public boolean containsPlayer(OfflinePlayer player) {
        for (OfflinePlayer p: this.players.getPlayers()) {
            if(p.getUniqueId().toString().equalsIgnoreCase(player.getUniqueId().toString())) {
                return true;
            }
        }
        return false;
    }
    
    public Set<OfflinePlayer> getPlayers() {
        return this.players.getPlayers();
    }
    
    public void reset() {
        Set<OfflinePlayer> pcopy = getPlayers();
        for (OfflinePlayer p: pcopy) {
            this.players.removePlayer(p);
        }
        this.team.scoreboard.setScore(0);
        this.toConfig();
        this.fromConfig();
    }
    
    public void toConfig() {
        if (this.spawn == null) {
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
        this.plugin.getConfig().set(configlocation + ".spawn.world", this.spawn.getWorld().getName());
        // Save team member player lists
        List<String> plist = new ArrayList<String>();
        for( OfflinePlayer p: this.players.getPlayers()){
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
            this.plugin.getServer().getWorld(this.plugin.getConfig().getString(configlocation + ".spawn.world")),
            this.plugin.getConfig().getDouble(configlocation + ".spawn.x"),
            this.plugin.getConfig().getDouble(configlocation + ".spawn.y"),
            this.plugin.getConfig().getDouble(configlocation + ".spawn.z"),
            (float)this.plugin.getConfig().getDouble(configlocation + ".spawn.yaw"),
            (float)this.plugin.getConfig().getDouble(configlocation + ".spawn.pitch")
        );
        
        // Load our players
        List<String> plist = this.plugin.getConfig().getStringList(configlocation + ".players");
        for( String p: plist) {
            OfflinePlayer player = (OfflinePlayer) this.plugin.getServer().getPlayer(UUID.fromString(p));
            if (player == null) {
                player = this.plugin.getServer().getOfflinePlayer(UUID.fromString(p));
            }
            this.players.addPlayer(player);
        }
    }
}
