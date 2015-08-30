/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.c45y.tranceteams.flag;

import com.c45y.tranceteams.TranceTeams;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.potion.PotionEffect;

/**
 *
 * @author c45y
 */
public class BlockFlag {
    private TranceTeams _plugin;
    private String _name;
    private Location _location;
    List<PotionEffect> _effects;
    private boolean _isClaimable;
    
    public BlockFlag(TranceTeams plugin, String name, Location location, List<PotionEffect> effects) {
        _plugin = plugin;
        _name = name;
        _location = location;
        _effects = effects;
        _isClaimable = false;
    }
    
    public String getName() {
        return _name;
    }
    
    public Location getLocation() {
        return _location;
    }
    
    public List<PotionEffect> getEffects() {
        return _effects;
    }
    
    public boolean isClaimable() {
        return _isClaimable;
    }
    
    public void setClaimable(boolean state) {
        _isClaimable = state;
    }
    
    public boolean isFlag(Location location) {
        return _location.equals(location);
    }
    
    public void toConfig() {
        if (_effects == null) {
            return;
        }
        
        String configlocation = "flagpersist." + _name;
        // Save location
        _plugin.getConfig().set(configlocation + ".location", _location);
        // Save effect list
        _plugin.getConfig().set(configlocation + ".effects", _effects);
        _plugin.saveConfig();
    }
    
    public static BlockFlag fromConfig(String name) {
        String configlocation = "flagpersist." + name;
        TranceTeams plugin = TranceTeams.getInstance();
        plugin.reloadConfig();
        
        return new BlockFlag(plugin, name,
            (Location) plugin.getConfig().get(configlocation + ".location"),
            (List<PotionEffect>) plugin.getConfig().get(configlocation + ".effects")
        );
    }
}
