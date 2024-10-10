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

package life.thoms.mods.wandering_collector.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

/**
 * Configuration class for the Wandering Collector mod.
 * This class defines the configuration options for various features of the mod.
 */
public class WanderingCollectorConfig {

    public static final ForgeConfigSpec SPEC;

    private static final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> VALUABLE_ITEM_IDS;

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> VALUABLE_TAG_IDS;

    public static final ForgeConfigSpec.LongValue ITEM_DISPOSAL_TIME;

    public static final ForgeConfigSpec.LongValue TRADER_SUMMONING_COOLDOWN_DURATION;

    public static final ForgeConfigSpec.IntValue PROBABILITY_OF_LOST_ITEM_IN_TRADE;

    public static final ForgeConfigSpec.IntValue BASE_PRICE_OF_LOST_ITEM_IN_TRADE;

    public static final  ForgeConfigSpec.BooleanValue TRADER_SUMMONING_ENABLED;

    static {
        builder.push("");
        builder.comment("Weapons, armor, tools, and all enchanted items including books are added by default.")
                .comment("But only if they are not of tier: leather (armor), wooden, stone, iron, and gold")
                .comment("You don't need to add all items or tags, just the ones that aren't included by default.")
                .comment("Enchanted books will only cost base price");
        builder.pop();

        builder.push("Loot Config");
        VALUABLE_ITEM_IDS = builder
                .comment("List of valuable item IDs/price. Use Minecraft namespace format (e.g. minecraft:golden_apple/5).")
                .comment("Can also use modded items (e.g. spartanweaponry:battle_hammer/20)")
                .defineList("VALUABLE_ITEM_IDS", List.of(
                        "minecraft:golden_apple/5",
                        "minecraft:enchanted_golden_apple/15",
                        "minecraft:golden_carrot/1",
                        "minecraft:totem_of_undying/15",
                        "minecraft:nether_star/70",
                        "minecraft:ender_pearl/2",
                        "minecraft:ender_eye/15"
                ), value -> value instanceof String);

        VALUABLE_TAG_IDS = builder
                .comment("List of valuable item tags/price. Use Minecraft namespace format (e.g. forge:gems/4).")
                .comment("Can also use modded tags (e.g. alexmobs:chestplates/25)")
                .defineList("VALUABLE_TAG_IDS", List.of(), value -> value instanceof String);
        builder.pop();

        builder.push("Item Expiration Config");

        TRADER_SUMMONING_ENABLED = builder
                .comment("Enable being able to summon traders in villages by ringing a bell with an emerald")
                .define("TRADER_SUMMONING_ENABLED", true );

        ITEM_DISPOSAL_TIME = builder
                .comment("Minecraft ticks until item lost forever (0 to disable) (24000 = 1 in game day) (2 week default)")
                .defineInRange("ITEM_DISPOSAL_TIME", 336000, 0, Long.MAX_VALUE);
        builder.pop();

        builder.push("Wandering Trader Config");
        TRADER_SUMMONING_COOLDOWN_DURATION = builder
                .comment("Minecraft ticks until you can call another trader per village (24000 = 1 in game day)" +
                        "(4 day default) (called by village bell)")
                .defineInRange("TRADER_SUMMONING_COOLDOWN_DURATION", 96000, 0, Long.MAX_VALUE);

        PROBABILITY_OF_LOST_ITEM_IN_TRADE = builder
                .comment("Probability % on any item to be added as trade on wandering trader every cooldown refresh (default: \"15\")")
                .defineInRange("PROBABILITY_OF_LOST_ITEM_IN_TRADE", 15, 0, 100);

        BASE_PRICE_OF_LOST_ITEM_IN_TRADE = builder
                .comment("Base price of item trades in emeralds, influenced by item type and enchantments")
                .comment("(default: \"4\") (min: 1) (it should be what you consider a diamond is worth), netherite is double")
                .defineInRange("BASE_PRICE_OF_LOST_ITEM_IN_TRADE", 4, 1, Integer.MAX_VALUE);
        builder.pop();

        SPEC = builder.build();
    }
}
