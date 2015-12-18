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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fseek.simon.gameone.util.Check;

public class CachedStream<T> {
    private final SizedStream<T> stream;
    private List<T> cacheList;

    private final AtomicBoolean streamReturned = new AtomicBoolean(false);
    private final StreamListener<T> streamListener;

    public CachedStream(SizedStream<T> entries) {
        Check.requireNonNull(entries);
        this.stream = entries;
        this.streamListener = new StreamListenerImpl(entries.size());
    }

    public CachedStream(List<T> entriesList) {
        Check.requireNonNull(entriesList);
        this.cacheList = entriesList;
        this.stream = null;
        this.streamListener = null;
    }

    public CachedStream(CachedStream<T> stream) {
        this.cacheList = stream.cacheList;
        this.stream = stream.stream;
        this.streamListener = stream.streamListener;
    }

    public Stream<T> stream() {
        if (this.cacheList != null) {
            return this.cacheList.stream();
        }
        synchronized (streamReturned) {
            Stream<T> cacheStream = createCacheStream(this.stream, this.streamListener);
            streamReturned.set(true);
            return cacheStream;
        }
    }

    public int size() {
        if (this.cacheList != null) {
            return this.cacheList.size();
        }
        return this.stream.size();
    }

    public List<T> getList() {
        if (this.cacheList == null && streamReturned.get()) {
            throw new IllegalStateException(
                    "You need to close the stream before you can get the elements in lazy mode!");
        }
        if (this.cacheList == null) {
            this.cacheList = this.stream().collect(Collectors.toList());
        }
        return this.cacheList;
    }

    public boolean isCached() {
        return this.cacheList != null;
    }

    protected CachedStream<T> cache() {
        // ensure list is loaded
        getList();
        return this;
    }

    private class StreamListenerImpl implements StreamListener<T> {
        private List<T> tmp;

        public StreamListenerImpl(int size) {
            tmp = Collections.synchronizedList(new ArrayList<>(size));
        }

        @Override
        public void onElement(T element) {
            tmp.add(element);
        }

        @Override
        public void onEnd() {
            if (CachedStream.this.cacheList == null) {
                CachedStream.this.cacheList = tmp;
            }
            tmp = null;
        }
    }

    private static <T> Stream<T> createCacheStream(SizedStream<T> iterable, final StreamListener<T> listener) {
        final long size = iterable.size();
        final Runnable onEnd = () -> {
            listener.onEnd();
        };
        final AtomicLong count = new AtomicLong(0);
        Stream<T> stream = iterable.stream().filter(e -> {
            listener.onElement(e);
            if (size >= 0 && count.incrementAndGet() >= size) {
                onEnd.run();
            }
            return true;
        });
        if (size < 0) {
            stream = stream.onClose(onEnd);
        }
        return stream;
    }

    public static interface StreamListener<T> {
        public void onElement(T element);

        public void onEnd();
    }
}
