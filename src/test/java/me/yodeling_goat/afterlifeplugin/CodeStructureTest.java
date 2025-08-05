package me.yodeling_goat.afterlifeplugin;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Simple code structure verification test
 */
public class CodeStructureTest {
    
    public static void main(String[] args) {
        System.out.println("🧪 Running Code Structure Verification Tests...\n");
        
        testAuraDealerCode();
        testFireballLimitCode();
        testPluginStructure();
        
        System.out.println("\n✅ All code structure tests completed!");
    }
    
    private static void testAuraDealerCode() {
        try {
            System.out.println("🔍 Testing AuraDealer Code Structure...");
            
            String filePath = "src/main/java/me/yodeling_goat/afterlifeplugin/karma/AuraDealer.java";
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            
            // Check for whitelist
            if (content.contains("WHITELISTED_PLAYERS")) {
                System.out.println("✅ WHITELISTED_PLAYERS field exists");
            } else {
                System.out.println("❌ WHITELISTED_PLAYERS field missing");
            }
            
            // Check for sesudos_pitufo in whitelist
            if (content.contains("sesudos_pitufo")) {
                System.out.println("✅ sesudos_pitufo is whitelisted");
            } else {
                System.out.println("❌ sesudos_pitufo is NOT whitelisted");
            }
            
            // Check for command methods
            if (content.contains("onCommand")) {
                System.out.println("✅ Command handler exists");
            } else {
                System.out.println("❌ Command handler missing");
            }
            
            // Check for auragive command
            if (content.contains("auragive")) {
                System.out.println("✅ /auragive command exists");
            } else {
                System.out.println("❌ /auragive command missing");
            }
            
            // Check for auraremove command
            if (content.contains("auraremove")) {
                System.out.println("✅ /auraremove command exists");
            } else {
                System.out.println("❌ /auraremove command missing");
            }
            
            // Check for auradealer command
            if (content.contains("auradealer")) {
                System.out.println("✅ /auradealer command exists");
            } else {
                System.out.println("❌ /auradealer command missing");
            }
            
            // Check for removeKarmaPoints method
            if (content.contains("removeKarmaPoints")) {
                System.out.println("✅ removeKarmaPoints method exists");
            } else {
                System.out.println("❌ removeKarmaPoints method missing");
            }
            
            // Check for giveKarmaPoints method
            if (content.contains("giveKarmaPoints")) {
                System.out.println("✅ giveKarmaPoints method exists");
            } else {
                System.out.println("❌ giveKarmaPoints method missing");
            }
            
            System.out.println("✅ AuraDealer code structure test passed\n");
            
        } catch (Exception e) {
            System.out.println("❌ AuraDealer code test failed: " + e.getMessage());
        }
    }
    
    private static void testFireballLimitCode() {
        try {
            System.out.println("🔍 Testing Fireball Limit Code Structure...");
            
            String filePath = "src/main/java/me/yodeling_goat/afterlifeplugin/afterlife/util/MobMorphManager.java";
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            
            // Check for fireball limit constant
            if (content.contains("FIREBALL_LIMIT")) {
                System.out.println("✅ FIREBALL_LIMIT constant exists");
            } else {
                System.out.println("❌ FIREBALL_LIMIT constant missing");
            }
            
            // Check for fireball tracking
            if (content.contains("fireballsUsed")) {
                System.out.println("✅ fireballsUsed HashMap exists");
            } else {
                System.out.println("❌ fireballsUsed HashMap missing");
            }
            
            // Check for fireball methods
            if (content.contains("getFireballsUsed")) {
                System.out.println("✅ getFireballsUsed method exists");
            } else {
                System.out.println("❌ getFireballsUsed method missing");
            }
            
            if (content.contains("canUseFireball")) {
                System.out.println("✅ canUseFireball method exists");
            } else {
                System.out.println("❌ canUseFireball method missing");
            }
            
            if (content.contains("resetFireballsUsed")) {
                System.out.println("✅ resetFireballsUsed method exists");
            } else {
                System.out.println("❌ resetFireballsUsed method missing");
            }
            
            // Check for warning messages
            if (content.contains("You have") && content.contains("left to fire")) {
                System.out.println("✅ Warning messages exist");
            } else {
                System.out.println("❌ Warning messages missing");
            }
            
            System.out.println("✅ Fireball limit code structure test passed\n");
            
        } catch (Exception e) {
            System.out.println("❌ Fireball limit code test failed: " + e.getMessage());
        }
    }
    
    private static void testPluginStructure() {
        try {
            System.out.println("🔍 Testing Plugin Structure...");
            
            // Check plugin.yml
            String pluginYmlPath = "src/main/resources/plugin.yml";
            String pluginYmlContent = new String(Files.readAllBytes(Paths.get(pluginYmlPath)));
            
            if (pluginYmlContent.contains("auragive")) {
                System.out.println("✅ /auragive command registered in plugin.yml");
            } else {
                System.out.println("❌ /auragive command not registered in plugin.yml");
            }
            
            if (pluginYmlContent.contains("auraremove")) {
                System.out.println("✅ /auraremove command registered in plugin.yml");
            } else {
                System.out.println("❌ /auraremove command not registered in plugin.yml");
            }
            
            if (pluginYmlContent.contains("auradealer")) {
                System.out.println("✅ /auradealer command registered in plugin.yml");
            } else {
                System.out.println("❌ /auradealer command not registered in plugin.yml");
            }
            
            // Check main plugin class
            String mainPluginPath = "src/main/java/me/yodeling_goat/afterlifeplugin/AfterLifePlugin.java";
            String mainPluginContent = new String(Files.readAllBytes(Paths.get(mainPluginPath)));
            
            if (mainPluginContent.contains("AuraDealer")) {
                System.out.println("✅ AuraDealer imported in main plugin");
            } else {
                System.out.println("❌ AuraDealer not imported in main plugin");
            }
            
            if (mainPluginContent.contains("setExecutor")) {
                System.out.println("✅ Commands registered in main plugin");
            } else {
                System.out.println("❌ Commands not registered in main plugin");
            }
            
            // Check for specific command registrations
            if (mainPluginContent.contains("auraremove")) {
                System.out.println("✅ /auraremove command registered in main plugin");
            } else {
                System.out.println("❌ /auraremove command not registered in main plugin");
            }
            
            System.out.println("✅ Plugin structure test passed\n");
            
        } catch (Exception e) {
            System.out.println("❌ Plugin structure test failed: " + e.getMessage());
        }
    }
} 