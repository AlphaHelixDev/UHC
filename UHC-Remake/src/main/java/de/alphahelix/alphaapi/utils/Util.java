/*
 *     Copyright (C) <2016>  <AlphaHelixDev>
 *
 *     This program is free software: you can redistribute it under the
 *     terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.alphahelix.alphaapi.utils;

import de.alphahelix.alphaapi.AlphaAPI;
import de.alphahelix.alphaapi.uuid.UUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Util {

    /**
     * Rounds a {@link Double} up
     *
     * @param value     the {@link Double} to round
     * @param precision the precision to round up to
     * @return the rounded up {@link Double}
     */
    public static double round(double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

    /**
     * Creates a cooldown
     *
     * @param length       the lenght of the cooldown in ticks
     * @param key          the key to add a cooldown for
     * @param cooldownList the {@link List} where the key is in
     */
    public static <T> void cooldown(int length, final T key, final List<T> cooldownList) {
        cooldownList.add(key);
        new BukkitRunnable() {
            public void run() {
                cooldownList.remove(key);
            }
        }.runTaskLaterAsynchronously(AlphaAPI.getInstance(), length);
    }

    /**
     * @return [] out of a ... array
     */
    @SafeVarargs
    public static <T> T[] makeArray(T... types) {
        return types;
    }

    public static Player[] makePlayerArray(Player... types) {
        return types;
    }

    public static Player[] makePlayerArray(List<String> types) {
        ArrayList<Player> playerArrayList = new ArrayList<>();
        for (String type : types) {
            if (Bukkit.getPlayer(UUIDFetcher.getUUID(type)) == null) continue;
            playerArrayList.add(Bukkit.getPlayer(UUIDFetcher.getUUID(type)));
        }
        return playerArrayList.toArray(new Player[playerArrayList.size()]);
    }

    public static <T, E> Set<T> getKeysByValue(Map<T, E> map, E value) {
        Set<T> keys = new HashSet<>();
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                keys.add(entry.getKey());
            }
        }
        return keys;
    }

    /**
     * Gets the key by its value
     *
     * @param map   the {@link Map} where the key & value is inside
     * @param value the value of the key
     * @return the key
     */
    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
}
