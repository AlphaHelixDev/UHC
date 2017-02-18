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
package de.alphahelix.alphalibary.reflection;

import java.lang.reflect.Method;

public class SaveMethod {

    private Method m;

    public SaveMethod(Method m) {
        try {
            m.setAccessible(true);
            this.m = m;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object invoke(Object instance, Boolean stackTrace, Object... args) {
        try {
            return m.invoke(instance, args);
        } catch (Exception e) {
            if (stackTrace) e.printStackTrace();
        }
        return null;
    }

}
