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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fseek.simon.gameone.util.Check;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Pages<T> {
    private final transient CachedStream<Page<T>> stream;

    public Pages(SizedStream<Page<T>> pages) {
        Check.requireNonNull(pages);
        this.stream = new CachedStream<>(pages);
    }

    public Pages(List<Page<T>> pages) {
        Check.requireNonNull(pages);
        this.stream = new CachedStream<>(pages);
    }

    public Pages(Pages<T> overview) {
        this.stream = overview.stream;
    }

    public Pages<T> load() {
        // ensure list is loaded
        getPages().stream().forEach((p) -> {
            p.load();
        });
        return this;
    }

    public Stream<Page<T>> pageStream() {
        return this.stream.stream();
    }

    public Stream<T> entryStream() {
        return pageStream().flatMap(p -> p.entryStream());
    }

    public List<Page<T>> getPages() {
        return this.stream.getList();
    }

    @JsonIgnore
    public List<T> getEntries() {
        return getPages().stream().flatMap(page -> page.getEntries().stream()).collect(Collectors.toList());
    }

    public int getEntryCount() {
        return getEntries().size();
    }
}
