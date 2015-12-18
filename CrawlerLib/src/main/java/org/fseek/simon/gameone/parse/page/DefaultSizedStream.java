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
package org.fseek.simon.gameone.parse.page;

import java.util.Collection;
import java.util.stream.Stream;

public class DefaultSizedStream<T> implements SizedStream<T> {
    private final Stream<T> items;
    private final int size;

    public DefaultSizedStream(Collection<T> items) {
        this(items.stream(), items.size());
    }

    public DefaultSizedStream(Stream<T> items, int size) {
        this.items = items;
        this.size = size;
    }

    @Override
    public Stream<T> stream() {
        return items;
    }

    @Override
    public int size() {
        return size;
    }
}
