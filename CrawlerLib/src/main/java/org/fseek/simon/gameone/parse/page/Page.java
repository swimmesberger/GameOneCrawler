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

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.fseek.simon.gameone.util.Check;

public class Page<T> implements Pageable {
    private final URL page;
    private final transient CachedStream<T> entries;
    private final Optional<URL> nextPage;

    public Page(URL page, SizedStream<T> entries, Optional<URL> nextPage) {
        Check.requireNonNull(page, entries, nextPage);
        this.page = page;
        this.entries = new CachedStream<>(entries);
        this.nextPage = nextPage;
    }

    public Page(URL page, List<T> entriesList, Optional<URL> nextPage) {
        this.page = page;
        this.entries = new CachedStream<>(entriesList);
        this.nextPage = nextPage;
    }

    public Page(Page<T> page) {
        this.page = page.page;
        this.entries = page.entries;
        this.nextPage = page.nextPage;
    }

    protected Page<T> load() {
        this.entries.cache();
        return this;
    }

    public URL getPage() {
        return page;
    }

    public Stream<T> entryStream() {
        return this.entries.stream();
    }

    public List<T> getEntries() {
        return this.entries.getList();
    }

    public int getEntryCount() {
        return this.entries.size();
    }

    @Override
    public Optional<URL> getNextPage() {
        return nextPage;
    }
}
