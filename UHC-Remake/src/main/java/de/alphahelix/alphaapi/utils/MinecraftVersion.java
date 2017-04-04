/*
 * Copyright (C) <2017>  <AlphaHelixDev>
 *
 *       This program is free software: you can redistribute it under the
 *       terms of the GNU General Public License as published by
 *       the Free Software Foundation, either version 3 of the License.
 *
 *       This program is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU General Public License for more details.
 *
 *       You should have received a copy of the GNU General Public License
 *       along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.alphahelix.alphaapi.utils;

import de.alphahelix.alphaapi.reflection.ReflectionUtil;

public enum MinecraftVersion {

    EIGHT,
    NINE,
    TEN,
    ELEVEN;

    static {
        if(ReflectionUtil.getVersion().contains("1_8")) {
            server = EIGHT;
        } else if(ReflectionUtil.getVersion().contains("1_9")) {
            server = NINE;
        } else if(ReflectionUtil.getVersion().contains("1_10")) {
            server = TEN;
        } else if(ReflectionUtil.getVersion().contains("1_11")) {
            server = ELEVEN;
        }
    }

    private static MinecraftVersion server;

    public static MinecraftVersion getServer() {
        return server;
    }
}
