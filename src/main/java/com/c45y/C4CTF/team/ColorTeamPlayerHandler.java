/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.c45y.C4CTF.team;

import com.c45y.C4CTF.C4CTF;
import java.util.logging.Level;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 *
 * @author c45y
 */
public class ColorTeamPlayerHandler implements Listener {
    C4CTF plugin;
    ColorTeam team;
    
    public ColorTeamPlayerHandler(C4CTF plugin, ColorTeam team) {
        this.plugin = plugin;
        this.team = team;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (!plugin.inMonitoredWorld(event.getPlayer().getWorld())) {
            plugin.getLogger().log(Level.INFO, "World {0} not found in configured worlds", event.getPlayer().getWorld().getName());
            return;
        }
        if( this.team.config.containsPlayer(event.getPlayer())) {
            event.setRespawnLocation(this.team.config.getSpawn());
            event.getPlayer().getInventory().setHelmet(new ItemStack(Material.WOOL, 1, this.team.getColor().getData()));
            for (ItemStack item: this.team.config.respawnKit) {
                event.getPlayer().getInventory().addItem(item);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if( this.team.config.containsPlayer(event.getEntity())) {
            if (event.getEntity().getKiller() instanceof Player) {
                ColorTeam iteam = this.plugin.teamManager.getTeam(event.getEntity().getKiller());
                if (iteam != null) {
                    iteam.scoreboard.incrementScore();
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (!this.team.config.containsPlayer((Player)event.getEntity())) {
            return;
        }
        
        if (event.getDamager() instanceof Player) {
            if (this.team.config.containsPlayer((Player)event.getDamager())) {
                event.setCancelled(true);
                return;
            }
        }
        if (event.getDamager() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getDamager();
            if (arrow.getShooter() instanceof Player) {
                if (this.team.config.containsPlayer((Player)arrow.getShooter())) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
        
        if (event.getDamager() instanceof Potion) {
            Potion potion = (Potion) event.getDamager();
            if (!potion.isSplash()) {
                return;
            }
            // Stop player splash potions hurting other players, messes with witches
            for(PotionEffect potionEffect : potion.getEffects()) {
                if((potionEffect.getType() == PotionEffectType.HARM) ||
                   (potionEffect.getType() == PotionEffectType.POISON) ||
                   (potionEffect.getType() == PotionEffectType.WEAKNESS)) {
                    event.setCancelled(true);
                    break;
                }
            }
        }
    }    
}
