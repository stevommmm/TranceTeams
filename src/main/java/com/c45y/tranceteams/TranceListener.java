/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.c45y.tranceteams;

import com.c45y.tranceteams.team.ColorTeam;
import java.util.logging.Level;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 *
 * @author c45y
 */
public class TranceListener implements Listener {
    private TranceTeams _plugin;
    
    public TranceListener(TranceTeams plugin) {
        _plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {      
        _plugin.updateScoreboard();
        
        if (!_plugin.inMonitoredWorld(event.getPlayer().getWorld())) {
            _plugin.getLogger().log(Level.INFO, "World {0} not found in configured worlds", event.getPlayer().getWorld().getName());
            return;
        }
        ColorTeam team = _plugin.tryAssignToTeam(event.getPlayer());
        if (!event.getPlayer().hasPlayedBefore()) {
            event.getPlayer().teleport(team.config.getSpawn(), TeleportCause.PLUGIN);
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        final Player player = event.getPlayer();
        if (!_plugin.inMonitoredWorld(player.getWorld())) {
            _plugin.getLogger().log(Level.INFO, "World {0} not found in configured worlds", player.getWorld().getName());
            return;
        }
        final ColorTeam team = _plugin.tryAssignToTeam(player);
        if (team != null) {
            _plugin.getLogger().log(Level.INFO, "Player {0} will be spawned at their base with kit", player.getName());
            player.teleport(team.config.getSpawn(), TeleportCause.PLUGIN);
            _plugin.getServer().getScheduler().runTaskLater(_plugin, new Runnable() {
                public void run() {
                    player.teleport(team.config.getSpawn(), TeleportCause.PLUGIN);
                }
            }, 1);
        }
    }
     
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        _plugin.updateScoreboard();
        ColorTeam team = _plugin.teamManager.getTeam(event.getPlayer());
        if (team == null) {
            return;
        }

        event.setRespawnLocation(team.config.getSpawn());
        
        event.getPlayer().getInventory().setHelmet(new ItemStack(Material.WOOL, 1, team.getColor().getData()));
        for (ItemStack item: team.config.respawnKit) {
            event.getPlayer().getInventory().addItem(item);
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void teamOnPlayerDeath(PlayerDeathEvent event) {
        if (!_plugin.inMonitoredWorld(event.getEntity().getWorld())) {
            _plugin.getLogger().log(Level.INFO, "World {0} not found in configured worlds", event.getEntity().getWorld().getName());
            return;
        }
        ColorTeam team = _plugin.teamManager.getTeam(event.getEntity());
        if (team == null) {
            return;
        }
        if (event.getEntity().getKiller() instanceof Player) {
            ColorTeam iteam = _plugin.teamManager.getTeam(event.getEntity().getKiller());
            if (iteam != null) {
                iteam.scoreboard.incrementScore();
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void teamOnEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        ColorTeam team = _plugin.teamManager.getTeam((Player)event.getEntity());
        if (team == null) {
            return;
        }
         
        // Block team damage
        if (event.getDamager() instanceof Player) {
            if (team.config.containsPlayer((Player)event.getDamager())) {
                event.setCancelled(true);
                return;
            }
        }
        if (event.getDamager() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getDamager();
            if (arrow.getShooter() instanceof Player) {
                if (team.config.containsPlayer((Player)arrow.getShooter())) {
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
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        if (!_plugin.teamManager.inTeam((OfflinePlayer) event.getWhoClicked())) {
            return;
        }
        if (event.getSlot() == 39 /* Helmet slot */) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        ColorTeam team = _plugin.teamManager.getTeam(event.getPlayer());
        if (team == null) {
            return;
        }
        event.getPlayer().setDisplayName(team.getChatColor() + event.getPlayer().getName() + ChatColor.RESET);
    }
}
