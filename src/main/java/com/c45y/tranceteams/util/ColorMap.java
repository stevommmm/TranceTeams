/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.c45y.tranceteams.util;

import java.util.HashMap;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

/**
 *
 * @author c45y
 */
public class ColorMap {
    public static final HashMap<DyeColor, ChatColor> mapDyeChatColor;
    
    static {
        mapDyeChatColor = new HashMap<DyeColor, ChatColor>();
        mapDyeChatColor.put(DyeColor.BLACK, ChatColor.DARK_GRAY);
        mapDyeChatColor.put(DyeColor.BLUE, ChatColor.DARK_BLUE);
        mapDyeChatColor.put(DyeColor.BROWN, ChatColor.GOLD);
        mapDyeChatColor.put(DyeColor.CYAN, ChatColor.AQUA);
        mapDyeChatColor.put(DyeColor.GRAY, ChatColor.GRAY);
        mapDyeChatColor.put(DyeColor.GREEN, ChatColor.DARK_GREEN);
        mapDyeChatColor.put(DyeColor.LIGHT_BLUE, ChatColor.BLUE);
        mapDyeChatColor.put(DyeColor.LIME, ChatColor.GREEN);
        mapDyeChatColor.put(DyeColor.MAGENTA, ChatColor.LIGHT_PURPLE);
        mapDyeChatColor.put(DyeColor.ORANGE, ChatColor.GOLD);
        mapDyeChatColor.put(DyeColor.PINK, ChatColor.LIGHT_PURPLE);
        mapDyeChatColor.put(DyeColor.PURPLE, ChatColor.DARK_PURPLE);
        mapDyeChatColor.put(DyeColor.RED, ChatColor.DARK_RED);
        mapDyeChatColor.put(DyeColor.SILVER, ChatColor.GRAY);
        mapDyeChatColor.put(DyeColor.WHITE, ChatColor.WHITE);
        mapDyeChatColor.put(DyeColor.YELLOW, ChatColor.YELLOW);
    }
}
