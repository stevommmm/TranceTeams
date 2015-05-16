/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.c45y.C4CTF;

import java.util.HashMap;
import org.bukkit.material.Wool;

/**
 *
 * @author c45y
 */
public class TeamManager {
    private C4CTF plugin;
    private HashMap<Wool, ColorTeam> teams;
    
    public TeamManager(C4CTF plugin) {
        this.plugin = plugin;
        this.populateTeams();
    }
    
    private void populateTeams() {
        System.out.println(this.plugin.getConfig().getConfigurationSection("persist").getKeys(false).toString());
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
    
    public ColorTeam lowestTeam() {
        ColorTeam lowestTeam = null;
        int lowest = Integer.MAX_VALUE;
        
        for( ColorTeam t: this.teams.values()) {
            if (t.countPlayers() < lowest) {
                lowestTeam = t;
            }
        } 
        
        return lowestTeam;
    }
}
