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

import net.minecraft.world.entity.item.ItemEntity;

/**
 * Utility class for handling operations related to item entities.
 */
public class ItemEntityUtil {

    /**
     * Checks if the specified item entity has despawned based on various conditions.
     *
     * @param itemEntity the item entity to check
     * @param minBuildHeight the minimum build height below which the entity is considered to have despawned
     * @return true if the item entity has despawned, false otherwise
     */
    public static boolean itemEntityHasDespawned(ItemEntity itemEntity, int minBuildHeight) {
        return (itemEntity.hurtMarked || itemEntity.lifespan == itemEntity.getAge() ||
                itemEntity.getY() < minBuildHeight ||
                (itemEntity.isOnFire() || itemEntity.wasOnFire) && !itemEntity.fireImmune());
    }
}
