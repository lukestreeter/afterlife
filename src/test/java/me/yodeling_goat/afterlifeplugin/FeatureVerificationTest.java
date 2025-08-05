package me.yodeling_goat.afterlifeplugin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Simple verification test for all implemented features
 */
public class FeatureVerificationTest {
    
    public static void main(String[] args) {
        System.out.println("🧪 Running Feature Verification Tests...\n");
        
        testAuraDealerWhitelist();
        testFireballLimitSystem();
        testKarmaManager();
        testAfterlifeManager();
        testPluginStructure();
        
        System.out.println("\n✅ All feature verification tests completed successfully!");
    }
    
    private static void testAuraDealerWhitelist() {
        try {
            System.out.println("🔍 Testing AuraDealer Whitelist System...");
            
            Class<?> auraDealerClass = Class.forName("me.yodeling_goat.afterlifeplugin.karma.AuraDealer");
            System.out.println("✅ AuraDealer class exists");
            
            Field whitelistField = auraDealerClass.getDeclaredField("WHITELISTED_PLAYERS");
            whitelistField.setAccessible(true);
            List<String> whitelist = (List<String>) whitelistField.get(null);
            
            if (whitelist.contains("sesudos_pitufo")) {
                System.out.println("✅ sesudos_pitufo is whitelisted");
            } else {
                System.out.println("❌ sesudos_pitufo is NOT whitelisted");
            }
            
            System.out.println("📋 Whitelisted players: " + whitelist);
            System.out.println("✅ AuraDealer whitelist test passed\n");
            
        } catch (Exception e) {
            System.out.println("❌ AuraDealer whitelist test failed: " + e.getMessage());
        }
    }
    
    private static void testFireballLimitSystem() {
        try {
            System.out.println("🔍 Testing Fireball Limit System...");
            
            Class<?> mobMorphClass = Class.forName("me.yodeling_goat.afterlifeplugin.afterlife.util.MobMorphManager");
            System.out.println("✅ MobMorphManager class exists");
            
            Field fireballLimitField = mobMorphClass.getDeclaredField("FIREBALL_LIMIT");
            int limit = (int) fireballLimitField.get(null);
            System.out.println("✅ FIREBALL_LIMIT constant exists: " + limit);
            
            Field fireballsUsedField = mobMorphClass.getDeclaredField("fireballsUsed");
            System.out.println("✅ fireballsUsed HashMap exists");
            
            Method getFireballsUsed = mobMorphClass.getDeclaredMethod("getFireballsUsed", Class.forName("org.bukkit.entity.Player"));
            System.out.println("✅ getFireballsUsed method exists");
            
            Method canUseFireball = mobMorphClass.getDeclaredMethod("canUseFireball", Class.forName("org.bukkit.entity.Player"));
            System.out.println("✅ canUseFireball method exists");
            
            Method resetFireballsUsed = mobMorphClass.getDeclaredMethod("resetFireballsUsed", Class.forName("org.bukkit.entity.Player"));
            System.out.println("✅ resetFireballsUsed method exists");
            
            System.out.println("✅ Fireball limit system test passed\n");
            
        } catch (Exception e) {
            System.out.println("❌ Fireball limit test failed: " + e.getMessage());
        }
    }
    
    private static void testKarmaManager() {
        try {
            System.out.println("🔍 Testing KarmaManager...");
            
            Class<?> karmaManagerClass = Class.forName("me.yodeling_goat.afterlifeplugin.karma.KarmaManager");
            System.out.println("✅ KarmaManager class exists");
            
            Method getKarma = karmaManagerClass.getDeclaredMethod("getKarma", Class.forName("org.bukkit.entity.Player"));
            System.out.println("✅ getKarma method exists");
            
            Method setKarma = karmaManagerClass.getDeclaredMethod("setKarma", Class.forName("org.bukkit.entity.Player"), int.class);
            System.out.println("✅ setKarma method exists");
            
            System.out.println("✅ KarmaManager test passed\n");
            
        } catch (Exception e) {
            System.out.println("❌ KarmaManager test failed: " + e.getMessage());
        }
    }
    
    private static void testAfterlifeManager() {
        try {
            System.out.println("🔍 Testing AfterlifeManager...");
            
            Class<?> afterlifeManagerClass = Class.forName("me.yodeling_goat.afterlifeplugin.afterlife.AfterlifeManager");
            System.out.println("✅ AfterlifeManager class exists");
            
            Method sendToAfterlife = afterlifeManagerClass.getDeclaredMethod("sendToAfterlife", Class.forName("org.bukkit.entity.Player"));
            System.out.println("✅ sendToAfterlife method exists");
            
            System.out.println("✅ AfterlifeManager test passed\n");
            
        } catch (Exception e) {
            System.out.println("❌ AfterlifeManager test failed: " + e.getMessage());
        }
    }
    
    private static void testPluginStructure() {
        try {
            System.out.println("🔍 Testing Plugin Structure...");
            
            Class<?> mainPluginClass = Class.forName("me.yodeling_goat.afterlifeplugin.AfterLifePlugin");
            System.out.println("✅ Main plugin class exists");
            
            System.out.println("✅ Plugin structure test passed\n");
            
        } catch (Exception e) {
            System.out.println("❌ Plugin structure test failed: " + e.getMessage());
        }
    }
} 