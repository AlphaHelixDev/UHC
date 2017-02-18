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

package de.alphahelix.alphalibary.fakeapi.utils;

import com.mojang.authlib.GameProfile;
import de.alphahelix.alphalibary.UUID.UUIDFetcher;
import de.alphahelix.alphalibary.fakeapi.FakeAPI;
import de.alphahelix.alphalibary.fakeapi.Register;
import de.alphahelix.alphalibary.fakeapi.instances.FakePlayer;
import de.alphahelix.alphalibary.fakeapi.utils.intern.EquipSlot;
import de.alphahelix.alphalibary.fakeapi.utils.intern.REnumGamemode;
import de.alphahelix.alphalibary.fakeapi.utils.intern.UtilBase;
import de.alphahelix.alphalibary.item.SkullItemBuilder;
import de.alphahelix.alphalibary.nms.REnumPlayerInfoAction;
import de.alphahelix.alphalibary.reflection.PacketUtil;
import de.alphahelix.alphalibary.reflection.ReflectionUtil;
import de.alphahelix.alphalibary.utils.MinecraftVersion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class PlayerUtil extends UtilBase {

    private static HashMap<String, BukkitTask> followMap = new HashMap<>();
    private static HashMap<String, BukkitTask> stareMap = new HashMap<>();
    private static HashMap<String, BukkitTask> splitMap = new HashMap<>();

    private static Class<?> entityPlayerClass;
    private static Constructor<?> entitPlayer;

    private static Field listName;
    private static Field displayName;
    private static Field gameProfileName;

    static {
        try {
            entityPlayerClass = ReflectionUtil.getNmsClass("EntityPlayer");
            entitPlayer = entityPlayerClass.getConstructor(
                    ReflectionUtil.getNmsClass("MinecraftServer"),
                    ReflectionUtil.getNmsClass("WorldServer"),
                    GameProfile.class,
                    ReflectionUtil.getNmsClass("PlayerInteractManager"));

            listName = entityPlayerClass.getField("listName");
            displayName = entityPlayerClass.getField("displayName");
            gameProfileName = GameProfile.class.getDeclaredField("name");

            listName.setAccessible(true);
            displayName.setAccessible(true);
            gameProfileName.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    /**
     * Spawns in a {@link FakePlayer} for the {@link Player}
     *
     * @param p          the {@link Player} to spawn the {@link FakePlayer} for
     * @param loc        {@link Location} where the {@link FakePlayer} should be spawned at
     * @param skin       the {@link OfflinePlayer} which has the skin for the {@link FakePlayer}
     * @param customName of the {@link FakePlayer} inside the file and above his head
     * @return the new spawned {@link FakePlayer}
     */
    public static FakePlayer spawnPlayer(final Player p, Location loc, OfflinePlayer skin, String customName) {
        try {
            GameProfile gameProfile = new GameProfile(UUIDFetcher.getUUID(skin.getName()), skin.getName());

            Object npc = entitPlayer.newInstance(
                    ReflectionUtil.getMinecraftServer(Bukkit.getServer()),
                    ReflectionUtil.getWorldServer(loc.getWorld()),
                    gameProfile,
                    ReflectionUtil.getNmsClass("PlayerInteractManager")
                            .getConstructor(ReflectionUtil.getNmsClass("World"))
                            .newInstance(ReflectionUtil.getWorldServer(loc.getWorld())));

            setLocation().invoke(npc, loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());

            listName.set(npc, ReflectionUtil.serializeString(customName));
            displayName.set(npc, customName);
            gameProfileName.set(gameProfile, customName);

            Object packetPlayOutPlayerInfoAdd = PacketUtil.createPlayerInfoPacket(
                    REnumPlayerInfoAction.ADD_PLAYER.getPlayerInfoAction(),
                    gameProfile,
                    0,
                    REnumGamemode.CREATIVE.getEnumGamemode(),
                    customName);

            final Object packetPlayOutPlayerInfoRemove = PacketUtil.createPlayerInfoPacket(
                    REnumPlayerInfoAction.REMOVE_PLAYER.getPlayerInfoAction(),
                    gameProfile,
                    0,
                    REnumGamemode.CREATIVE.getEnumGamemode(),
                    customName);

            ReflectionUtil.sendPacket(p, packetPlayOutPlayerInfoAdd);
            ReflectionUtil.sendPacket(p, getPacketPlayOutNamedEntitySpawn().newInstance(npc));
            ReflectionUtil.sendPacket(p, getPacketPlayOutEntityHeadRotation().newInstance(npc, FakeAPI.toAngle(loc.getYaw())));
            ReflectionUtil.sendPacket(p, getPacketPlayOutEntityLook().newInstance(ReflectionUtil.getEntityID(npc), FakeAPI.toAngle(loc.getYaw()), FakeAPI.toAngle(loc.getPitch()), true));

            Register.getPlayerLocationsFile().addPlayerToFile(loc, customName, skin.getName());
            FakeAPI.addFakePlayer(p, new FakePlayer(loc, customName, skin, npc));

            new BukkitRunnable() {
                @Override
                public void run() {
                    ReflectionUtil.sendPacket(p, packetPlayOutPlayerInfoRemove);

                    if (ReflectionUtil.getVersion().contains("1_8")) {
                        GameProfile gameProfile = new GameProfile(UUIDFetcher.getUUID(p.getName()), p.getName());
                        ReflectionUtil.sendPacket(p,
                                PacketUtil.createPlayerInfoPacket(
                                        REnumPlayerInfoAction.ADD_PLAYER.getPlayerInfoAction(),
                                        gameProfile,
                                        0,
                                        ReflectionUtil.getEnumGamemode(p),
                                        p.getPlayerListName()));
                    }
                }
            }.runTaskLater(FakeAPI.getFakeAPI(), 20);
            return new FakePlayer(loc, customName, skin, npc);
        } catch (IllegalAccessException | NoSuchFieldException | InstantiationException | SecurityException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Spawns in a temporary {@link FakePlayer} (disappears after rejoin) for the {@link Player}
     *
     * @param p          the {@link Player} to spawn the {@link FakePlayer} for
     * @param loc        {@link Location} where the {@link FakePlayer} should be spawned at
     * @param skin       the {@link OfflinePlayer} which has the skin for the {@link FakePlayer}
     * @param customName of the {@link FakePlayer} inside the file and above his head
     * @return the new spawned {@link FakePlayer}
     */
    public static FakePlayer spawnTemporaryPlayer(final Player p, Location loc, OfflinePlayer skin, String customName) {
        try {
            GameProfile gameProfile = new GameProfile(UUIDFetcher.getUUID(skin.getName()), skin.getName());

            Object npc = entitPlayer.newInstance(
                    ReflectionUtil.getMinecraftServer(Bukkit.getServer()),
                    ReflectionUtil.getWorldServer(loc.getWorld()),
                    gameProfile,
                    ReflectionUtil.getNmsClass("PlayerInteractManager")
                            .getConstructor(ReflectionUtil.getNmsClass("World"))
                            .newInstance(ReflectionUtil.getWorldServer(loc.getWorld())));

            setLocation().invoke(npc, loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());

            listName.set(npc, ReflectionUtil.serializeString(customName));
            displayName.set(npc, customName);
            gameProfileName.set(gameProfile, customName);

            Object packetPlayOutPlayerInfoAdd = PacketUtil.createPlayerInfoPacket(
                    REnumPlayerInfoAction.ADD_PLAYER.getPlayerInfoAction(),
                    gameProfile,
                    0,
                    REnumGamemode.CREATIVE.getEnumGamemode(),
                    customName);

            final Object packetPlayOutPlayerInfoRemove = PacketUtil.createPlayerInfoPacket(
                    REnumPlayerInfoAction.REMOVE_PLAYER.getPlayerInfoAction(),
                    gameProfile,
                    0,
                    REnumGamemode.CREATIVE.getEnumGamemode(),
                    customName);

            ReflectionUtil.sendPacket(p, packetPlayOutPlayerInfoAdd);
            ReflectionUtil.sendPacket(p, getPacketPlayOutNamedEntitySpawn().newInstance(npc));
            ReflectionUtil.sendPacket(p, getPacketPlayOutEntityHeadRotation().newInstance(npc, FakeAPI.toAngle(loc.getYaw())));
            ReflectionUtil.sendPacket(p, getPacketPlayOutEntityLook().newInstance(ReflectionUtil.getEntityID(npc), FakeAPI.toAngle(loc.getYaw()), FakeAPI.toAngle(loc.getPitch()), true));

            FakeAPI.addFakePlayer(p, new FakePlayer(loc, customName, skin, npc));

            new BukkitRunnable() {
                @Override
                public void run() {
                    ReflectionUtil.sendPacket(p, packetPlayOutPlayerInfoRemove);

                    if (ReflectionUtil.getVersion().contains("1_8")) {
                        GameProfile gameProfile = new GameProfile(UUIDFetcher.getUUID(p.getName()), p.getName());
                        ReflectionUtil.sendPacket(p,
                                PacketUtil.createPlayerInfoPacket(
                                        REnumPlayerInfoAction.ADD_PLAYER.getPlayerInfoAction(),
                                        gameProfile,
                                        0,
                                        ReflectionUtil.getEnumGamemode(p),
                                        p.getPlayerListName()));
                    }
                }
            }.runTaskLater(FakeAPI.getFakeAPI(), 20);
            return new FakePlayer(loc, customName, skin, npc);
        } catch (IllegalAccessException | NoSuchFieldException | InstantiationException | SecurityException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Removes a {@link FakePlayer} for on {@link Player} from the {@link org.bukkit.World}
     *
     * @param p   the {@link Player} to destroy the {@link FakePlayer} for
     * @param npc the {@link FakePlayer} to remove
     */
    public static void removePlayer(Player p, FakePlayer npc) {
        try {
            ReflectionUtil.sendPacket(p, getPacketPlayOutEntityDestroy().newInstance(new int[]{ReflectionUtil.getEntityID(npc.getNmsEntity())}));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Moves the given {@link FakePlayer}
     *
     * @param p     the {@link Player} to move the {@link FakePlayer} for
     * @param x     blocks in x direction
     * @param y     blocks in y direction
     * @param z     blocks in z direction
     * @param yaw   new yaw
     * @param pitch new pitch
     * @param npc   the {@link FakePlayer} which should be moved
     */
    public static void movePlayer(Player p, double x, double y, double z, float yaw, float pitch, FakePlayer npc) {
        try {
            if (VERSION != MinecraftVersion.EIGHT) {
                ReflectionUtil.sendPacket(p, getPacketPlayOutRelEntityMove().newInstance(
                        ReflectionUtil.getEntityID(npc.getNmsEntity()),
                        FakeAPI.toDelta((long) x),
                        FakeAPI.toDelta((long) y),
                        FakeAPI.toDelta((long) z),
                        false));
            } else {
                ReflectionUtil.sendPacket(p, getPacketPlayOutRelEntityMove().newInstance(
                        ReflectionUtil.getEntityID(npc.getNmsEntity()),
                        ((byte) (x * 32)),
                        ((byte) (y * 32)),
                        ((byte) (z * 32)),
                        false));
            }

            ReflectionUtil.sendPacket(p, getPacketPlayOutEntityHeadRotation().newInstance(npc.getNmsEntity(), FakeAPI.toAngle(yaw)));

            ReflectionUtil.sendPacket(p, getPacketPlayOutEntityLook().newInstance(ReflectionUtil.getEntityID(npc.getNmsEntity()), FakeAPI.toAngle(yaw), FakeAPI.toAngle(pitch), true));

            npc.setCurrentlocation(npc.getCurrentlocation().add(x, y, z));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Teleports a {@link FakePlayer} to a specific {@link Location} for the given {@link Player}
     *
     * @param p   the {@link Player} to teleport the {@link FakePlayer} for
     * @param loc the {@link Location} to teleport the {@link FakePlayer} to
     * @param npc the {@link FakePlayer} which should be teleported
     */
    public static void teleportPlayer(Player p, Location loc, FakePlayer npc) {
        try {
            if (VERSION != MinecraftVersion.EIGHT) {

                Field x = ReflectionUtil.getNmsClass("Entity").getField("locX"), y = ReflectionUtil.getNmsClass("Entity").getField("locY"), z = ReflectionUtil.getNmsClass("Entity").getField("locZ"), yaw = ReflectionUtil.getNmsClass("Entity").getField("yaw"), pitch = ReflectionUtil.getNmsClass("Entity").getField("pitch");
                Object a = npc.getNmsEntity();

                x.setAccessible(true);
                y.setAccessible(true);
                z.setAccessible(true);
                yaw.setAccessible(true);
                pitch.setAccessible(true);

                x.set(a, loc.getX());
                y.set(a, loc.getY());
                z.set(a, loc.getZ());
                yaw.set(a, loc.getYaw());
                pitch.set(a, loc.getPitch());

                ReflectionUtil.sendPacket(p, getPacketPlayOutEntityTeleport().newInstance(a));
            } else {
                ReflectionUtil.sendPacket(p, getPacketPlayOutEntityTeleport().newInstance(
                        ReflectionUtil.getEntityID(npc.getNmsEntity()),
                        FakeAPI.floor(loc.getBlockX() * 32.0D),
                        FakeAPI.floor(loc.getBlockY() * 32.0D),
                        FakeAPI.floor(loc.getBlockZ() * 32.0D),
                        (byte) ((int) (loc.getYaw() * 256.0F / 360.0F)),
                        (byte) ((int) (loc.getPitch() * 256.0F / 360.0F)),
                        true));
            }
            ReflectionUtil.sendPacket(p, getPacketPlayOutEntityHeadRotation().newInstance(npc.getNmsEntity(), FakeAPI.toAngle(loc.getYaw())));

            ReflectionUtil.sendPacket(p, getPacketPlayOutEntityLook().newInstance(ReflectionUtil.getEntityID(npc.getNmsEntity()), FakeAPI.toAngle(loc.getYaw()), FakeAPI.toAngle(loc.getPitch()), true));
            FakeAPI.getFakePlayerByObject(p, npc).setCurrentlocation(loc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Equip a {@link FakePlayer} with a {@link ItemStack} for the {@link Player}
     *
     * @param p    the {@link Player} to equip the {@link FakePlayer} for
     * @param npc  the {@link FakePlayer} which should get equipped
     * @param item the {@link ItemStack} which the {@link FakePlayer} should receive
     * @param slot the {@link EquipSlot} where the {@link ItemStack} should be placed at
     */
    public static void equipPlayer(Player p, FakePlayer npc, ItemStack item, EquipSlot slot) {
        try {
            ReflectionUtil.sendPacket(p, getPacketPlayOutEntityEquipment().newInstance(
                    ReflectionUtil.getEntityID(npc.getNmsEntity()),
                    slot.getNmsSlot(),
                    ReflectionUtil.getObjectNMSItemStack(item)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Check if a {@link FakePlayer} follows a {@link Player}
     *
     * @param toCheck the {@link Player} to check if he has a {@link FakePlayer} which follows him
     * @return if the {@link Player} has a {@link FakePlayer} which followes him
     */
    public static boolean hasFollower(Player toCheck) {
        return followMap.containsKey(toCheck.getName());
    }

    /**
     * Make a {@link FakePlayer} follow a specific {@link Player}, which only the {@link Player}  can see
     *
     * @param p        the {@link Player} to see the following {@link FakePlayer}
     * @param toFollow the {@link Player} which the {@link FakePlayer} should follow
     * @param npc      the {@link FakePlayer} which should follow the {@link Player}
     */
    public static void followPlayer(final Player p, final Player toFollow, final FakePlayer npc) {
        followMap.put(p.getName(), new BukkitRunnable() {
            @Override
            public void run() {
                teleportPlayer(p, p.getLocation(), npc);
            }
        }.runTaskTimer(FakeAPI.getFakeAPI(), 0, 1));
    }

    /**
     * Make a {@link FakePlayer} unfollow his {@link Player}
     *
     * @param p the {@link Player} who shouldn't be followed anylonger
     */
    public static void unFollowPlayer(Player p) {
        if (followMap.containsKey(p.getName())) {
            followMap.get(p.getName()).cancel();
            followMap.remove(p.getName());
        }
    }

    /**
     * Make a {@link FakePlayer} look at a specific Player, which another specific {@link Player} can see
     *
     * @param p        the {@link Player} to see the following watch
     * @param toLookAt the {@link Player} to look at
     * @param npc      the {@link FakePlayer} who should watch the {@link Player}
     */
    public static void lookAtPlayer(Player p, Player toLookAt, FakePlayer npc) {
        try {
            ReflectionUtil.sendPacket(p, getPacketPlayOutEntityHeadRotation().newInstance(npc.getNmsEntity(), FakeAPI.toAngle(FakeAPI.lookAt(npc.getCurrentlocation(), toLookAt.getLocation()).getYaw())));

            ReflectionUtil.sendPacket(p, getPacketPlayOutEntityLook().newInstance(
                    ReflectionUtil.getEntityID(npc.getNmsEntity()),
                    FakeAPI.toAngle(FakeAPI.lookAt(npc.getCurrentlocation(), toLookAt.getLocation()).getYaw()),
                    FakeAPI.toAngle(FakeAPI.lookAt(npc.getCurrentlocation(), toLookAt.getLocation()).getPitch()),
                    true));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Make a {@link FakePlayer} stare at a specific Player, which another specific {@link Player} can see
     *
     * @param p         the {@link Player} to see the following watch
     * @param toStareAt the {@link Player} to stare at
     * @param npc       the {@link FakePlayer} who should stare at the {@link Player}
     */
    public static void stareAtPlayer(final Player p, final Player toStareAt, final FakePlayer npc) {
        try {
            stareMap.put(p.getName(), new BukkitRunnable() {
                @Override
                public void run() {
                    lookAtPlayer(p, toStareAt, npc);
                }
            }.runTaskTimer(FakeAPI.getFakeAPI(), 0, 1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Reset the look for all {@link FakePlayer}s which a specific {@link Player} can see
     *
     * @param p the {@link Player}
     */
    public static void normalizeLook(Player p) {
        if (stareMap.containsKey(p.getName())) {
            stareMap.get(p.getName()).cancel();
            stareMap.remove(p.getName());
        }
    }

    /**
     * Make a {@link FakePlayer} attack a {@link Player}
     *
     * @param p        the {@link Player} who can see the attack
     * @param toAttack the {@link Player} who should be attacked
     * @param npc      the {@link FakePlayer} who should attack
     * @param damage   the damage which should be done by the {@link FakePlayer}
     */
    public static void attackPlayer(Player p, Player toAttack, FakePlayer npc, double damage) {
        try {
            if (!FakeAPI.getFakePlayersInRadius(toAttack, 4).contains(npc)) return;

            lookAtPlayer(p, toAttack, npc);

            ReflectionUtil.sendPacket(p, getPacketPlayOutAnimation().newInstance(npc.getNmsEntity(), 0));

            toAttack.damage(damage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Teleport a {@link FakePlayer} to a specific {@link Location} in certain intervals, which is visible for all Players
     *
     * @param p             the {@link Player} to teleport the {@link FakePlayer} for
     * @param to            the {@link Location} where the {@link FakePlayer} should be teleported to
     * @param teleportCount the amount of teleportation that should be made
     * @param wait          the amount of time to wait 'till the next teleport starts
     * @param npc           the {@link FakePlayer} which should be teleported
     */
    public static void splitTeleportPlayer(final Player p, final Location to, final int teleportCount, final long wait, final FakePlayer npc) {
        try {
            final Location currentLocation = npc.getCurrentlocation();
            Vector between = to.toVector().subtract(currentLocation.toVector());

            final double toMoveInX = between.getX() / teleportCount;
            final double toMoveInY = between.getY() / teleportCount;
            final double toMoveInZ = between.getZ() / teleportCount;

            splitMap.put(p.getName(), new BukkitRunnable() {
                public void run() {
                    if (!FakeAPI.isSameLocation(currentLocation, to)) {
                        teleportPlayer(p, currentLocation.add(new Vector(toMoveInX, toMoveInY, toMoveInZ)), npc);
                    } else
                        this.cancel();
                }
            }.runTaskTimer(FakeAPI.getFakeAPI(), 0, wait));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Cancels all teleport tasks for the {@link Player}
     *
     * @param p the {@link Player} to cancel all teleport tasks
     */
    public static void cancelAllSplittedTasks(Player p) {
        if (splitMap.containsKey(p.getName())) {
            splitMap.get(p.getName()).cancel();
            splitMap.remove(p.getName());
        }
    }

    /**
     * Sets the head of the {@link FakePlayer} to a custom {@link org.bukkit.material.Skull} for a specific {@link Player}
     * You can use custom textures in the format of a 1.7 skin here
     *
     * @param p          the {@link Player} to show the custom Skull
     * @param mob        the {@link FakePlayer} which should get equipped
     * @param textureURL the URL where to find the plain 1.7 skin
     */
    public static void equipPlayerSkull(Player p, FakePlayer mob, String textureURL) {
        try {
            equipPlayer(p, mob, SkullItemBuilder.getSkull(textureURL), EquipSlot.HELMET);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the head of the {@link FakePlayer} to a custom {@link org.bukkit.material.Skull} for a specific {@link Player}
     * You can use custom textures in the format of a 1.7 skin here
     *
     * @param p       the {@link Player} to show the custom Skull
     * @param mob     the {@link FakePlayer} which should get equipped
     * @param profile the {@link GameProfile} of the owner of the skull
     */
    public static void equipPlayerSkull(Player p, FakePlayer mob, GameProfile profile) {
        try {
            equipPlayer(p, mob, SkullItemBuilder.getSkull(profile), EquipSlot.HELMET);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
