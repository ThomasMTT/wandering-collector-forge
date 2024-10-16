/*
 * Copyright 2024 ThomasMTT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package life.thoms.mods.wandering_collector.helpers;

import life.thoms.mods.wandering_collector.utils.CustomLootDataUtil;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for managing inventory-related operations.
 */
public class InventoryDataHelper {

    /**
     * Sets the owner for valuable items in the player's inventory.
     *
     * @param player the player whose inventory items will be updated
     */
    public static void setOwnerToInventoryItems(Player player) {
        Inventory inventory = player.getInventory();
        List<ItemStack> itemInInventory = new ArrayList<>(inventory.items);
        itemInInventory.addAll(inventory.armor);
        itemInInventory.add(player.getOffhandItem());

        for (ItemStack stack : itemInInventory) {
            if (ItemValueFilter.filterValuableItems(stack)) {
                CustomLootDataUtil.setStackOwner(stack, player);
            }
        }
    }
}
