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

package life.thoms.mods.wandering_collector.utils;

import life.thoms.mods.wandering_collector.config.WanderingCollectorConfig;
import net.minecraft.item.*;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Utility class for filtering and classifying ItemStacks.
 */
public class ItemFilterUtil {

    /**
     * Checks if the given item stack is an excluded item.
     *
     * @param stack The ItemStack to check.
     * @return True if the item is excluded, false otherwise.
     */
    public static boolean isExcludedItem(ItemStack stack) {
        return (Items.EMERALD == stack.getItem() ||
                Items.AIR == stack.getItem());
    }

    /**
     * Checks if the given item stack is enchanted.
     *
     * @param stack The ItemStack to check.
     * @return True if the item stack is enchanted, false otherwise.
     */
    public static boolean isEnchanted(ItemStack stack) {
        return stack.isEnchanted();
    }

    /**
     * Checks if the given item stack is an enchanted book.
     *
     * @param stack The ItemStack to check.
     * @return True if the item stack is an enchanted book, false otherwise.
     */
    public static boolean isEnchantedBook(ItemStack stack) {
        return stack.getItem() instanceof EnchantedBookItem;
    }

    /**
     * Checks if the given item stack is a weapon.
     *
     * @param stack The ItemStack to check.
     * @return True if the item stack is a weapon, false otherwise.
     */
    public static boolean isWeapon(ItemStack stack) {
        Item weapon = stack.getItem();

        if (weapon instanceof SwordItem) {
            IItemTier tier = (((SwordItem) weapon).getTier());
            return !(tier.equals(ItemTier.WOOD) || tier.equals(ItemTier.STONE) ||
                    tier.equals(ItemTier.IRON) || tier.equals(ItemTier.GOLD));
        }
        return (weapon instanceof BowItem || weapon instanceof CrossbowItem || weapon instanceof TridentItem);
    }

    /**
     * Checks if the given item stack is armor.
     *
     * @param stack The ItemStack to check.
     * @return True if the item stack is armor, false otherwise.
     */
    public static boolean isArmor(ItemStack stack) {
        Item armor = stack.getItem();
        if (armor instanceof ElytraItem) {
            return true;
        }
        if (armor.toString().contains("wolf_armor") || armor.toString().contains("horse_armor")) {
            return false;
        }
        if (armor instanceof ArmorItem) {
            IArmorMaterial material = ((ArmorItem) armor).getMaterial();
            return !(material.equals(ArmorMaterial.LEATHER) || material.equals(ArmorMaterial.IRON) ||
                    material.equals(ArmorMaterial.GOLD));
        }
        return false;
    }

    /**
     * Checks if the given item stack is a tool.
     *
     * @param stack The ItemStack to check.
     * @return True if the item stack is a tool, false otherwise.
     */
    public static boolean isTool(ItemStack stack) {
        Item tool = stack.getItem();

        if (tool instanceof FishingRodItem) {
            return true;
        }

        IItemTier tier = ItemTier.WOOD;
        if (tool instanceof PickaxeItem) {
            PickaxeItem pickaxeItem = (PickaxeItem) tool;
            tier = pickaxeItem.getTier();
        } else if (tool instanceof AxeItem) {
            AxeItem axeItem = (AxeItem) tool;
            tier = axeItem.getTier();
        } else if (tool instanceof ShovelItem) {
            ShovelItem shovelItem = (ShovelItem) tool;
            tier = shovelItem.getTier();
        } else if (tool instanceof HoeItem) {
            HoeItem hoeItem = (HoeItem) tool;
            tier = hoeItem.getTier();
        }

        return !(tier.equals(ItemTier.WOOD) || tier.equals(ItemTier.STONE) ||
                tier.equals(ItemTier.IRON) || tier.equals(ItemTier.GOLD));
    }

    /**
     * Checks if the given item stack is a valuable block.
     *
     * @param stack The ItemStack to check.
     * @return True if the item stack is a valuable block, false otherwise.
     */
    public static boolean isValuableBlock(ItemStack stack) {
        Item item = stack.getItem();
        return (item == Items.DIAMOND_BLOCK || item == Items.NETHERITE_BLOCK);
    }

    /**
     * Checks if the given item stack is a valuable ingot.
     *
     * @param stack The ItemStack to check.
     * @return True if the item stack is a valuable ingot, false otherwise.
     */
    public static boolean isValuableIngot(ItemStack stack) {
        Item item = stack.getItem();
        return (item == Items.DIAMOND || item == Items.NETHERITE_INGOT);
    }

    /**
     * Checks if the given item stack is a potion with effects.
     *
     * @param stack The ItemStack to check.
     * @return True if the item stack is an effect potion, false otherwise.
     */
    public static boolean isEffectPotion(ItemStack stack) {
        return stack.getItem() instanceof PotionItem;
    }

    /**
     * Gets the configured price for the given item stack based on the configuration.
     *
     * @param stack The ItemStack to get the price for.
     * @return The price of the item, or -1 if not found.
     */
    public static int getConfiguredPrice(ItemStack stack) {
        if (WanderingCollectorConfig.VALUABLE_ITEM_IDS != null) {
            for (String itemId : WanderingCollectorConfig.VALUABLE_ITEM_IDS.get()) {
                String[] values = itemId.split("/");
                if (values.length == 2) {
                    String id = values[0];
                    String priceStr = values[1];
                    try {
                        int price = Integer.parseInt(priceStr);
                        ResourceLocation location = new ResourceLocation(id);
                        Item valuableItem = ForgeRegistries.ITEMS.getValue(location);
                        if (valuableItem != null && stack.getItem() == valuableItem) {
                            return price;
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }
        }

        if (WanderingCollectorConfig.VALUABLE_TAG_IDS != null) {
            for (String tagId : WanderingCollectorConfig.VALUABLE_TAG_IDS.get()) {
                String[] values = tagId.split("/");
                if (values.length == 2) {
                    String id = values[0];
                    String priceStr = values[1];
                    try {
                        int price = Integer.parseInt(priceStr);
                        ITag.INamedTag<Item> tag = ItemTags.bind(id);
                        if (tag.contains(stack.getItem())) {
                            return price;
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }
        }
        return -1;
    }
}
