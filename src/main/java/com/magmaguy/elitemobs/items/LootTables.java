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

package com.magmaguy.elitemobs.items;

import com.magmaguy.elitemobs.EntityTracker;
import com.magmaguy.elitemobs.MetadataHandler;
import com.magmaguy.elitemobs.config.ConfigValues;
import com.magmaguy.elitemobs.config.ItemsDropSettingsConfig;
import com.magmaguy.elitemobs.config.ItemsProceduralSettingsConfig;
import com.magmaguy.elitemobs.items.itemconstructor.ItemConstructor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

import static com.magmaguy.elitemobs.utils.WeightedProbablity.pickWeighedProbability;

/**
 * Created by MagmaGuy on 04/06/2017.
 */
public class LootTables implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(EntityDeathEvent event) {

        if (event.getEntity() == null) return;
        LivingEntity livingEntity = event.getEntity();

        if (!EntityTracker.isNaturalEntity(livingEntity) ||
                !livingEntity.hasMetadata(MetadataHandler.ELITE_MOB_MD)) return;

        if (livingEntity.getMetadata(MetadataHandler.ELITE_MOB_MD).get(0).asInt() < 2) return;

        Item item = generateLoot((LivingEntity) livingEntity);

        if (item == null) return;

        RareDropEffect.runEffect(item);

    }

    private static boolean proceduralItemsOn = ConfigValues.itemsProceduralSettingsConfig.getBoolean(ItemsProceduralSettingsConfig.DROP_ITEMS_ON_DEATH);
    private static boolean customItemsOn = ConfigValues.itemsDropSettingsConfig.getBoolean(ItemsDropSettingsConfig.DROP_CUSTOM_ITEMS) &&
            !CustomItemConstructor.customItemList.isEmpty();
    private static boolean staticWeightCustomItemsExist = CustomItemConstructor.staticCustomItemHashMap.size() > 0;
    private static boolean dynamicWeightCustomItemsExist = CustomItemConstructor.dynamicRankedItemStacks.size() > 0;

    public static Item generateLoot(LivingEntity livingEntity) {


        int mobTier = (int) MobTierFinder.findMobTier(livingEntity);
        /*
        Add some wiggle room to avoid making obtaining loot too linear
         */
        int itemTier = (int) setItemTier(mobTier);

         /*
        Handle the odds of an item dropping
         */
        double baseChance = ConfigValues.itemsDropSettingsConfig.getDouble(ItemsDropSettingsConfig.ELITE_ITEM_FLAT_DROP_RATE) / 100;
        double dropChanceBonus = ConfigValues.itemsDropSettingsConfig.getDouble(ItemsDropSettingsConfig.ELITE_ITEM_TIER_DROP_RATE) / 100 * itemTier;

        if (ThreadLocalRandom.current().nextDouble() > baseChance + dropChanceBonus)
            return null;

        /*
        First split: Check if the loot should be procedural or based on unique/custom items
        This makes more sense because the ratio between custom/unique items and procedural items is quite drastic
         */
        if (!proceduralItemsOn && !customItemsOn) return null;
        if (proceduralItemsOn && customItemsOn) {

            HashMap<String, Double> weightedConfigValues = new HashMap<>();
            weightedConfigValues.put(ItemsDropSettingsConfig.PROCEDURAL_ITEM_WEIGHT, ConfigValues.itemsDropSettingsConfig.getDouble(ItemsDropSettingsConfig.PROCEDURAL_ITEM_WEIGHT));
            weightedConfigValues.put(ItemsDropSettingsConfig.CUSTOM_ITEM_WEIGHT, ConfigValues.itemsDropSettingsConfig.getDouble(ItemsDropSettingsConfig.CUSTOM_ITEM_WEIGHT));

            String selectedLootSystem = pickWeighedProbability(weightedConfigValues);

            if (selectedLootSystem.equals(ItemsDropSettingsConfig.PROCEDURAL_ITEM_WEIGHT))
                return dropProcedurallyGeneratedItem(itemTier, livingEntity);


        }


        if (proceduralItemsOn && !customItemsOn)
            return dropProcedurallyGeneratedItem(itemTier, livingEntity);



        /*
        First split is done, moving on to second split
        Split between static and dynamic weight loot
         */
        if (staticWeightCustomItemsExist && dynamicWeightCustomItemsExist) {

            HashMap<String, Double> weightedConfigValues = new HashMap<>();
            weightedConfigValues.put(ItemsDropSettingsConfig.CUSTOM_DYNAMIC_ITEM_WEIGHT, ConfigValues.itemsDropSettingsConfig.getDouble(ItemsDropSettingsConfig.CUSTOM_DYNAMIC_ITEM_WEIGHT));
            weightedConfigValues.put(ItemsDropSettingsConfig.CUSTOM_STATIC_ITEM_WEIGHT, ConfigValues.itemsDropSettingsConfig.getDouble(ItemsDropSettingsConfig.CUSTOM_STATIC_ITEM_WEIGHT));

            String selectedLootSystem = pickWeighedProbability(weightedConfigValues);

            if (selectedLootSystem.equals(ItemsDropSettingsConfig.CUSTOM_STATIC_ITEM_WEIGHT)) {
                return dropCustomStaticLoot(livingEntity);

            }

        }

        /*
        Second split is done, moving on to third split
        At this point only scalability type is left, can be either static, limited or dynamic
         */
        HashMap<String, Double> weightedProbability = new HashMap<>();
        if (ScalableItemConstructor.dynamicallyScalableItems.size() > 0)
            weightedProbability.put("dynamic", 33.0);
        if (ScalableItemConstructor.limitedScalableItems.size() > 0)
            weightedProbability.put("limited", 33.0);
        if (ScalableItemConstructor.staticItems.size() > 0)
            weightedProbability.put("static", 33.0);

        String selectedLootSystem = pickWeighedProbability(weightedProbability);

        switch (selectedLootSystem) {
            case "dynamic":
                return dropDynamicallyScalingItem(livingEntity, itemTier);
            case "limited":
                return dropLimitedScalingItem(livingEntity, itemTier);
            case "static":
                return dropStaticScalingItem(livingEntity, itemTier);
        }

        return null;

    }

    public static double setItemTier(int mobTier) {

        double chanceToUpgradeTier = 10 / (double) mobTier * ConfigValues.itemsDropSettingsConfig.getDouble(ItemsDropSettingsConfig.MAXIMUM_LOOT_TIER);

        if (ThreadLocalRandom.current().nextDouble() * 100 < chanceToUpgradeTier)
            return mobTier + 1;


        double diceRoll = ThreadLocalRandom.current().nextDouble();

        /*
        10% of the time, give an item a tier below what the player is wearing
        40% of the time, give an item of the same tier as what the player is wearing
        50% of the time, give an item better than what the player is wearing
        If you're wondering why this isn't configurable, wonder instead why no one has noticed it isn't before you reading this
         */
        if (diceRoll < 0.10)
            mobTier -= 2;
        else if (diceRoll < 0.50)
            mobTier -= 1;

        if (mobTier < 0) mobTier = 0;

        return mobTier;

    }

    private static Item dropCustomStaticLoot(Entity entity) {

        double totalWeight = 0;

        for (ItemStack itemStack : CustomItemConstructor.staticCustomItemHashMap.keySet())
            totalWeight += CustomItemConstructor.staticCustomItemHashMap.get(itemStack);


        ItemStack generatedItemStack = null;
        double random = Math.random() * totalWeight;

        for (ItemStack itemStack : CustomItemConstructor.staticCustomItemHashMap.keySet()) {

            random -= CustomItemConstructor.staticCustomItemHashMap.get(itemStack);

            if (random <= 0) {

                generatedItemStack = itemStack;
                break;

            }

        }

        return entity.getWorld().dropItem(entity.getLocation(), generatedItemStack);

    }

    private static Item dropProcedurallyGeneratedItem(int tierLevel, LivingEntity livingEntity) {

        ItemStack randomLoot = ItemConstructor.constructItem(tierLevel, livingEntity);
        return livingEntity.getWorld().dropItem(livingEntity.getLocation(), randomLoot);

    }

    private static Item dropDynamicallyScalingItem(LivingEntity livingEntity, int itemTier) {

        return livingEntity.getWorld().dropItem(livingEntity.getLocation(), ScalableItemConstructor.constructDynamicItem(itemTier));

    }

    private static Item dropLimitedScalingItem(LivingEntity livingEntity, int itemTier) {

        return livingEntity.getWorld().dropItem(livingEntity.getLocation(), ScalableItemConstructor.constructLimitedItem(itemTier));

    }

    private static Item dropStaticScalingItem(LivingEntity livingEntity, int itemTier) {

        return livingEntity.getWorld().dropItem(livingEntity.getLocation(),
                ScalableItemConstructor.staticItems.get(itemTier).get(ThreadLocalRandom.current()
                        .nextInt(ScalableItemConstructor.staticItems.get(itemTier).size()) - 1));

    }

}
