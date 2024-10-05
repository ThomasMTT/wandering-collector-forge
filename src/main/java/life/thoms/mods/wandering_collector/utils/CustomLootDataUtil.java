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
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import java.util.*;

/**
 * Utility class for managing custom loot data associated with ItemStacks.
 */
public class CustomLootDataUtil {

    /**
     * Gets the unique identifier for a given ItemStack.
     *
     * @param stack The ItemStack to retrieve the identifier from.
     * @return The unique identifier (UUID) or null if not present.
     */
    public static UUID getStackUniqueIdentifier(ItemStack stack) {
        CompoundNBT tag = stack.getOrCreateTag();
        if (tag.contains(ModConstants.STACK_UUID)) {
            return tag.getUUID(ModConstants.STACK_UUID);
        }
        return null;
    }

    /**
     * Gets the expiration time for a given ItemStack.
     *
     * @param stack The ItemStack to check for expiration time.
     * @return The expiration time (Long) or null if not present.
     */
    public static Long getStackExpirationTime(ItemStack stack) {
        CompoundNBT tag = stack.getOrCreateTag();
        if (tag.contains(ModConstants.STACK_DISPOSAL_TIME)) {
            return tag.getLong(ModConstants.STACK_DISPOSAL_TIME);
        }
        return null;
    }

    /**
     * Adds custom data (UUID and disposal time) to the given ItemStack.
     *
     * @param stack The ItemStack to add custom data to.
     * @param gameTime The current game time to set as the disposal time.
     * @return The modified ItemStack with custom data.
     */
    public static ItemStack addStackCustomData(ItemStack stack, Long gameTime) {
        CompoundNBT stackDataTag = stack.getOrCreateTag();
        stackDataTag.putUUID(ModConstants.STACK_UUID, UUID.randomUUID());
        stackDataTag.putLong(ModConstants.STACK_DISPOSAL_TIME, gameTime);
        return stack;
    }

    /**
     * Gets the owner of the given ItemStack.
     *
     * @param stack The ItemStack to check for an owner.
     * @return The owner's UUID or null if not present.
     */
    public static UUID getStackOwner(ItemStack stack) {
        CompoundNBT tag = stack.getOrCreateTag();
        if (tag.contains(ModConstants.STACK_OWNER)) {
            return tag.getUUID(ModConstants.STACK_OWNER);
        }
        return null;
    }

    /**
     * Sets the owner of the given ItemStack.
     *
     * @param stack The ItemStack to set the owner for.
     * @param stackOwner The UUID of the new owner.
     */
    public static void setStackOwner(ItemStack stack, UUID stackOwner) {
        CompoundNBT stackDataTag = stack.getOrCreateTag();
        stackDataTag.putUUID(ModConstants.STACK_OWNER, stackOwner);
    }

    /**
     * Retrieves an ItemStack from a list based on its unique identifier.
     *
     * @param stackUniqueIdentifier The unique identifier to search for.
     * @param stackList The list of ItemStacks to search in.
     * @return The matching ItemStack or null if not found.
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
     * Checks if a unique identifier exists in a list of ItemStacks.
     *
     * @param stackUniqueIdentifier The unique identifier to check.
     * @param stackList The list of ItemStacks to search in.
     * @return True if the identifier is in the list, false otherwise.
     */
    public static boolean isUniqueIdentifierInList(UUID stackUniqueIdentifier, List<ItemStack> stackList) {
        return getStackFromList(stackUniqueIdentifier, stackList) != null;
    }
}
