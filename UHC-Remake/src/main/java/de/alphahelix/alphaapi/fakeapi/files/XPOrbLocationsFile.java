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

package de.alphahelix.alphaapi.fakeapi.files;

import de.alphahelix.alphaapi.fakeapi.instances.FakeXPOrb;
import de.alphahelix.alphaapi.file.SimpleFile;
import org.bukkit.Location;

import java.util.HashMap;

public class XPOrbLocationsFile extends SimpleFile {

    public XPOrbLocationsFile() {
        super("plugins/UHC-Remake/libary", "fake_xporb.yml");
    }

    /**
     * Adds a new {@link FakeXPOrb} to the file
     *
     * @param loc {@link Location} where the {@link FakeXPOrb} is located at
     * @param name of the {@link FakeXPOrb}
     */
    public void addXPOrbToFile(Location loc, String name) {
        if (!configContains(name)) {
            setLocation(name.replace(" ", "_").replace("§", "&"), loc);
        }
    }

    /**
     * Gets all {@link FakeXPOrb} from the file and returns it as a {@link HashMap}
     *
     * @return the {@link HashMap} with the name as keys and {@link Location}s as values
     */
    public HashMap<String, Location> getPacketXPOrb() {
        HashMap<String, Location> xpOrbmap = new HashMap<>();

        for (String names : getKeys(false)) {
            xpOrbmap.put(names.replace("_", " ").replace("&", "§"), getLocation(names));
        }
        return xpOrbmap;
    }
}
