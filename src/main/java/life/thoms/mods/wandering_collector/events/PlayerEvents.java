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

package life.thoms.mods.wandering_collector.events;

import it.unimi.dsi.fastutil.longs.LongSet;
import life.thoms.mods.wandering_collector.config.WanderingCollectorConfig;
import life.thoms.mods.wandering_collector.constants.ModConstants;
import life.thoms.mods.wandering_collector.helpers.InventoryDataHelper;
import life.thoms.mods.wandering_collector.helpers.TraderSummoningHelper;
import net.minecraft.block.BellBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.tileentity.BellTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.VillageStructure;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

/**
 * Handles events related to player actions, including death and interactions with blocks.
 */
@Mod.EventBusSubscriber(modid = ModConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerEvents {

    /**
     * Called when a player dies. This method sets the ownership of inventory items to the player.
     *
     * @param playerDeathEvent The event containing information about the player death.
     */
    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent playerDeathEvent) {
        if (!playerDeathEvent.getEntity().level.isClientSide()) {
            if (playerDeathEvent.getEntity() instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) playerDeathEvent.getEntity();
                InventoryDataHelper.setOwnerToInventoryItems(player);
            }
        }
    }

    /**
     * Called when a player right-clicks a block. This method checks if the block is a bell and
     * if the player is holding an emerald, potentially summoning a trader.
     *
     * @param rightClickBlockEvent The event containing information about the block interaction.
     */
    @SubscribeEvent
    public static void onPlayerRightClickBlock(PlayerInteractEvent.RightClickBlock rightClickBlockEvent) {
        if (WanderingCollectorConfig.TRADER_SUMMONING_ENABLED.get()) {
            if (!rightClickBlockEvent.getWorld().isClientSide()) {
                if (rightClickBlockEvent.getWorld().getBlockEntity(rightClickBlockEvent.getHitVec().getBlockPos()) instanceof BellTileEntity) {
                    PlayerEntity player = rightClickBlockEvent.getPlayer();
                    if (player.getItemInHand(player.getUsedItemHand()).getItem() == Items.EMERALD) {
                        BlockPos pos = rightClickBlockEvent.getPos();
                        World world = rightClickBlockEvent.getWorld();

                        BlockState bellState = world.getBlockState(pos);
                        Direction bellDirection = bellState.getValue(BellBlock.FACING);
                        Direction bellHitDirection = rightClickBlockEvent.getHitVec().getDirection();
                        boolean bellWillRing = false;

                        switch (bellState.getValue(BellBlock.ATTACHMENT)) {
                            case FLOOR: {
                                if (bellDirection == bellHitDirection || bellDirection == bellHitDirection.getOpposite()) {
                                    bellWillRing = true;
                                }
                                break;
                            }
                            case CEILING: {
                                if (bellDirection != Direction.UP && bellDirection != Direction.DOWN) {
                                    bellWillRing = true;
                                }
                                break;
                            }
                            default: {
                                if (bellDirection.getClockWise() == bellHitDirection ||
                                        bellDirection.getCounterClockWise() == bellHitDirection) {
                                    bellWillRing = true;
                                }
                                break;
                            }
                        }

                        if (bellWillRing) {
                            Map<Structure<?>, LongSet> structureMap = rightClickBlockEvent.getWorld().getChunk(pos).getAllReferences();
                            for (Structure<?> structure : structureMap.keySet()) {
                                if (structure.getStructure() instanceof VillageStructure) {
                                    VillageStructure villageStructure = (VillageStructure) structure;
                                    if (structureMap.get(structure).toLongArray().length > 0) {
                                        Long villageId = structureMap.get(structure).toLongArray()[0];
                                        TraderSummoningHelper.summonTrader(villageId, pos, world, rightClickBlockEvent.getWorld().getGameTime(), player);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
