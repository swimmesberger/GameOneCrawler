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
package org.fseek.simon.gameone.parse.full;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fseek.simon.gameone.parse.MediaOfflineException;
import org.fseek.simon.gameone.parse.ParseException;
import org.fseek.simon.gameone.parse.URLParser;
import org.fseek.simon.gameone.parse.page.Page;
import org.fseek.simon.gameone.parse.page.PageParser;
import org.fseek.simon.gameone.parse.page.Pages;
import org.fseek.simon.gameone.util.Check;
import org.fseek.simon.gameone.util.JsoupUtil;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @param <T>
 *            OverviewEntry type
 * @param <E>
 *            Entry type
 * @param <R>
 *            Return type including overview entries and parsed entries
 */
public abstract class AbstractFullPageParser<T extends OverviewEntry, E, R extends FullPage<T, E>> {
    private static final Logger logger = LoggerFactory.getLogger(AbstractFullPageParser.class);

    private final PageParser<T> overviewParser;
    private final DocumentParser<E> documentParser;

    public AbstractFullPageParser(PageParser<T> overviewParser, DocumentParser<E> documentParser) {
        this.overviewParser = overviewParser;
        this.documentParser = documentParser;
    }

    public R parse(URLParser parser) throws ParseException {
        return parse(parser, Optional.empty());
    }

    public R parse(URLParser parser, Optional<FullPageParseListener<E>> listener) throws ParseException {
        Check.requireNonNull(parser, listener);
        final List<Page<T>> pages = Collections.synchronizedList(new ArrayList<>());
        final List<CompletableFuture<List<E>>> postFutures = Collections.synchronizedList(new ArrayList<>());

        long startTime = System.nanoTime();
        try (Stream<Page<T>> pageStream = getOverviewParser().lazy(parser).pageStream()) {
            pageStream.parallel().forEach(page -> {
                pages.add(page);
                // schedule post parsing asynchronous so we can continue parsing
                // pages
                postFutures.add(CompletableFuture.supplyAsync(() -> {
                    try (Stream<T> entryStream = page.entryStream()) {
                        return entryStream.parallel().map(entry -> {
                            URL url = parsePageEntry(entry, parser, listener);
                            Document document = parseURL(url, parser, listener);
                            return parseDocument(document, parser, listener);
                        }).collect(Collectors.toList());
                    }
                }));
            });
        }
        // wait for all post parsings to complete
        List<E> parsedEntries = postFutures.stream().flatMap(postFuture -> {
            // filter null (errornous) values
            return postFuture.join().stream().filter(entry -> entry != null);
        }).collect(Collectors.toList());

        logger.info(
                "Parsing full page took: " + TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + " ms");
        return create(new Pages<>(pages), parsedEntries);
    }

    protected URL parsePageEntry(T entry, URLParser parser, Optional<FullPageParseListener<E>> listener) {
        return entry.getEntryURL();
    }

    protected Document parseURL(URL url, URLParser parser, Optional<FullPageParseListener<E>> listener) {
        try {
            logger.info("Working on entry: " + url.getFile());
            Document entryPage = parser.parse(url);
            return entryPage;
        } catch (ParseException ex) {
            logger.warn("Failed to parse entry: " + url, ex);
            if (listener.isPresent())
                listener.get().onEntryFailed(url, ex);
            return null;
        }
    }

    protected E parseDocument(Document document, URLParser parser, Optional<FullPageParseListener<E>> listener) {
        if (document == null)
            return null;
        String uri = document.baseUri();
        URL url = null;
        try {
            url = JsoupUtil.url(uri);
            E parsedEntry = documentParser.parse(document, parser);
            Objects.requireNonNull(parsedEntry);
            if (listener.isPresent())
                listener.get().onEntry(parsedEntry);
            logger.info("Finished entry: " + url.getFile());
            return parsedEntry;
        } catch (MediaOfflineException ex) {
            logger.info("Entry offline: " + uri, ex);
            if (listener.isPresent())
                listener.get().onEntryOffline(url);
        } catch (ParseException ex) {
            logger.warn("Failed to parse entry: " + uri, ex);
            if (listener.isPresent())
                listener.get().onEntryFailed(url, ex);
        } catch (Throwable ex) {
            logger.error(ex.getMessage(), ex);
            if (listener.isPresent())
                listener.get().onEntryFailed(url, ex);
        }
        return null;
    }

    protected abstract R create(Pages<T> pages, List<E> entries);

    protected PageParser<T> getOverviewParser() {
        return overviewParser;
    }

    protected DocumentParser<E> getDocumentParser() {
        return documentParser;
    }

    public static interface FullPageParseListener<E> {
        public void onEntry(E entry);

        public void onEntryFailed(URL postURL, Throwable ex);

        public void onEntryOffline(URL postURL);
    }
}
