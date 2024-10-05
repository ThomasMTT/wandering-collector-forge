package life.thoms.mods.wandering_collector.constants;

import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Holds constants and configuration data for the Wandering Collector mod.
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
