/*
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.magmaguy.elitemobs.mobspawning;

import com.magmaguy.elitemobs.MetadataHandler;
import com.magmaguy.elitemobs.config.*;
import com.magmaguy.elitemobs.items.customenchantments.CustomEnchantmentCache;
import com.magmaguy.elitemobs.mobscanner.ValidAggressiveMobFilter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

import static org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.CUSTOM;
import static org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.NATURAL;

/**
 * Created by MagmaGuy on 24/04/2017.
 */
public class NaturalMobMetadataAssigner implements Listener {

    private static int range = Bukkit.getServer().getViewDistance() * 16;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSpawn(CreatureSpawnEvent event) {

        if (event.isCancelled()) return;
        /*
        Deal with entities spawned within the plugin
         */
        if (event.getEntity().hasMetadata(MetadataHandler.ELITE_MOB_MD)) return;

        if (!ConfigValues.mobCombatSettingsConfig.getBoolean(MobCombatSettingsConfig.NATURAL_MOB_SPAWNING))
            return;
        if (!ConfigValues.validMobsConfig.getBoolean(ValidMobsConfig.ALLOW_AGGRESSIVE_ELITEMOBS))
            return;
        if (!ConfigValues.validWorldsConfig.getBoolean("Valid worlds." + event.getEntity().getWorld().getName()))
            return;
        if (event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.SPAWNER) && !ConfigValues.mobCombatSettingsConfig.getBoolean(MobCombatSettingsConfig.SPAWNERS_SPAWN_ELITE_MOBS))
            return;
        if (event.getEntity().getCustomName() != null && ConfigValues.defaultConfig.getBoolean(DefaultConfig.PREVENT_ELITE_MOB_CONVERSION_OF_NAMED_MOBS))
            return;
        if (!(event.getSpawnReason() == NATURAL || event.getSpawnReason() == CUSTOM && !ConfigValues.defaultConfig.getBoolean(DefaultConfig.STRICT_SPAWNING_RULES)))
            return;
        if (!ValidAggressiveMobFilter.checkValidAggressiveMob(event.getEntity()))
            return;

        LivingEntity livingEntity = event.getEntity();
//        MetadataHandler.registerMetadata(livingEntity, MetadataHandler.NATURAL_MOB_MD, true); //todo: remove this metadata
//        EntityTracker.registerNaturalEntity(livingEntity);

        int huntingGearChanceAdder = getHuntingGearBonus(livingEntity);

        Double validChance = (ConfigValues.mobCombatSettingsConfig.getDouble(MobCombatSettingsConfig.AGGRESSIVE_MOB_CONVERSION_PERCENTAGE) +
                huntingGearChanceAdder) / 100;

        if (!(ThreadLocalRandom.current().nextDouble() < validChance))
            return;

        NaturalSpawning.naturalMobProcessor(livingEntity);

    }

    private static int getHuntingGearBonus(Entity entity) {

        int huntingGearChanceAdder = 0;

        for (Player player : Bukkit.getOnlinePlayers()) {

            if (player.getWorld().equals(entity.getWorld()) &&
                    (!player.hasMetadata(MetadataHandler.VANISH_NO_PACKET) ||
                            player.hasMetadata(MetadataHandler.VANISH_NO_PACKET) && !player.getMetadata(MetadataHandler.VANISH_NO_PACKET).get(0).asBoolean())) {

                if (player.getLocation().distance(entity.getLocation()) < range) {

                    ItemStack helmet = player.getInventory().getHelmet();
                    ItemStack chestplate = player.getInventory().getChestplate();
                    ItemStack leggings = player.getInventory().getLeggings();
                    ItemStack boots = player.getInventory().getBoots();
                    ItemStack heldItem = player.getInventory().getItemInMainHand();
                    ItemStack offHandItem = player.getInventory().getItemInOffHand();

                    if (CustomEnchantmentCache.hunterEnchantment.hasCustomEnchantment(helmet))
                        huntingGearChanceAdder += CustomEnchantmentCache.hunterEnchantment.getCustomEnchantmentLevel(helmet);
                    if (CustomEnchantmentCache.hunterEnchantment.hasCustomEnchantment(chestplate))
                        huntingGearChanceAdder += CustomEnchantmentCache.hunterEnchantment.getCustomEnchantmentLevel(chestplate);
                    if (CustomEnchantmentCache.hunterEnchantment.hasCustomEnchantment(leggings))
                        huntingGearChanceAdder += CustomEnchantmentCache.hunterEnchantment.getCustomEnchantmentLevel(leggings);
                    if (CustomEnchantmentCache.hunterEnchantment.hasCustomEnchantment(boots))
                        huntingGearChanceAdder += CustomEnchantmentCache.hunterEnchantment.getCustomEnchantmentLevel(boots);
                    if (CustomEnchantmentCache.hunterEnchantment.hasCustomEnchantment(heldItem))
                        huntingGearChanceAdder += CustomEnchantmentCache.hunterEnchantment.getCustomEnchantmentLevel(heldItem);
                    if (CustomEnchantmentCache.hunterEnchantment.hasCustomEnchantment(offHandItem))
                        huntingGearChanceAdder += CustomEnchantmentCache.hunterEnchantment.getCustomEnchantmentLevel(offHandItem);

                }

            }

        }

        huntingGearChanceAdder = huntingGearChanceAdder * ConfigValues.customEnchantmentsConfig.getInt(CustomEnchantmentsConfig.HUNTER_SPAWN_BONUS);

        return huntingGearChanceAdder;

    }

}
