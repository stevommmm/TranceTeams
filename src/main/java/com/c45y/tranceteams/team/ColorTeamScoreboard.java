/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.c45y.tranceteams.team;

import com.c45y.tranceteams.TranceTeams;
import org.bukkit.scoreboard.Team;

/**
 *
 * @author c45y
 */
public class ColorTeamScoreboard {
    private TranceTeams plugin;
    private ColorTeam team;
    private int score = 0;
    
    public ColorTeamScoreboard(TranceTeams plugin, ColorTeam team) {
        this.plugin = plugin;
        this.team = team;
    }
    
    public int getScore() {
        return this.score;
    }
    
    public void setScore(int s) {
        this.score = s;
        this.plugin.scoreboardObjective.getScore(team.getChatColor() + team.getName()).setScore(s);
        this.plugin.updateScoreboard();
    }
    
    public void incrementScore() {
        int currentScore = this.score;
        this.setScore(++currentScore);
    }
        
}
