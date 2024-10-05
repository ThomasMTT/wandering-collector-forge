/*
 * Copyright 2024 ThomasMTT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * you may obtain a copy of the License at
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

import net.minecraft.item.ItemStack;

import static life.thoms.mods.wandering_collector.utils.ItemFilterUtil.*;

/**
 * A helper class for filtering valuable items.
 */
public class ItemValueFilter {

    /**
     * Filters items to determine if they are considered valuable based on various criteria.
     *
     * @param stack The ItemStack to be evaluated.
     * @return true if the item is considered valuable; false otherwise.
     */
    public static boolean filterValuableItems(ItemStack stack) {
        return !isExcludedItem(stack) && (isEnchanted(stack) || isEnchantedBook(stack) || isWeapon(stack)
                || isArmor(stack) || isTool(stack) || isEffectPotion(stack) || isValuableBlock(stack) ||
                isValuableIngot(stack) || getConfiguredPrice(stack) != -1);
    }

}
