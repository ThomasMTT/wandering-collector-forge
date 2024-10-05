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
import life.thoms.mods.wandering_collector.constants.ModConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.Random;

/**
 * Helper class for summoning wandering traders in the game.
 */
public class TraderSummoningHelper {

    /**
     * Summons a wandering trader at a location determined by a village ID and position.
     *
     * @param villageId the ID of the village to which the trader is associated
     * @param pos the position where the trader should be summoned
     * @param level the level in which the trader will be summoned
     * @param gameTime the current game time
     * @param player the player who initiated the summoning
     */
    public static void summonTrader(Long villageId, BlockPos pos, Level level, long gameTime, Player player) {
        int chunkSize = 16;
        Random random = new Random();
        int minDistanceChunks = 2;
        int chunkRadius = 3;

        int offsetX = (random.nextInt(2 * minDistanceChunks + chunkRadius) - minDistanceChunks) * chunkSize;
        int offsetZ = (random.nextInt(2 * minDistanceChunks + chunkRadius) - minDistanceChunks) * chunkSize;

        int newX = pos.getX() + offsetX;
        int newZ = pos.getZ() + offsetZ;
        int newY = level.getHeight(Heightmap.Types.WORLD_SURFACE, newX, newZ);

        WanderingTrader wanderingTrader = new WanderingTrader(EntityType.WANDERING_TRADER, level);
        wanderingTrader.setPos(newX, newY, newZ);

        // Check for cooldown on summoning traders
        for (Long listStructure : ModConstants.VILLAGE_SUMMON_COOLDOWN.keySet()) {
            if (listStructure.equals(villageId)) {
                Long villageCooldown = ModConstants.VILLAGE_SUMMON_COOLDOWN.get(villageId);
                if (gameTime < villageCooldown + WanderingCollectorConfig.TRADER_SUMMONING_COOLDOWN_DURATION.get()) {
                    Component textComponent = getVillageCooldownComponent(gameTime, villageCooldown);
                    player.sendSystemMessage(textComponent);
                    return;
                }
            }
        }

        ModConstants.VILLAGE_SUMMON_COOLDOWN.put(villageId, gameTime);
        level.addFreshEntity(wanderingTrader);
        wanderingTrader.spawnAnim();
        wanderingTrader.setWanderTarget(pos);
        Component textComponent = Component.literal("Wandering Trader is on his way");
        player.sendSystemMessage(textComponent);
    }

    /**
     * Generates a message indicating the time left until a new trader can be summoned.
     *
     * @param gameTime the current game time
     * @param villageCooldown the last cooldown time for the village
     * @return a component containing the cooldown message
     */
    private static Component getVillageCooldownComponent(long gameTime, Long villageCooldown) {
        String timeMetric = " seconds";
        long timeLeft = (WanderingCollectorConfig.TRADER_SUMMONING_COOLDOWN_DURATION.get()
                - (gameTime - villageCooldown));
        if (timeLeft > 24000) {
            timeMetric = " day";
            if (timeLeft > 48000) {
                timeMetric = " days";
            }
            timeLeft = Math.round((float) timeLeft / 24000);
        } else if (timeLeft > 1200) {
            timeLeft = Math.round((float) timeLeft / 1200);
            timeMetric = " minutes";
        } else {
            timeLeft = Math.round((float) timeLeft / 20);
        }
        return Component.literal("There aren't any new traders nearby, come back in " + timeLeft + timeMetric);
    }
}
