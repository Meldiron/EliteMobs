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

package com.magmaguy.elitemobs.config;

import com.magmaguy.elitemobs.MetadataHandler;
import org.bukkit.Bukkit;

import java.io.File;

/**
 * Created by MagmaGuy on 01/05/2017.
 */
public class ItemsCustomLootListConfig {

    public static final String CONFIG_NAME = "ItemsCustomLootList.yml";

    public void intializeConfig() {

        Bukkit.getLogger().info(Bukkit.getPluginManager().getPlugin(MetadataHandler.ELITE_MOBS).getDataFolder().getAbsolutePath() + "/data/" + CONFIG_NAME);

        File file = new File(Bukkit.getPluginManager().getPlugin(MetadataHandler.ELITE_MOBS).getDataFolder().getAbsolutePath() + "/data/" + CONFIG_NAME);

        if (!file.exists()) {

            CustomConfigConstructor customConfigConstructor = new CustomConfigConstructor(CONFIG_NAME, CONFIG_NAME);

        }

    }

}
