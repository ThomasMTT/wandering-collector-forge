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

package life.thoms.mods.wandering_collector.events;

import life.thoms.mods.wandering_collector.constants.ModConstants;
import life.thoms.mods.wandering_collector.data.LootPersistenceManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Handles player session events, including login and logout.
 * <p>
 * This class is responsible for loading and saving player loot when
 * players log in and out of the server.
 */
@Mod.EventBusSubscriber(modid = ModConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerSessionEvents {

    /**
     * Called when a player logs in.
     *
     * @param loginEvent the event containing information about the player's login
     */
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent loginEvent) {
        if (!loginEvent.getEntity().level.isClientSide()) {
            ModConstants.SERVER_LOOT.remove(loginEvent.getEntity().getUUID());
            LootPersistenceManager.loadPlayerLoot((ServerPlayer) loginEvent.getEntity());
        }
    }

    /**
     * Called when a player logs out.
     *
     * @param logoutEvent the event containing information about the player's logout
     */
    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent logoutEvent) {
        if (!logoutEvent.getEntity().level.isClientSide()) {
            LootPersistenceManager.savePlayerLoot(logoutEvent.getEntity());
            ModConstants.SERVER_LOOT.remove(logoutEvent.getEntity().getUUID());
        }
    }

}
