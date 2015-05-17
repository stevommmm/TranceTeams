/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.c45y.C4CTF.team;

import com.c45y.C4CTF.C4CTF;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
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
    
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerRespawn(PlayerRespawnEvent  event) {
        if( this.team.config.containsPlayer(event.getPlayer())) {
            event.setRespawnLocation(this.team.config.getSpawn());
            event.getPlayer().getInventory().setHelmet(new ItemStack(Material.WOOL, 1, this.team.getColor().getData()));
            for (ItemStack item: this.team.config.respawnKit) {
                event.getPlayer().getInventory().addItem(item);
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
    
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getPlayer().hasPermission("ctf.op")) {
            return;
        }
        Block b = event.getBlock();
        if( this.team.isAssetBlock(b)) {
            if (this.team.config.containsPlayer(event.getPlayer())){
                event.setCancelled(true);
                return;
            }
            
            // Assert hardness of block, maybe should be done on interact
            if (!b.hasMetadata("hardness")) {
                b.setMetadata("hardness", new FixedMetadataValue(this.plugin, 0));
            }
            
            int hardness = b.getMetadata("hardness").get(0).asInt();
            if (hardness < this.team.config.assetHardness) {
                b.setMetadata("hardness", new FixedMetadataValue(this.plugin, ++hardness));
                event.setCancelled(true);
                
                int remaining = this.team.config.assetHardness - hardness;
                if (remaining > 0) {
                    this.team.broadcast(this.team.getName() + " asset has " + (remaining + 1) + " breaks remaining until capture!");
                } else {
                    this.team.broadcast(this.team.getName() + " asset is about to be captured!");
                }
                
                return;
            } else {
                b.setMetadata("hardness", new FixedMetadataValue(this.plugin, 0));
            }
            
            // If we get here then the block is being captured, tell everyone and schedule it to respawn on delay
            this.team.broadcast("Team " + this.team.getName() + " has had their asset captured!");
            this.team.scheduleRespawnAssetBlock(20 * this.team.config.respawnDelay);
            this.plugin.teamManager.getTeam(event.getPlayer()).scoreboard.incrementScore();
        }
    }
    
    
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getPlayer().hasPermission("ctf.op")) {
            return;
        }
        
        if( this.team.isAssetBlock(event.getBlock())) {
            event.setCancelled(true);
        }
    }
}
