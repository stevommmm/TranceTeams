package com.c45y.tranceteams;

import com.c45y.tranceteams.flag.BlockFlag;
import com.c45y.tranceteams.flag.FlagManager;
import com.c45y.tranceteams.team.ColorTeam;
import com.c45y.tranceteams.team.TeamManager;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Wool;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class TranceTeams extends JavaPlugin {
    private static TranceTeams _this;
    public TeamManager teamManager;
    public FlagManager flagManager;
    public Scoreboard scoreboard;
    public Objective scoreboardObjective;
   

    @Override
    public void onEnable() {
        _this = this;
        
        ConfigurationSerialization.registerClass(BlockFlag.class, "BlockFlag");
        
        
        this.getConfig().options().copyDefaults(true);
        this.getConfig().addDefault("worldJoinAssign", new String[] {"world"});
        this.getConfig().addDefault("countKills", true);
        List<ItemStack> respawnKit = new ArrayList<ItemStack>();
        
        // Kit sword
        ItemStack sword = new ItemStack(Material.IRON_SWORD, 1);
        sword.addEnchantment(Enchantment.KNOCKBACK, 2);
        sword.addEnchantment(Enchantment.DURABILITY, 3);
        respawnKit.add(sword);
        
        // Kit bow
        ItemStack bow = new ItemStack(Material.BOW, 1);
        bow.addEnchantment(Enchantment.ARROW_INFINITE, 1);
        bow.addEnchantment(Enchantment.DURABILITY, 3);
        respawnKit.add(bow);
        
        // Single arrow for INF bow
        respawnKit.add(new ItemStack(Material.ARROW, 1));
        this.getConfig().addDefault("respawnKit", respawnKit);
        
        this.saveConfig();
        this.reloadConfig();
        
        this.scoreboard = this.getServer().getScoreboardManager().getNewScoreboard();
        this.scoreboardObjective = this.scoreboard.registerNewObjective("sidebar", "dummy");
        this.scoreboardObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        this.scoreboardObjective.setDisplayName("Team Score");
                
        this.teamManager = new TeamManager(this);
        this.flagManager = new FlagManager(this);

        this.getServer().getPluginManager().registerEvents(new TranceListener(this), this);
        
    }

    @Override
    public void onDisable() {
        this.teamManager.persistTeams();
        this.flagManager.persist();
    }
    
    public static TranceTeams getInstance() {
        return _this;
    }
    
    public void updateScoreboard() {
        for (Player p: this.getServer().getOnlinePlayers()) {
            p.setScoreboard(this.scoreboard);
        }
    }
    
    public boolean inMonitoredWorld(World world) {
        return getConfig().getStringList("worldJoinAssign").contains(world.getName());
    }
    
    public ColorTeam tryAssignToTeam(Player player) {
        if( player.hasPermission("ctf.op")) {
            getLogger().log(Level.INFO, "Player {0} has permission ctf.op", player.getName());
            return null;
        }
        if (!this.teamManager.inTeam(player)) {
            ColorTeam team = this.teamManager.lowestTeam();
            if (team == null) {
                getLogger().log(Level.INFO, "No teams found when adding player {0}", player.getName());
                return null; // We probably havent created any teams yet
            }
            team.config.addPlayer(player);
            team.broadcast(player.getName() + " has joined team " + team.getName());
            team.spawnPlayer(player);
            return team;
        } else {
            return this.teamManager.getTeam(player);
        } 
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("team")) {
            return true;
        }
        else if (cmd.getName().equalsIgnoreCase("flagadmin") && sender.hasPermission("tranceteams.op")) {
            if (!(sender instanceof Player)) {
                return true;
            }
            Player player = (Player) sender;
            if (args.length == 0) {
                player.sendMessage("Missing required arguements. [create, remove, list, save]");
                return true;
            }
            if (args[0].equalsIgnoreCase("create")) {
                if (args.length != 2) {
                    player.sendMessage("Missing args, /create <name>");
                    return true;
                }
                Block target = player.getTargetBlock((HashSet<Material>) null, 4);
                if (target == null) {
                    player.sendMessage("You need to be looking at a block");
                    return true;
                }
                target.setType(Material.GLOWSTONE);
                Location location = target.getLocation();
                this.flagManager.addFlag(new BlockFlag(args[1], location, player.getActivePotionEffects()));
                player.sendMessage(String.format("Flag created at %f, %f, %f with buffs:", new Object[] {location.getX(), location.getY(), location.getZ()}));
                for (PotionEffect effect: player.getActivePotionEffects()) {
                    player.sendMessage(" - " + effect.getType().getName() + ", duration:" + effect.getDuration());
                }
                return true;
            } else if (args[0].equalsIgnoreCase("remove")) {
                
            } else if (args[0].equalsIgnoreCase("list")) {
                for (BlockFlag flag: this.flagManager.getFlags()) {
                    player.sendMessage(String.format("Flag %s at %f, %f, %f with buffs:", new Object[] {flag.getName(), flag.getLocation().getX(), flag.getLocation().getY(), flag.getLocation().getZ()}));
                    for (PotionEffect effect: flag.getEffects()) {
                        player.sendMessage(" - " + effect.getType().getName() + ", duration:" + effect.getDuration());
                    }
                }
                return true;
            } else if (args[0].equalsIgnoreCase("save")) {
                this.flagManager.persist();
                return true;
            } else {
                player.sendMessage("Unknown command");
            }
            
            
            
        } else if (cmd.getName().equalsIgnoreCase("teamadmin") && sender.hasPermission("tranceteams.op")) {
            if (!(sender instanceof Player)) {
                return true;
            }
            Player player = (Player) sender;
            
            if (args.length == 0) {
                player.sendMessage("Missing required arguements. [create, setspawn, reset, broadcast, save]");
                return true;
            }
            
            if (args[0].equalsIgnoreCase("save")) {
                this.teamManager.persistTeams();
                return true;
            }
            else if (args[0].equalsIgnoreCase("broadcast")) {
                for (ColorTeam team: this.teamManager.getTeams()) {
                    this.getServer().broadcastMessage(team.getChatColor() + "Team " + team.getName() + " has " + team.countPlayers() + " players!");
                    for (OfflinePlayer p: team.config.getPlayers()) {
                        this.getServer().broadcastMessage(team.getChatColor() + " - " + p.getName() + " (" + p.getUniqueId().toString() + ")");
                    }
                }
                return true;
            }
            else if (args[0].equalsIgnoreCase("reset")) {
                for (ColorTeam team: this.teamManager.getTeams()) {
                    team.config.reset();
                }
                return true;
            }
            
            // Decide what team we are interacting with
            ItemStack itemInHand = player.getItemInHand();
            if( itemInHand == null || itemInHand.getType() != Material.WOOL) {
                player.sendMessage("You must have the teams wool block in your hand when running commands!");
                return true;
            }
            Wool wool = (Wool) itemInHand.getData();
            
            if (args[0].equalsIgnoreCase("create")) {
                this.teamManager.addTeam(wool);
                player.sendMessage("Team " + wool.getColor().name() + " has has been created!");
            } 
            else if (args[0].equalsIgnoreCase("setspawn")) {
                ColorTeam team = this.teamManager.getTeam(wool);
                if (team == null) {
                    player.sendMessage("Invalid team, do you need to \"/teamadmin create\" it first?");
                    return true;
                }
                team.config.setSpawn(player.getLocation());
                player.sendMessage("Team " + team.getName() + " has had their spawn set!");
            }
            return true;
        }
        return false;
    }
}
