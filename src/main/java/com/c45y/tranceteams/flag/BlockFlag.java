/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.c45y.tranceteams.flag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Utility;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.potion.PotionEffect;

/**
 *
 * @author c45y
 */
public class BlockFlag implements ConfigurationSerializable {
    private String _name;
    private Location _location;
    List<PotionEffect> _effects;
    private long _lastClaimed;
    
    public BlockFlag(String name, Location location) {
        this(name, location, new ArrayList<PotionEffect>());
    }
    
    public BlockFlag(String name, Location location, List<PotionEffect> effects) {
        this(name, location, effects, System.currentTimeMillis());
    }
    
    public BlockFlag(String name, Location location, Collection<PotionEffect> effects) {
        this(name, location, new ArrayList(effects), System.currentTimeMillis());
    }
    
    public BlockFlag(String name, Location location, List<PotionEffect> effects, long lastClaimed) {
        _name = name;
        _location = location;
        _effects = effects;
        _lastClaimed = lastClaimed;
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
        return System.currentTimeMillis() > _lastClaimed;
    }
    
    public long getClaimWait() {
        long delay = System.currentTimeMillis() - _lastClaimed;
        return delay > 0 ? delay : 0L;
    }
    
    public void toggleClaimable() {
        _lastClaimed = System.currentTimeMillis();
    }
    
    public boolean isFlag(Location location) {
        return _location.equals(location);
    }

    @Utility
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("name", _name);
        data.put("world", _location.getWorld().getName());
        data.put("x", _location.getX());
        data.put("y", _location.getY());
        data.put("z", _location.getZ());
        data.put("effects", _effects);
        data.put("lastclaimed", _lastClaimed);
        return data;
    }
    
    public static BlockFlag deserialize(Map<String, Object> args) {
        World world = Bukkit.getWorld((String) args.get("world"));
        if (world == null) {
            throw new IllegalArgumentException("unknown world");
        }
        Location location = new Location(world, (Double) args.get("x"), (Double) args.get("y"), (Double) args.get("z"));
        //String name, Location location, List<PotionEffect> effects, boolean claimable
        return new BlockFlag((String) args.get("name"), location, (List<PotionEffect>) args.get("effects"), (Long) args.get("lastclaimed"));
    }
}
