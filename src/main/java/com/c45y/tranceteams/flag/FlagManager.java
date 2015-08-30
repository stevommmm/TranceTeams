/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.c45y.tranceteams.flag;

import com.c45y.tranceteams.TranceTeams;
import java.util.HashMap;
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
        if (_plugin.getConfig().contains("persist")) {
            for( String flagname : _plugin.getConfig().getConfigurationSection("flagpersist").getKeys(false)) {
                BlockFlag flag = BlockFlag.fromConfig(flagname);
                _flags.put(flag.getLocation(), flag);
            }
        }
    }
    
    public BlockFlag getFlag(Location location) {
        if (_flags.containsKey(location)) {
            return _flags.get(location);
        }
        return null;
    }
}
