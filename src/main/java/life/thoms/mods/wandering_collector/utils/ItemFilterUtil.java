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

package life.thoms.mods.wandering_collector.utils;

import life.thoms.mods.wandering_collector.config.WanderingCollectorConfig;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Utility class for filtering and checking properties of ItemStacks.
 */
public class ItemFilterUtil {

    /**
     * Checks if the given ItemStack is excluded from consideration.
     *
     * @param stack The ItemStack to check.
     * @return True if the ItemStack is excluded, false otherwise.
     */
    public static boolean isExcludedItem(ItemStack stack) {
        return (Items.EMERALD == stack.getItem() || Items.AIR == stack.getItem());
    }

    /**
     * Checks if the given ItemStack is enchanted.
     *
     * @param stack The ItemStack to check.
     * @return True if the ItemStack is enchanted, false otherwise.
     */
    public static boolean isEnchanted(ItemStack stack) {
        return stack.isEnchanted();
    }

    /**
     * Checks if the given ItemStack is an enchanted book.
     *
     * @param stack The ItemStack to check.
     * @return True if the ItemStack is an enchanted book, false otherwise.
     */
    public static boolean isEnchantedBook(ItemStack stack) {
        return stack.getItem() instanceof EnchantedBookItem;
    }

    /**
     * Checks if the given ItemStack is classified as a weapon.
     *
     * @param stack The ItemStack to check.
     * @return True if the ItemStack is a weapon, false otherwise.
     */
    public static boolean isWeapon(ItemStack stack) {
        Item weapon = stack.getItem();

        if (weapon instanceof SwordItem) {
            Tier tier = (((SwordItem) weapon).getTier());
            return !(tier.equals(Tiers.WOOD) || tier.equals(Tiers.STONE) ||
                    tier.equals(Tiers.IRON) || tier.equals(Tiers.GOLD));
        }
        return (weapon instanceof BowItem || weapon instanceof CrossbowItem || weapon instanceof TridentItem);
    }

    /**
     * Checks if the given ItemStack is classified as armor.
     *
     * @param stack The ItemStack to check.
     * @return True if the ItemStack is armor, false otherwise.
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
            Holder<ArmorMaterial> material = ((ArmorItem) armor).getMaterial();
            return !(material.equals(ArmorMaterials.LEATHER) || material.equals(ArmorMaterials.IRON) ||
                    material.equals(ArmorMaterials.GOLD));
        }
        return false;
    }

    /**
     * Checks if the given ItemStack is classified as a tool.
     *
     * @param stack The ItemStack to check.
     * @return True if the ItemStack is a tool, false otherwise.
     */
    public static boolean isTool(ItemStack stack) {
        Item tool = stack.getItem();

        if (tool instanceof FishingRodItem) {
            return true;
        }

        Tier tier = Tiers.WOOD;
        switch (tool) {
            case PickaxeItem pickaxeItem -> tier = pickaxeItem.getTier();
            case AxeItem axeItem -> tier = axeItem.getTier();
            case ShovelItem shovelItem -> tier = shovelItem.getTier();
            case HoeItem hoeItem -> tier = hoeItem.getTier();
            default -> {
            }
        }

        return !(tier.equals(Tiers.WOOD) || tier.equals(Tiers.STONE) ||
                tier.equals(Tiers.IRON) || tier.equals(Tiers.GOLD));
    }

    /**
     * Checks if the given ItemStack is classified as a valuable block.
     *
     * @param stack The ItemStack to check.
     * @return True if the ItemStack is a valuable block, false otherwise.
     */
    public static boolean isValuableBlock(ItemStack stack) {
        Item item = stack.getItem();
        return (item == Items.DIAMOND_BLOCK || item == Items.NETHERITE_BLOCK);
    }

    /**
     * Checks if the given ItemStack is classified as a valuable ingot.
     *
     * @param stack The ItemStack to check.
     * @return True if the ItemStack is a valuable ingot, false otherwise.
     */
    public static boolean isValuableIngot(ItemStack stack) {
        Item item = stack.getItem();
        return (item == Items.DIAMOND || item == Items.NETHERITE_INGOT);
    }

    /**
     * Checks if the given ItemStack is classified as an effect potion.
     *
     * @param stack The ItemStack to check.
     * @return True if the ItemStack is an effect potion, false otherwise.
     */
    public static boolean isEffectPotion(ItemStack stack) {
        return stack.getItem() instanceof PotionItem;
    }

    /**
     * Retrieves the configured price for the given ItemStack.
     *
     * @param stack The ItemStack for which to get the price.
     * @return The price of the ItemStack, or -1 if not configured.
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
                        TagKey<Item> tag = TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), new ResourceLocation(id));
                        if (stack.is(tag)) {
                            return price;
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }
        }
        return -1;
    }
}
