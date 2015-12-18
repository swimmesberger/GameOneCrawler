/*******************************************************************************
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package org.fseek.simon.gameone.blog.video;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DefaultFlashObject implements FlashObject {
    private final Map<String, Object> variables;

    public DefaultFlashObject() {
        this.variables = new HashMap<>();
    }

    public void addVariable(String key, Object value) {
        this.variables.put(key, value);
    }

    @Override
    public Object getVariable(String key) {
        return this.variables.get(key);
    }

    @Override
    public Map<String, Object> getVariables() {
        return Collections.unmodifiableMap(variables);
    }
}
