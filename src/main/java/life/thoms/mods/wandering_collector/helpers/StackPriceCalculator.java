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

import life.thoms.mods.wandering_collector.config.WanderingCollectorConfig;
import net.minecraft.world.item.*;

import static life.thoms.mods.wandering_collector.utils.ItemFilterUtil.*;

/**
 * A calculator for determining the price of item stacks based on their
 * type, enchantments, and other characteristics.
 */
public class StackPriceCalculator {

    private static final int basePrice = WanderingCollectorConfig.BASE_PRICE_OF_LOST_ITEM_IN_TRADE.get();

    /**
     * Calculates the price of a given ItemStack based on its type,
     * enchantments, and other factors.
     *
     * @param stack the ItemStack for which the price is to be calculated
     * @return the calculated price of the item stack
     */
    public static int getStackPrice(ItemStack stack) {

        if (isEnchantedBook(stack)) {
            return basePrice;
        }

        int price = basePrice;

        int configPrice = getConfiguredPrice(stack);
        if (configPrice != -1) {
            price = configPrice * stack.getCount();
            if (isEnchanted(stack)) {
                price += stack.getEnchantmentTags().size() * basePrice * 2;
            }
            return price;
        }

        if (isWeapon(stack)) {
            Item item = stack.getItem();

            if (item instanceof BowItem || item instanceof CrossbowItem || item instanceof TridentItem) {
                price += basePrice * 4;
            } else {
                price *= 2;
                SwordItem sword = (SwordItem) stack.getItem();
                if (sword.getTier().equals(Tiers.NETHERITE)) {
                    price *= 2;
                }
            }
        } else if (isArmor(stack)) {
            if (stack.getItem() instanceof ElytraItem) {
                price = basePrice * 20;
            } else {
                ArmorItem armor = (ArmorItem) stack.getItem();
                short materialsPerArmor = 0;
                switch (armor.getSlot()) {
                    case HEAD -> materialsPerArmor = 5;
                    case CHEST -> materialsPerArmor = 8;
                    case LEGS -> materialsPerArmor = 7;
                    case FEET -> materialsPerArmor = 4;
                }
                price = basePrice * materialsPerArmor;
                if (armor.getMaterial().equals(ArmorMaterials.NETHERITE)) {
                    price *= 2;
                }
            }
        } else if (isTool(stack)) {
            if (stack.getItem() instanceof FishingRodItem) {
                return Math.round((float) basePrice / 2);
            }

            Tier tier = Tiers.WOOD;
            short materialsPerArmor = 0;
            Item tool = stack.getItem();
            if (tool instanceof PickaxeItem pickaxeItem) {
                tier = pickaxeItem.getTier();
                materialsPerArmor = 3;
            } else if (tool instanceof AxeItem axeItem) {
                tier = axeItem.getTier();
                materialsPerArmor = 3;
            } else if (tool instanceof ShovelItem shovelItem) {
                tier = shovelItem.getTier();
                materialsPerArmor = 1;
            } else if (tool instanceof HoeItem hoeItem) {
                tier = hoeItem.getTier();
                materialsPerArmor = 2;
            }

            price = basePrice * materialsPerArmor;
            if (tier.equals(Tiers.NETHERITE)) {
                price *= 2;
            }
        } else {
            Item item = stack.getItem();
            if (item instanceof BlockItem) {
                price = basePrice * 9;
                if (item == Items.NETHERITE_BLOCK) {
                    price *= 2;
                }
            }
            if (item == Items.NETHERITE_INGOT) {
                price *= 2;
            }
            price *= stack.getCount();
        }

        if (isEnchanted(stack)) {
            price += stack.getEnchantmentTags().size() * basePrice * 2;
        }

        return price;
    }
}
