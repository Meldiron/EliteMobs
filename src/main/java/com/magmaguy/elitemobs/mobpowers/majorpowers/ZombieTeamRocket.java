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

package com.magmaguy.elitemobs.mobpowers.majorpowers;

import com.magmaguy.elitemobs.MetadataHandler;
import com.magmaguy.elitemobs.config.ConfigValues;
import com.magmaguy.elitemobs.mobconstructor.AggressiveEliteMobConstructor;
import com.magmaguy.elitemobs.powerstances.MajorPowerPowerStance;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.Random;

import static com.magmaguy.elitemobs.ChatColorConverter.convert;

/**
 * Created by MagmaGuy on 07/05/2017.
 */
public class ZombieTeamRocket extends MajorPowers implements Listener {

    private static final ItemStack TEAM_ROCKET_CHESTPIECE = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
    private static final ItemStack TEAM_ROCKET_LEGGINGS = new ItemStack(Material.LEATHER_LEGGINGS, 1);
    private static final ItemStack TEAM_ROCKET_BOOTS = new ItemStack(Material.LEATHER_BOOTS, 1);
    private static final ItemStack TEAM_ROCKET_HELMET = new ItemStack(Material.LEATHER_HELMET, 1);
    private static Random random = new Random();
    int processID;
    Plugin plugin = Bukkit.getPluginManager().getPlugin(MetadataHandler.ELITE_MOBS);
    String powerMetadata = MetadataHandler.ZOMBIE_TEAM_ROCKET_H;
    Configuration configuration = ConfigValues.translationConfig;

    @Override
    public void applyPowers(Entity entity) {

        MetadataHandler.registerMetadata(entity, powerMetadata, true);
        MajorPowerPowerStance majorPowerStanceMath = new MajorPowerPowerStance();
        majorPowerStanceMath.itemEffect(entity);

    }

    @Override
    public boolean existingPowers(Entity entity) {

        return entity.hasMetadata(powerMetadata);

    }

    @EventHandler
    public void onHit(EntityDamageEvent event) {

        if (event.isCancelled()) return;

        if (event.getEntity().hasMetadata(powerMetadata) && event.getEntity() instanceof Zombie &&
                !event.getEntity().hasMetadata(MetadataHandler.TEAM_ROCKET_ACTIVATED) && random.nextDouble() < 0.5) {

            Entity entity = event.getEntity();

            MetadataHandler.registerMetadata(entity, MetadataHandler.TEAM_ROCKET_ACTIVATED, true);

            int assistMobLevel = (int) Math.floor(entity.getMetadata(MetadataHandler.ELITE_MOB_MD).get(0).asInt() / 4);

            if (assistMobLevel < 1) {

                assistMobLevel = 1;

            }


            LeatherArmorMeta teamRocketChestpieceMeta = (LeatherArmorMeta) TEAM_ROCKET_CHESTPIECE.getItemMeta();
            teamRocketChestpieceMeta.setColor(Color.WHITE);
            TEAM_ROCKET_CHESTPIECE.setItemMeta(teamRocketChestpieceMeta);


            LeatherArmorMeta teamRocketLeggingsMeta = (LeatherArmorMeta) TEAM_ROCKET_CHESTPIECE.getItemMeta();
            teamRocketLeggingsMeta.setColor(Color.WHITE);
            TEAM_ROCKET_LEGGINGS.setItemMeta(teamRocketLeggingsMeta);

            LeatherArmorMeta teamRocketBootsMeta = (LeatherArmorMeta) TEAM_ROCKET_BOOTS.getItemMeta();
            teamRocketBootsMeta.setColor(Color.BLACK);
            TEAM_ROCKET_BOOTS.setItemMeta(teamRocketBootsMeta);

            ItemStack jesseHelmet = TEAM_ROCKET_HELMET.clone();
            LeatherArmorMeta jesseHelmetMeta = (LeatherArmorMeta) jesseHelmet.getItemMeta();
            jesseHelmetMeta.setColor(Color.PURPLE);
            jesseHelmet.setItemMeta(jesseHelmetMeta);

            ItemStack jamesHelmet = TEAM_ROCKET_HELMET.clone();
            LeatherArmorMeta jamesHelmetMeta = (LeatherArmorMeta) jamesHelmet.getItemMeta();
            jamesHelmetMeta.setColor(Color.BLUE);
            jamesHelmet.setItemMeta(jamesHelmetMeta);

            Zombie jesse = (Zombie) entity.getWorld().spawnEntity(entity.getLocation(), EntityType.ZOMBIE);
            MetadataHandler.registerMetadata(jesse, MetadataHandler.ELITE_MOB_MD, assistMobLevel);
            MetadataHandler.registerMetadata(jesse, MetadataHandler.CUSTOM_NAME, true);
            MetadataHandler.registerMetadata(jesse, MetadataHandler.CUSTOM_ARMOR, true);
            MetadataHandler.registerMetadata(jesse, MetadataHandler.CUSTOM_STACK, true);
            MetadataHandler.registerMetadata(jesse, MetadataHandler.CUSTOM_POWERS_MD, true);
            MetadataHandler.registerMetadata(jesse, MetadataHandler.TEAM_ROCKET_MEMBER, true);
            jesse.setCustomName("Jesse");
            jesse.getEquipment().setHelmet(jesseHelmet);
            jesse.getEquipment().setChestplate(TEAM_ROCKET_CHESTPIECE);
            jesse.getEquipment().setLeggings(TEAM_ROCKET_LEGGINGS);
            jesse.getEquipment().setBoots(TEAM_ROCKET_BOOTS);
            AggressiveEliteMobConstructor.constructAggressiveEliteMob(jesse);

            Zombie james = (Zombie) entity.getWorld().spawnEntity(entity.getLocation(), EntityType.ZOMBIE);
            MetadataHandler.registerMetadata(james, MetadataHandler.ELITE_MOB_MD, assistMobLevel);
            MetadataHandler.registerMetadata(james, MetadataHandler.CUSTOM_NAME, true);
            MetadataHandler.registerMetadata(james, MetadataHandler.CUSTOM_ARMOR, true);
            MetadataHandler.registerMetadata(james, MetadataHandler.CUSTOM_STACK, true);
            MetadataHandler.registerMetadata(james, MetadataHandler.CUSTOM_POWERS_MD, true);
            MetadataHandler.registerMetadata(james, MetadataHandler.TEAM_ROCKET_MEMBER, true);
            james.setCustomName("James");
            james.getEquipment().setHelmet(jamesHelmet);
            james.getEquipment().setChestplate(TEAM_ROCKET_CHESTPIECE);
            james.getEquipment().setLeggings(TEAM_ROCKET_LEGGINGS);
            james.getEquipment().setBoots(TEAM_ROCKET_BOOTS);
            AggressiveEliteMobConstructor.constructAggressiveEliteMob(james);

            Ocelot meowth = (Ocelot) entity.getWorld().spawnEntity(entity.getLocation(), EntityType.OCELOT);
            MetadataHandler.registerMetadata(meowth, MetadataHandler.TEAM_ROCKET_MEMBER, true);
            meowth.setCustomName("Meowth");

            ((Zombie) entity).getEquipment().setChestplate(TEAM_ROCKET_CHESTPIECE);
            ((Zombie) entity).getEquipment().setLeggings(TEAM_ROCKET_LEGGINGS);
            ((Zombie) entity).getEquipment().setBoots(TEAM_ROCKET_BOOTS);
            MetadataHandler.registerMetadata(entity, MetadataHandler.CUSTOM_ARMOR, true);

            Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

                int counter = 1;

                @Override
                public void run() {

                    switch (counter) {

                        case 1:
                            jesse.setCustomNameVisible(true);
                            jesse.setCustomName(convert(configuration.getStringList("ZombieTeamRocket.Intro").get(0)));
                            break;
                        case 2:
                            jesse.setCustomNameVisible(false);
                            james.setCustomName(convert(configuration.getStringList("ZombieTeamRocket.Intro").get(1)));
                            james.setCustomNameVisible(true);
                            break;
                        case 3:
                            james.setCustomNameVisible(false);
                            jesse.setCustomName(convert(configuration.getStringList("ZombieTeamRocket.Intro").get(2)));
                            jesse.setCustomNameVisible(true);
                            break;
                        case 4:
                            jesse.setCustomName(convert(configuration.getStringList("ZombieTeamRocket.Intro").get(3)));
                            break;
                        case 5:
                            jesse.setCustomNameVisible(false);
                            james.setCustomName(convert(configuration.getStringList("ZombieTeamRocket.Intro").get(4)));
                            james.setCustomNameVisible(true);
                            break;
                        case 6:
                            james.setCustomName(convert(configuration.getStringList("ZombieTeamRocket.Intro").get(5)));
                            break;
                        case 7:
                            james.setCustomNameVisible(false);
                            jesse.setCustomName(convert(configuration.getStringList("ZombieTeamRocket.Intro").get(6)));
                            jesse.setCustomNameVisible(true);
                            break;
                        case 8:
                            jesse.setCustomName(convert(configuration.getStringList("ZombieTeamRocket.Intro").get(7)));
                            break;
                        case 9:
                            jesse.setCustomNameVisible(false);
                            james.setCustomName(convert(configuration.getStringList("ZombieTeamRocket.Intro").get(8)));
                            james.setCustomNameVisible(true);
                            break;
                        case 10:
                            james.setCustomName(convert(configuration.getStringList("ZombieTeamRocket.Intro").get(9)));
                            break;
                        case 11:
                            james.setCustomNameVisible(false);
                            jesse.setCustomName(convert(configuration.getStringList("ZombieTeamRocket.Intro").get(10)));
                            jesse.setCustomNameVisible(true);
                            break;
                        case 12:
                            jesse.setCustomNameVisible(false);
                            james.setCustomName(convert(configuration.getStringList("ZombieTeamRocket.Intro").get(11)));
                            james.setCustomNameVisible(true);
                            break;
                        case 13:
                            james.setCustomNameVisible(false);
                            jesse.setCustomName(convert(configuration.getStringList("ZombieTeamRocket.Intro").get(12)));
                            jesse.setCustomNameVisible(true);
                            break;
                        case 14:
                            jesse.setCustomName(convert(configuration.getStringList("ZombieTeamRocket.Intro").get(13)));
                            break;
                        case 15:
                            jesse.setCustomNameVisible(false);
                            james.setCustomName(convert(configuration.getStringList("ZombieTeamRocket.Intro").get(14)));
                            james.setCustomNameVisible(true);
                            break;
                        case 16:
                            james.setCustomName(convert(configuration.getStringList("ZombieTeamRocket.Intro").get(15)));
                            break;
                        case 17:
                            james.setCustomNameVisible(false);
                            meowth.setCustomName(convert(configuration.getStringList("ZombieTeamRocket.Intro").get(16)));
                            meowth.setCustomNameVisible(true);
                            break;
                        case 18:
                            jesse.setCustomName(convert(configuration.getString("ZombieTeamRocket.Jesse name")));
                            jesse.setCustomNameVisible(true);
                            james.setCustomName(convert(configuration.getString("ZombieTeamRocket.James name")));
                            james.setCustomNameVisible(true);
                            meowth.setCustomName(convert(configuration.getString("ZombieTeamRocket.Meowth name")));
                        case 30:
                            meowth.remove();
                            Bukkit.getScheduler().cancelTask(processID);
                            break;

                    }

                    counter++;

                }

            }, 1, 15 * 2);

        }

    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {

        if (event.getEntity().hasMetadata(MetadataHandler.TEAM_ROCKET_MEMBER)) {

            Entity entity = event.getEntity();

            entity.setCustomName(convert(configuration.getString("ZombieTeamRocket.DeathMessage")));

            entity.setVelocity(new Vector(0, 3, 0));

        }

    }

}