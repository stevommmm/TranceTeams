/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.c45y.tranceteams.flag;

import com.c45y.tranceteams.TranceTeams;
import java.util.Collection;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 *
 * @author c45y
 */
public class FlagManager {
    private TranceTeams _plugin;
    private HashMap<Location, BlockFlag> _flags;
    
    public FlagManager(TranceTeams plugin) {
        _plugin = plugin;
        _flags = new HashMap<Location, BlockFlag>();
        populate();
    }
    
    public void populate() {
        if (_plugin.getConfig().contains("flagpersist")) {
            Collection<BlockFlag> flags = (Collection<BlockFlag>) _plugin.getConfig().get("flagpersist");
            for (BlockFlag flag: flags) {
                _flags.put(flag.getLocation(), flag);
            }
        }
    }
    
    public void persist() {
        _plugin.getConfig().set("flagpersist", _flags.values());
        _plugin.saveConfig();
    }
    
    public BlockFlag getFlag(Location location) {
        if (_flags.containsKey(location)) {
            return _flags.get(location);
        }
        return null;
    }
}
