package me.yodeling_goat.afterlifeplugin;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Random;
import org.bukkit.block.BlockFace;
import java.util.ArrayList;
import java.util.Collections;

public class GravestoneManager {
    
    private static final Map<UUID, GravestoneData> gravestones = new HashMap<>();
    private static final Random random = new Random();
    
    public static class GravestoneData {
        private final Location location;
        private final String playerName;
        private final long deathTime;
        private final String deathCause;
        private final ItemStack[] inventory;
        private final java.util.List<Location> casketArea;
        
        public GravestoneData(Location location, String playerName, String deathCause, ItemStack[] inventory, java.util.List<Location> casketArea) {
            this.location = location;
            this.playerName = playerName;
            this.deathTime = System.currentTimeMillis();
            this.deathCause = deathCause;
            this.inventory = inventory;
            this.casketArea = casketArea;
        }
        
        public Location getLocation() { return location; }
        public String getPlayerName() { return playerName; }
        public long getDeathTime() { return deathTime; }
        public String getDeathCause() { return deathCause; }
        public ItemStack[] getInventory() { return inventory; }
        public java.util.List<Location> getCasketArea() { return casketArea; }
    }
    
    public static void createGravestone(Player player, String deathCause) {
        Location deathLocation = player.getLocation();
        World world = deathLocation.getWorld();
        Location groundLocation = findGroundLocation(deathLocation);
        ItemStack[] inventory = player.getInventory().getContents().clone();
        int x = groundLocation.getBlockX();
        int y = groundLocation.getBlockY();
        int z = groundLocation.getBlockZ();

        // 1. Create the casket and get its area
        java.util.List<Location> casketArea = createCasket(world, x, y, z);

        // 2. Determine the head of the casket (center block of the first row)
        Location casketHead = getCasketHead(casketArea);

        // 3. Place the gravestone wall at the head
        createGravestoneWalls(world, casketHead);

        // 4. Add flowers to the casket area
        addFlowers(world, casketArea);

        // 5. Add epitaphs relative to the casket head
        addEpitaphs(player, casketHead, deathCause);

        // Store gravestone data
        gravestones.put(player.getUniqueId(), new GravestoneData(groundLocation, player.getName(), deathCause, inventory, casketArea));
        player.sendMessage("§7[AfterLife] §fA gravestone has been placed at your death location.");
    }
    
    private static Location findGroundLocation(Location location) {
        World world = location.getWorld();
        int x = location.getBlockX();
        int z = location.getBlockZ();
        
        // Find the highest solid block at this X,Z coordinate
        for (int y = location.getBlockY(); y > 0; y--) {
            Block block = world.getBlockAt(x, y, z);
            if (block.getType().isSolid()) {
                return new Location(world, x, y + 1, z);
            }
        }
        
        return location;
    }
    
    /**
     * Creates the casket for a gravestone, given the gravestone's base coordinates.
     * Places coarse dirt in the casket area and returns the area as a list of Locations.
     *
     * @param world The world where the grave is placed
     * @param x The x coordinate of the gravestone base
     * @param y The y coordinate of the gravestone base
     * @param z The z coordinate of the gravestone base
     * @return List of Locations representing the casket area
     *
     * Example usage:
     *   List<Location> casketArea = GravestoneManager.createCasket(world, x, y, z);
     *   GravestoneManager.addFlowers(world, casketArea);
     */
    public static java.util.List<Location> createCasket(World world, int x, int y, int z) {
        java.util.List<Location> casketArea = new ArrayList<>();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = 1; dz <= 6; dz++) {
                Location loc = new Location(world, x + dx, y - 1, z + dz);
                casketArea.add(loc);
                world.getBlockAt(loc).setType(Material.COARSE_DIRT);
            }
        }
        return casketArea;
    }
    
    public static void addFlowers(World world, java.util.List<Location> casketArea) {
        Material[] flowers = {
            Material.DANDELION, Material.POPPY, Material.BLUE_ORCHID, Material.ALLIUM,
            Material.AZURE_BLUET, Material.RED_TULIP, Material.ORANGE_TULIP,
            Material.WHITE_TULIP, Material.PINK_TULIP, Material.OXEYE_DAISY,
            Material.CORNFLOWER, Material.LILY_OF_THE_VALLEY
        };
        java.util.Collections.shuffle(casketArea, random);
        int minFlowers = casketArea.size() / 2;
        int maxFlowers = (int) (casketArea.size() * 0.75);
        int flowerCount = minFlowers + random.nextInt(maxFlowers - minFlowers + 1);
        for (int i = 0; i < flowerCount; i++) {
            Location loc = casketArea.get(i);
            Material flower = flowers[random.nextInt(flowers.length)];
            world.getBlockAt(loc.clone().add(0, 1, 0)).setType(flower);
        }
    }
    
    public static void addEpitaphs(Player player, Location casketHead, String deathCause) {
        World world = casketHead.getWorld();
        int x = casketHead.getBlockX();
        int y = casketHead.getBlockY();
        int z = casketHead.getBlockZ();
        // Place a wall sign on the north face of the gravestone
        Block signBlock = world.getBlockAt(x, y + 1, z);
        signBlock.setType(Material.OAK_WALL_SIGN);
        org.bukkit.block.data.type.WallSign wallSignData = (org.bukkit.block.data.type.WallSign) signBlock.getBlockData();
        wallSignData.setFacing(org.bukkit.block.BlockFace.SOUTH); // Sign faces away from the viewer
        signBlock.setBlockData(wallSignData, false);
        org.bukkit.block.Sign sign = (org.bukkit.block.Sign) signBlock.getState();
        sign.setLine(0, "§7Here Lies");
        sign.setLine(1, "§f" + player.getName());
        sign.setLine(2, "§7Rest in Peace");
        sign.setLine(3, "§7†");
        sign.update();
    }

    private static void placeWallSignOnWall(Location wallLocation, BlockFace signFacing, String[] signText) {
        World world = wallLocation.getWorld();
        Block wallBlock = world.getBlockAt(wallLocation);

        if (wallBlock.getType() != Material.valueOf("COBBLED_DEEPSLATE_WALL")) {
            // Optionally handle error
            return;
        }

        Location signLocation = wallLocation.clone().add(signFacing.getModX(), signFacing.getModY(), signFacing.getModZ());
        Block signBlock = world.getBlockAt(signLocation);

        if (!signBlock.getType().isAir()) {
            // Optionally handle error
            return;
        }

        signBlock.setType(Material.OAK_WALL_SIGN);
        org.bukkit.block.data.type.WallSign wallSign = (org.bukkit.block.data.type.WallSign) signBlock.getBlockData();
        wallSign.setFacing(signFacing);
        signBlock.setBlockData(wallSign);

        org.bukkit.block.Sign sign = (org.bukkit.block.Sign) signBlock.getState();
        for (int i = 0; i < Math.min(signText.length, 4); i++) {
            sign.setLine(i, signText[i]);
        }
        sign.update();
    }
    
    private static String formatDeathCause(String deathCause) {
        // Format the death cause to be more readable
        if (deathCause == null || deathCause.isEmpty()) {
            return "Unknown";
        }
        
        // Remove common prefixes and format nicely
        String formatted = deathCause.replace("EntityDamageEvent.DamageCause.", "")
                                   .replace("_", " ")
                                   .toLowerCase();
        
        // Capitalize first letter of each word
        String[] words = formatted.split(" ");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (word.length() > 0) {
                result.append(Character.toUpperCase(word.charAt(0)))
                      .append(word.substring(1))
                      .append(" ");
            }
        }
        
        return result.toString().trim();
    }
    
    public static GravestoneData getGravestoneData(UUID playerUUID) {
        return gravestones.get(playerUUID);
    }
    
    public static void removeGravestone(UUID playerUUID) {
        gravestones.remove(playerUUID);
    }
    
    public static boolean hasGravestone(UUID playerUUID) {
        return gravestones.containsKey(playerUUID);
    }

    /**
     * Returns the head of the casket (center block of the first row, i.e., lowest z, middle x)
     */
    public static Location getCasketHead(java.util.List<Location> casketArea) {
        // Find the minimum z value (front row)
        int minZ = casketArea.stream().mapToInt(loc -> loc.getBlockZ()).min().orElse(0);
        // Get all locations in the front row
        java.util.List<Location> frontRow = new java.util.ArrayList<>();
        for (Location loc : casketArea) {
            if (loc.getBlockZ() == minZ) {
                frontRow.add(loc);
            }
        }
        // Find the middle x in the front row
        frontRow.sort(java.util.Comparator.comparingInt(Location::getBlockX));
        return frontRow.get(frontRow.size() / 2);
    }

    /**
     * Places the gravestone wall at the given head location (center of the casket head)
     */
    private static void createGravestoneWalls(World world, Location head) {
        int x = head.getBlockX();
        int y = head.getBlockY() + 1; // Wall sits above the casket
        int z = head.getBlockZ() - 1; // Wall is 1 block in front of the casket head
        // Place three blocks in a row: left, center, right
        world.getBlockAt(x - 1, y, z).setType(Material.valueOf("COBBLED_DEEPSLATE_WALL"));
        world.getBlockAt(x - 1, y + 1, z).setType(Material.valueOf("COBBLED_DEEPSLATE_WALL"));
        world.getBlockAt(x, y, z).setType(Material.valueOf("COBBLED_DEEPSLATE_WALL"));
        world.getBlockAt(x, y + 1, z).setType(Material.valueOf("COBBLED_DEEPSLATE_WALL"));
        world.getBlockAt(x + 1, y, z).setType(Material.valueOf("COBBLED_DEEPSLATE_WALL"));
        world.getBlockAt(x + 1, y + 1, z).setType(Material.valueOf("COBBLED_DEEPSLATE_WALL"));
    }
} 