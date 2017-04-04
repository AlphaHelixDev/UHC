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

package de.alphahelix.alphaapi.fakeapi.utils.intern;

import de.alphahelix.alphaapi.reflection.ReflectionUtil;
import de.alphahelix.alphaapi.utils.MinecraftVersion;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class FakeUtilBase {

    public static final MinecraftVersion VERSION = MinecraftVersion.getServer();
    private static Constructor<?> packetPlayOutSpawnEntity;
    private static Constructor<?> packetPlayOutSpawnEntityLiving;
    private static Constructor<?> packetPlayOutNamedEntitySpawn;
    private static Constructor<?> packetPlayOutRelEntityMove;
    private static Constructor<?> packetPlayOutEntityTeleport;
    private static Constructor<?> packetPlayOutEntityEquipment;
    private static Constructor<?> packetPlayOutEntityDestroy;
    private static Constructor<?> packetPlayOutEntityMetadata;
    private static Constructor<?> packetPlayOutEntityHeadRotation;
    private static Constructor<?> packetPlayOutEntityLook;
    private static Constructor<?> packetPlayOutAnimation;
    private static Constructor<?> packetPlayOutMount;
    private static Constructor<?> packetPlayOutEntityStatus;

    private static Method setLocation;
    private static Method setInvisible;
    private static Method setCustomName;
    private static Method setCustomNameVisible;
    private static Method getDataWatcher;
    private static Method watch;
    private static Method update;
    private static Method setItemStack;
    private static Method setPassenger;
    private static Method stopRiding;
    private static Method setBaby;
    private static Method setVariant;

    static {
        try {
            if (VERSION != MinecraftVersion.EIGHT) {
                packetPlayOutRelEntityMove = ReflectionUtil.getNmsClass("PacketPlayOutEntity$PacketPlayOutRelEntityMove").getConstructor(int.class, long.class, long.class, long.class, boolean.class);
                packetPlayOutEntityTeleport = ReflectionUtil.getNmsClass("PacketPlayOutEntityTeleport").getConstructor(ReflectionUtil.getNmsClass("Entity"));
                packetPlayOutEntityEquipment = ReflectionUtil.getNmsClass("PacketPlayOutEntityEquipment").getConstructor(int.class, ReflectionUtil.getNmsClass("EnumItemSlot"), ReflectionUtil.getNmsClass("ItemStack"));
                packetPlayOutSpawnEntity = ReflectionUtil.getNmsClass("PacketPlayOutSpawnEntity").getConstructor(ReflectionUtil.getNmsClass("Entity"), int.class);
                packetPlayOutSpawnEntityLiving = ReflectionUtil.getNmsClass("PacketPlayOutSpawnEntityLiving").getConstructor(ReflectionUtil.getNmsClass("EntityLiving"));
                packetPlayOutEntityDestroy = ReflectionUtil.getNmsClass("PacketPlayOutEntityDestroy").getConstructor(int[].class);
                packetPlayOutEntityMetadata = ReflectionUtil.getNmsClass("PacketPlayOutEntityMetadata").getConstructor(int.class, ReflectionUtil.getNmsClass("DataWatcher"), boolean.class);
                packetPlayOutEntityHeadRotation = ReflectionUtil.getNmsClass("PacketPlayOutEntityHeadRotation").getConstructor(ReflectionUtil.getNmsClass("Entity"), byte.class);
                packetPlayOutEntityLook = ReflectionUtil.getNmsClass("PacketPlayOutEntity$PacketPlayOutEntityLook").getConstructor(int.class, byte.class, byte.class, boolean.class);
                packetPlayOutAnimation = ReflectionUtil.getNmsClass("PacketPlayOutAnimation").getConstructor(ReflectionUtil.getNmsClass("Entity"), int.class);
                packetPlayOutNamedEntitySpawn = ReflectionUtil.getNmsClass("PacketPlayOutNamedEntitySpawn").getConstructor(ReflectionUtil.getNmsClass("EntityHuman"));
                packetPlayOutMount = ReflectionUtil.getNmsClass("PacketPlayOutMount").getConstructor(ReflectionUtil.getNmsClass("Entity"));
                packetPlayOutEntityStatus = ReflectionUtil.getNmsClass("PacketPlayOutEntityStatus").getConstructor(ReflectionUtil.getNmsClass("Entity"), byte.class);

                setLocation = ReflectionUtil.getNmsClass("Entity").getMethod("setLocation", double.class, double.class, double.class, float.class, float.class);
                setInvisible = ReflectionUtil.getNmsClass("Entity").getMethod("setInvisible", boolean.class);
                setCustomName = ReflectionUtil.getNmsClass("Entity").getMethod("setCustomName", String.class);
                setCustomNameVisible = ReflectionUtil.getNmsClass("Entity").getMethod("setCustomNameVisible", boolean.class);
                getDataWatcher = ReflectionUtil.getNmsClass("Entity").getMethod("getDataWatcher");
                watch = ReflectionUtil.getNmsClass("DataWatcher").getMethod("set", ReflectionUtil.getNmsClass("DataWatcherObject"), Object.class);
                setItemStack = ReflectionUtil.getNmsClass("EntityItem").getMethod("setItemStack", ReflectionUtil.getNmsClass("ItemStack"));
                setPassenger = ReflectionUtil.getNmsClass("Entity").getMethod("startRiding", ReflectionUtil.getNmsClass("Entity"));
                stopRiding = ReflectionUtil.getNmsClass("Entity").getMethod("stopRiding");
                setBaby = ReflectionUtil.getNmsClass("EntityAgeable").getMethod("setAge", int.class);
                setVariant = ReflectionUtil.getNmsClass("EntityLlama").getMethod("setVariant", int.class);
            } else {
                packetPlayOutSpawnEntity = ReflectionUtil.getNmsClass("PacketPlayOutSpawnEntity").getConstructor(ReflectionUtil.getNmsClass("Entity"), int.class);
                packetPlayOutSpawnEntityLiving = ReflectionUtil.getNmsClass("PacketPlayOutSpawnEntityLiving").getConstructor(ReflectionUtil.getNmsClass("EntityLiving"));
                packetPlayOutRelEntityMove = ReflectionUtil.getNmsClass("PacketPlayOutEntity$PacketPlayOutRelEntityMove").getConstructor(int.class, byte.class, byte.class, byte.class, boolean.class);
                packetPlayOutEntityTeleport = ReflectionUtil.getNmsClass("PacketPlayOutEntityTeleport").getConstructor(int.class, int.class, int.class, int.class, byte.class, byte.class, boolean.class);
                packetPlayOutEntityEquipment = ReflectionUtil.getNmsClass("PacketPlayOutEntityEquipment").getConstructor(int.class, int.class, ReflectionUtil.getNmsClass("ItemStack"));
                packetPlayOutEntityDestroy = ReflectionUtil.getNmsClass("PacketPlayOutEntityDestroy").getConstructor(int[].class);
                packetPlayOutEntityMetadata = ReflectionUtil.getNmsClass("PacketPlayOutEntityMetadata").getConstructor(int.class, ReflectionUtil.getNmsClass("DataWatcher"), boolean.class);
                packetPlayOutEntityHeadRotation = ReflectionUtil.getNmsClass("PacketPlayOutEntityHeadRotation").getConstructor(ReflectionUtil.getNmsClass("Entity"), byte.class);
                packetPlayOutEntityLook = ReflectionUtil.getNmsClass("PacketPlayOutEntity$PacketPlayOutEntityLook").getConstructor(int.class, byte.class, byte.class, boolean.class);
                packetPlayOutAnimation = ReflectionUtil.getNmsClass("PacketPlayOutAnimation").getConstructor(ReflectionUtil.getNmsClass("Entity"), int.class);
                packetPlayOutNamedEntitySpawn = ReflectionUtil.getNmsClass("PacketPlayOutNamedEntitySpawn").getConstructor(ReflectionUtil.getNmsClass("EntityHuman"));

                setLocation = ReflectionUtil.getNmsClass("Entity").getMethod("setLocation", double.class, double.class, double.class, float.class, float.class);
                setInvisible = ReflectionUtil.getNmsClass("Entity").getMethod("setInvisible", boolean.class);
                setCustomName = ReflectionUtil.getNmsClass("Entity").getMethod("setCustomName", String.class);
                setCustomNameVisible = ReflectionUtil.getNmsClass("Entity").getMethod("setCustomNameVisible", boolean.class);
                getDataWatcher = ReflectionUtil.getNmsClass("Entity").getMethod("getDataWatcher");
                watch = ReflectionUtil.getNmsClass("DataWatcher").getMethod("watch", int.class, Object.class);
                update = ReflectionUtil.getNmsClass("DataWatcher").getMethod("update", int.class);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

    public static Constructor<?> getPacketPlayOutSpawnEntity() {
        return packetPlayOutSpawnEntity;
    }

    public static Constructor<?> getPacketPlayOutSpawnEntityLiving() {
        return packetPlayOutSpawnEntityLiving;
    }

    public static Constructor<?> getPacketPlayOutRelEntityMove() {
        return packetPlayOutRelEntityMove;
    }

    public static Constructor<?> getPacketPlayOutEntityTeleport() {
        return packetPlayOutEntityTeleport;
    }

    public static Constructor<?> getPacketPlayOutEntityEquipment() {
        return packetPlayOutEntityEquipment;
    }

    public static Constructor<?> getPacketPlayOutEntityDestroy() {
        return packetPlayOutEntityDestroy;
    }

    public static Constructor<?> getPacketPlayOutEntityMetadata() {
        return packetPlayOutEntityMetadata;
    }

    public static Constructor<?> getPacketPlayOutEntityHeadRotation() {
        return packetPlayOutEntityHeadRotation;
    }

    public static Constructor<?> getPacketPlayOutEntityLook() {
        return packetPlayOutEntityLook;
    }

    public static Constructor<?> getPacketPlayOutAnimation() {
        return packetPlayOutAnimation;
    }

    public static Constructor<?> getPacketPlayOutNamedEntitySpawn() {
        return packetPlayOutNamedEntitySpawn;
    }

    public static Constructor<?> getPacketPlayOutMount() {
        return packetPlayOutMount;
    }

    public static Constructor<?> getPacketPlayOutEntityStatus() {
        return packetPlayOutEntityStatus;
    }

    public static Method setLocation() {
        return setLocation;
    }

    public static Method setInvisible() {
        return setInvisible;
    }

    public static Method setCustomName() {
        return setCustomName;
    }

    public static Method setCustomNameVisible() {
        return setCustomNameVisible;
    }

    public static Method getDataWatcher() {
        return getDataWatcher;
    }

    public static Method watch() {
        return watch;
    }

    public static Method update() {
        return update;
    }

    public static Method setItemStack() {
        return setItemStack;
    }

    public static Method setPassenger() {
        return setPassenger;
    }

    public static Method stopRiding() {
        return stopRiding;
    }

    public static Method setBaby() {
        return setBaby;
    }

    public static Method setVariant() {
        return setVariant;
    }
}
