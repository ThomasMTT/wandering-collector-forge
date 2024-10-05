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

package life.thoms.mods.wandering_collector.constants;

import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Class to hold mod constants for the Wandering Collector mod.
 * Contains various identifiers and data structures used throughout the mod.
 */
public class ModConstants {

    public static final String MOD_ID = "wandering_collector";

    public static final Map<UUID, List<ItemStack>> SERVER_LOOT = new HashMap<>();
    public static final Map<Long, Long> VILLAGE_SUMMON_COOLDOWN = new HashMap<>();

    public static final String PLAYER_LOOT_KEY = "playerLootPersistence";
    public static final String STACK_UUID = "item_stack_uuid";
    public static final String STACK_DISPOSAL_TIME = "item_stack_disposal_time";
    public static final String STACK_OWNER = "item_stack_owner";

    public static final String TRADER_PLAYER_TRADES = "trader_player_trades";
    public static final String TRADER_DEFAULT_TRADE_COUNT = "trader_default_count";

}
