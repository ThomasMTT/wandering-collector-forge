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
import net.minecraft.entity.EntityType;
import net.minecraft.entity.merchant.villager.WanderingTraderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;

import java.util.Random;

/**
 * Helper class for summoning wandering traders in the game.
 */
public class TraderSummoningHelper {

    /**
     * Summons a wandering trader at a specified position in the world, if the cooldown has expired.
     *
     * @param villageId  The ID of the village associated with the trader.
     * @param pos        The position where the trader will be summoned.
     * @param world      The world instance where the trader will be spawned.
     * @param gameTime   The current game time.
     * @param player     The player who initiated the summoning.
     */
    public static void summonTrader(Long villageId, net.minecraft.util.math.BlockPos pos, World world, long gameTime, PlayerEntity player) {
        int chunkSize = 16;
        Random random = new Random();
        int minDistanceChunks = 2;
        int chunkRadius = 3;

        int offsetX = (random.nextInt(2 * minDistanceChunks + chunkRadius) - minDistanceChunks) * chunkSize;
        int offsetZ = (random.nextInt(2 * minDistanceChunks + chunkRadius) - minDistanceChunks) * chunkSize;

        int newX = pos.getX() + offsetX;
        int newZ = pos.getZ() + offsetZ;
        int newY = world.getHeight(Heightmap.Type.WORLD_SURFACE, newX, newZ);

        WanderingTraderEntity wanderingTrader = new WanderingTraderEntity(EntityType.WANDERING_TRADER, world);
        wanderingTrader.setPos(newX, newY, newZ);

        for (Long listStructure : ModConstants.VILLAGE_SUMMON_COOLDOWN.keySet()) {
            if (listStructure.equals(villageId)) {
                Long villageCooldown = ModConstants.VILLAGE_SUMMON_COOLDOWN.get(villageId);
                if (gameTime < villageCooldown + WanderingCollectorConfig.TRADER_SUMMONING_COOLDOWN_DURATION.get()) {
                    ITextComponent textComponent = getVillageCooldownComponent(gameTime, villageCooldown);
                    player.sendMessage(textComponent, player.getUUID());
                    return;
                }
            }
        }

        ModConstants.VILLAGE_SUMMON_COOLDOWN.put(villageId, gameTime);
        world.addFreshEntity(wanderingTrader);
        wanderingTrader.spawnAnim();
        wanderingTrader.setWanderTarget(pos);
        ITextComponent textComponent = new StringTextComponent("Wandering Trader is on his way");
        player.sendMessage(textComponent, player.getUUID());
    }

    /**
     * Constructs a message indicating the remaining cooldown time before a new trader can be summoned.
     *
     * @param gameTime      The current game time.
     * @param villageCooldown The last time a trader was summoned.
     * @return An ITextComponent containing the cooldown message.
     */
    private static ITextComponent getVillageCooldownComponent(long gameTime, Long villageCooldown) {
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
        return new StringTextComponent("There aren't any new traders nearby, come back in " + timeLeft + timeMetric);
    }
}
