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

package de.alphahelix.alphalibary.fakeapi.instances;

import org.bukkit.Location;

class FakeEntity {

    private Location startLocation;
    private Location currentlocation;
    private String name;
    private Object nmsEntity;

    public FakeEntity(Location location, String name, Object nmsEntity) {
        this.startLocation = location;
        this.currentlocation = location;
        this.name = name;
        this.nmsEntity = nmsEntity;
    }


    /**
     * Gets the {@link Location} where the {@link FakeEntity} currently is
     *
     * @return the current {@link Location} of the {@link FakeEntity}
     */
    public Location getCurrentlocation() {
        return currentlocation;
    }

    /**
     * Sets the {@link Location} of the {@link FakeEntity}
     *
     * @param currentlocation the new {@link Location} of the {@link FakeEntity}
     */
    public void setCurrentlocation(Location currentlocation) {
        this.currentlocation = currentlocation;
    }

    /**
     * Gets the name of the {@link FakeEntity} which is saved inside the File
     *
     * @return the name of the {@link FakeEntity} inside the file
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the NMS Class of the {@link FakeEntity} which is saved inside the File
     *
     * @return the instance of the NMS Class of the {@link FakeEntity}
     */
    public Object getNmsEntity() {
        return nmsEntity;
    }

    /**
     * Gets the {@link Location} of the {@link FakeEntity} where it was spawned
     *
     * @return the {@link Location} where the {@link FakeEntity} was spawned
     */
    public Location getStartLocation() {
        return startLocation;
    }

    /**
     * Converts the {@link FakeEntity} to a String
     *
     * @return the serialized {@link FakeEntity}
     */
    @Override
    public String toString() {
        return "Name = (" + getName() + ") , CurrentLocation = (x[" + getCurrentlocation().getBlockX() + "] ; y[" + getCurrentlocation().getBlockY() + "] ; z[" + getCurrentlocation().getBlockZ() + "]) , StartLocation = (x[" + getStartLocation().getBlockX() + "] ; y[" + getStartLocation().getBlockY() + "] ; z[" + getStartLocation().getBlockZ() + "])";
    }
}
