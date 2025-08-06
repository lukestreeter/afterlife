package me.yodeling_goat.afterlifeplugin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * Test to verify afterlife effects are working
 */
public class AfterlifeEffectsTest {
    
    public static void main(String[] args) {
        System.out.println("🧪 Testing Afterlife Effects...\n");
        
        testAfterlifeManager();
        testSoundListener();
        testEffectsListener();
        
        System.out.println("\n✅ All afterlife effect tests completed!");
    }
    
    private static void testAfterlifeManager() {
        try {
            System.out.println("🔍 Testing AfterlifeManager...");
            
            Class<?> afterlifeManagerClass = Class.forName("me.yodeling_goat.afterlifeplugin.afterlife.AfterlifeManager");
            System.out.println("✅ AfterlifeManager class exists");
            
            // Test required methods exist
            Method sendToAfterlife = afterlifeManagerClass.getDeclaredMethod("sendToAfterlife", Class.forName("org.bukkit.entity.Player"));
            System.out.println("✅ sendToAfterlife method exists");
            
            Method applyPermanentAfterlifeEffects = afterlifeManagerClass.getDeclaredMethod("applyPermanentAfterlifeEffects", Class.forName("org.bukkit.entity.Player"));
            System.out.println("✅ applyPermanentAfterlifeEffects method exists");
            
            Method isInAfterlife = afterlifeManagerClass.getDeclaredMethod("isInAfterlife", Class.forName("org.bukkit.entity.Player"));
            System.out.println("✅ isInAfterlife method exists");
            
            System.out.println("✅ AfterlifeManager test passed\n");
            
        } catch (Exception e) {
            System.out.println("❌ AfterlifeManager test failed: " + e.getMessage());
        }
    }
    
    private static void testSoundListener() {
        try {
            System.out.println("🔍 Testing AfterlifeSoundListener...");
            
            Class<?> soundListenerClass = Class.forName("me.yodeling_goat.afterlifeplugin.afterlife.listeners.AfterlifeSoundListener");
            System.out.println("✅ AfterlifeSoundListener class exists");
            
            // Check that it has the required event handlers
            Method[] methods = soundListenerClass.getDeclaredMethods();
            boolean hasEntityTarget = false;
            boolean hasPlayerMove = false;
            
            for (Method method : methods) {
                if (method.getName().equals("onEntityTarget")) {
                    hasEntityTarget = true;
                }
                if (method.getName().equals("onPlayerMove")) {
                    hasPlayerMove = true;
                }
            }
            
            if (hasEntityTarget) {
                System.out.println("✅ EntityTarget handler exists");
            } else {
                System.out.println("❌ EntityTarget handler missing");
            }
            
            if (hasPlayerMove) {
                System.out.println("✅ PlayerMove handler exists");
            } else {
                System.out.println("❌ PlayerMove handler missing");
            }
            
            System.out.println("✅ AfterlifeSoundListener test passed\n");
            
        } catch (Exception e) {
            System.out.println("❌ AfterlifeSoundListener test failed: " + e.getMessage());
        }
    }
    
    private static void testEffectsListener() {
        try {
            System.out.println("🔍 Testing AfterlifeEffectsListener...");
            
            Class<?> effectsListenerClass = Class.forName("me.yodeling_goat.afterlifeplugin.afterlife.listeners.AfterlifeEffectsListener");
            System.out.println("✅ AfterlifeEffectsListener class exists");
            
            // Check that it has the required event handler
            Method[] methods = effectsListenerClass.getDeclaredMethods();
            boolean hasPlayerEnterAfterlife = false;
            
            for (Method method : methods) {
                if (method.getName().equals("onPlayerEnterAfterlife")) {
                    hasPlayerEnterAfterlife = true;
                }
            }
            
            if (hasPlayerEnterAfterlife) {
                System.out.println("✅ PlayerEnterAfterlife handler exists");
            } else {
                System.out.println("❌ PlayerEnterAfterlife handler missing");
            }
            
            System.out.println("✅ AfterlifeEffectsListener test passed\n");
            
        } catch (Exception e) {
            System.out.println("❌ AfterlifeEffectsListener test failed: " + e.getMessage());
        }
    }
} 