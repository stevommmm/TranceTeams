/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.c45y.tranceteams.flag;

import com.c45y.tranceteams.TranceTeams;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 *
 * @author c45y
 */
public final class FlagManager {
    private TranceTeams _plugin;
    private HashMap<Location, BlockFlag> _flags;
    
    public FlagManager(TranceTeams plugin) {
        _plugin = plugin;
        _flags = new HashMap<Location, BlockFlag>();
        populate();
        
        for (BlockFlag flag: _flags.values()) {
            if (!flag.isClaimable()) {
                beinRespawnTimer(flag);
            }
        }
    }
    
    public void beinRespawnTimer(final BlockFlag flag) {
        _plugin.getLogger().log(Level.INFO, "Began reset timer with {0} ticks", flag.getClaimWaitTicks());
        _plugin.getServer().getScheduler().runTaskLater(_plugin, new Runnable() {
            @Override
            public void run() {
                World w = flag.getLocation().getWorld();
                ExperienceOrb orb = w.spawn(flag.getLocation(), ExperienceOrb.class);
                for (Entity e: orb.getNearbyEntities(40D, 40D, 40D)) {
                    if (e.getType() == EntityType.PLAYER) {
                        w.strikeLightningEffect(e.getLocation());
                        ((Player) e).setHealth(0D);
                    }
                }
                orb.remove();
                _plugin.getServer().broadcastMessage(ChatColor.GOLD + "" + ChatColor.BOLD + flag.getName() + ChatColor.RESET + "" + ChatColor.GOLD + " flag has respawned!");
            }
        }, flag.getClaimWaitTicks());
        
    }
    
    public final void populate() {
        if (_plugin.getConfig().contains("flagpersist")) {
            Collection<BlockFlag> flags = (Collection<BlockFlag>) _plugin.getConfig().get("flagpersist");
            for (BlockFlag flag: flags) {
                _flags.put(flag.getLocation(), flag);
            }
        }
    }
    
    public void persist() {
        _plugin.getConfig().set("flagpersist", _flags.values().toArray());
        _plugin.saveConfig();
    }
    
    public void addFlag(BlockFlag flag) {
        _flags.put(flag.getLocation(), flag);
    }
    
    public BlockFlag getFlag(Location location) {
        if (_flags.containsKey(location)) {
            return _flags.get(location);
        }
        return null;
    }
    
    public Collection<BlockFlag> getFlags() {
        return _flags.values();
    }
    
    public void removeFlag(BlockFlag flag) {
        _flags.remove(flag.getLocation());
    }
}
