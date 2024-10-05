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

import life.thoms.mods.wandering_collector.constants.ModConstants;
import life.thoms.mods.wandering_collector.data.LootPersistenceManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Handles player session events such as login and logout.
 */
@Mod.EventBusSubscriber(modid = ModConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerSessionEvents {

    /**
     * Called when a player logs in. This event loads the player's loot data.
     *
     * @param loginEvent the event triggered on player login
     */
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent loginEvent) {
        if (!loginEvent.getPlayer().level.isClientSide()) {
            ModConstants.SERVER_LOOT.remove(loginEvent.getPlayer().getUUID());
            LootPersistenceManager.loadPlayerLoot((ServerPlayer) loginEvent.getPlayer());
        }
    }

    /**
     * Called when a player logs out. This event saves the player's loot data.
     *
     * @param logoutEvent the event triggered on player logout
     */
    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent logoutEvent) {
        if (!logoutEvent.getPlayer().level.isClientSide()) {
            LootPersistenceManager.savePlayerLoot(logoutEvent.getPlayer());
            ModConstants.SERVER_LOOT.remove(logoutEvent.getPlayer().getUUID());
        }
    }
}
