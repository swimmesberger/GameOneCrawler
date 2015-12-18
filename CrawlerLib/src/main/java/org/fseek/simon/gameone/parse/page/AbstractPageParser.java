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
import java.util.Optional;

import org.fseek.simon.gameone.parse.ParseException;
import org.fseek.simon.gameone.util.ErrorUtil;
import org.fseek.simon.gameone.util.JsoupUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public abstract class AbstractPageParser<T> implements PageParser<T> {
    @Override
    public Page<T> lazyPage(Document page) throws ParseException {
        return lazyPageWithElement(getEntryContainer(page));
    }

    protected Page<T> lazyPageWithElement(Element archive) throws ParseException {
        final Elements elements = getEntryElements(archive);
        SizedStream<T> stream = new DefaultSizedStream<>(elements.stream().map(ErrorUtil.rethrow(this::parseEntry)),
                elements.size());
        return new Page<>(getPageURL(archive), stream, getNextPage(archive));
    }

    protected URL getPageURL(Element container) throws ParseException {
        return JsoupUtil.url(container.baseUri());
    }

    protected abstract Element getEntryContainer(Document page) throws ParseException;

    protected abstract Elements getEntryElements(Element container) throws ParseException;

    protected abstract Optional<URL> getNextPage(Element container) throws ParseException;

    public abstract T parseEntry(Element entry) throws ParseException;
}
