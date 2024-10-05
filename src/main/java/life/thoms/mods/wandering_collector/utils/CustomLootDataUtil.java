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

import life.thoms.mods.wandering_collector.constants.ModConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.*;

/**
 * Utility class for managing custom loot data associated with item stacks.
 */
public class CustomLootDataUtil {

    /**
     * Retrieves the unique identifier for the given item stack.
     *
     * @param stack the item stack
     * @return the unique identifier or null if not present
     */
    public static UUID getStackUniqueIdentifier(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains(ModConstants.STACK_UUID)) {
            return tag.getUUID(ModConstants.STACK_UUID);
        }
        return null;
    }

    /**
     * Retrieves the expiration time for the given item stack.
     *
     * @param stack the item stack
     * @return the expiration time or null if not present
     */
    public static Long getStackExpirationTime(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains(ModConstants.STACK_DISPOSAL_TIME)) {
            return tag.getLong(ModConstants.STACK_DISPOSAL_TIME);
        }
        return null;
    }

    /**
     * Adds custom data to the item stack, including a unique identifier and expiration time.
     *
     * @param stack the item stack
     * @param gameTime the current game time
     * @return the modified item stack
     */
    public static ItemStack addStackCustomData(ItemStack stack, Long gameTime) {
        CompoundTag stackDataTag = stack.getOrCreateTag();
        stackDataTag.putUUID(ModConstants.STACK_UUID, UUID.randomUUID());
        stackDataTag.putLong(ModConstants.STACK_DISPOSAL_TIME, gameTime);
        return stack;
    }

    /**
     * Retrieves the owner of the given item stack.
     *
     * @param stack the item stack
     * @return the owner's UUID or null if not present
     */
    public static UUID getStackOwner(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains(ModConstants.STACK_OWNER)) {
            return tag.getUUID(ModConstants.STACK_OWNER);
        }
        return null;
    }

    /**
     * Sets the owner of the given item stack.
     *
     * @param stack the item stack
     * @param stackOwner the owner's UUID
     */
    public static void setStackOwner(ItemStack stack, UUID stackOwner) {
        CompoundTag stackDataTag = stack.getOrCreateTag();
        stackDataTag.putUUID(ModConstants.STACK_OWNER, stackOwner);
    }

    /**
     * Retrieves an item stack from a list based on its unique identifier.
     *
     * @param stackUniqueIdentifier the unique identifier
     * @param stackList the list of item stacks
     * @return the matching item stack or null if not found
     */
    public static ItemStack getStackFromList(UUID stackUniqueIdentifier, List<ItemStack> stackList) {
        if (stackUniqueIdentifier != null && stackList != null) {
            for (ItemStack stackFromPlayerLoot : stackList) {
                UUID stackUniqueIdentifierFromList = CustomLootDataUtil.getStackUniqueIdentifier(stackFromPlayerLoot);
                if (stackUniqueIdentifier.equals(stackUniqueIdentifierFromList)) return stackFromPlayerLoot;
            }
        }
        return null;
    }

    /**
     * Checks if a unique identifier exists in a list of item stacks.
     *
     * @param stackUniqueIdentifier the unique identifier
     * @param stackList the list of item stacks
     * @return true if found, false otherwise
     */
    public static boolean isUniqueIdentifierInList(UUID stackUniqueIdentifier, List<ItemStack> stackList) {
        return getStackFromList(stackUniqueIdentifier, stackList) != null;
    }
}
