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
import java.util.Collections;
import java.util.stream.Stream;

import org.fseek.simon.gameone.parse.ParseException;
import org.fseek.simon.gameone.parse.URLParser;
import org.fseek.simon.gameone.util.ErrorUtil;
import org.fseek.simon.gameone.util.ErrorUtil.FunctionException;
import org.jsoup.nodes.Document;

public interface PageParser<T> {
    public default Pages<T> parse(URLParser parser) throws ParseException {
        return parse(getStartPage(parser), parser);
    }

    public default Pages<T> lazy(URLParser parser) throws ParseException {
        return lazy(getStartPage(parser), parser);
    }

    public default Pages<T> parse(Document startPage, URLParser parser) throws ParseException {
        return lazy(startPage, parser).load();
    }

    public default Pages<T> lazy(Document startPage, URLParser parser) throws ParseException {
        return new Pages<T>(pageStream(startPage, parser));
    }

    public default SizedStream<Page<T>> pageStream(URLParser parser) throws ParseException {
        return pageStream(getStartPage(parser), parser);
    }

    public default SizedStream<Page<T>> pageStream(Document startPage, URLParser parser) throws ParseException {
        final SizedStream<URL> pages = getPages(startPage);
        final int pageCount = pages.size();
        final FunctionException<Document, Page<T>> pageParser = this::lazyPage;
        return new SizedStream<Page<T>>() {

            @Override
            public Stream<Page<T>> stream() {
                // skip first element because we reuse the startPage
                Stream<URL> urlStream = pages.stream().skip(1);
                return Stream.concat(Stream.of(startPage), urlStream.map(ErrorUtil.rethrow(url -> {
                    return parser.parse(url);
                }))).map(ErrorUtil.rethrow(pageParser));
            }

            @Override
            public int size() {
                return pageCount;
            }
        };
    }

    public default Page<T> parsePage(Document page) throws ParseException {
        return lazyPage(page).load();
    }

    public Page<T> lazyPage(Document page) throws ParseException;

    public URL getStartPage() throws ParseException;

    public default SizedStream<URL> getPages(Document startPage) throws ParseException {
        return new DefaultSizedStream<>(Collections.<URL> emptyList());
    }

    public default Document getStartPage(URLParser parser) throws ParseException {
        return parser.parse(PageParser.this.getStartPage());
    }
}
