/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.c45y.C4CTF.team;

import com.c45y.C4CTF.C4CTF;
import java.util.Collection;
import java.util.HashMap;
import org.bukkit.DyeColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.material.Wool;

/**
 *
 * @author c45y
 */
public class TeamManager {
    private C4CTF plugin;
    private HashMap<Wool, ColorTeam> teams = new HashMap<Wool, ColorTeam>();
    
    public TeamManager(C4CTF plugin) {
        this.plugin = plugin;
        this.populateTeams();
    }
    
    private void populateTeams() {
        if (this.plugin.getConfig().contains("persist")) {
            for( String color : this.plugin.getConfig().getConfigurationSection("persist").getKeys(false)) {
                Wool w = new Wool();
                w.setColor(DyeColor.valueOf(color));
                ColorTeam team = new ColorTeam(this.plugin, w);
                team.config.fromConfig();
                this.teams.put(w, team);
                this.plugin.getLogger().info("Team " + color + " has been loaded!");
            }
        }
    }
    
    public void persistTeams() {
        for( ColorTeam t: this.teams.values()) {
            t.config.toConfig();
        }
    }
    
    public void addTeam(Wool wool) {
        if (!this.teams.containsKey(wool)) {
            this.teams.put(wool, new ColorTeam(this.plugin, wool));
        }
    }
    
    public ColorTeam getTeam(Wool wool) {
        if (this.teams.containsKey(wool)) {
            return this.teams.get(wool);
        }
        return null;
    }
    public ColorTeam getTeam(Player player) {
        for( ColorTeam t: this.teams.values()) {
            if (t.config.containsPlayer(player)) {
                return t;
            }
        }
        return null;
    }
    
    public Collection<ColorTeam> getTeams() {
        return this.teams.values();
    }
    
    public boolean inTeam(OfflinePlayer player) {
        for( ColorTeam t: this.teams.values()) {
            if (t.config.containsPlayer((OfflinePlayer)player)) {
                return true;
            }
        }
        return false;
    }
    
    public ColorTeam lowestTeam() {
        ColorTeam lowestTeam = null;
        int lowest = Integer.MAX_VALUE;
        
        for( ColorTeam t: this.teams.values()) {
            if (t.countPlayers() < lowest) {
                lowestTeam = t;
		lowest = t.countPlayers();
            }
        } 
        
        return lowestTeam;
    }
}
