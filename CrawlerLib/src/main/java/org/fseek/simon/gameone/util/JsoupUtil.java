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
package org.fseek.simon.gameone.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;

import org.fseek.simon.gameone.constants.HTMLConstants;
import org.fseek.simon.gameone.parse.ParseException;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class JsoupUtil {
    private static final String ID_ATTRIBUTE = HTMLConstants.ID_ATTRIBUTE;
    private static final String ID_ATTRIBUTE_SEPERATOR = "_";
    private static final String ABSOLUTE_ATTRIBUTE_PREFIX = "abs:";

    public static Element getElementById(Element root, String id) throws ParseException {
        return get(oGetElementById(root, id),
                () -> ErrorUtil.parseError(String.format("Element with id '%s' not found!", id)));
    }

    public static Optional<Element> oGetElementById(Element root, String id) {
        return Optional.ofNullable(root.getElementById(id));
    }

    public static Element getElementByClass(Element root, String... className) throws ParseException {
        String[] classNames = (String[]) className;
        return get(oGetElementByClass(root, classNames),
                () -> ErrorUtil.parseError(String.format("No %s element found!", Arrays.toString(classNames))));
    }

    public static Optional<Element> oGetElementByClass(Element root, String... className) throws ParseException {
        Check.requireNonNull((Object[]) className);
        if (className.length <= 0)
            throw ErrorUtil.parseError("You have to specify minimum one class name !");
        if (className.length == 1) {
            return oFirst(root.getElementsByClass(className[0]));
        } else {
            StringBuilder genQuery = new StringBuilder();
            for (String s : className) {
                genQuery.append(".").append(s);
            }
            return oFirst(root.select(genQuery.toString()));
        }
    }

    public static Element getElementByTag(Element root, String tag) throws ParseException {
        return get(oGetElementByTag(root, tag), () -> ErrorUtil.parseError(String.format("No %s element found!", tag)));
    }

    public static Optional<Element> oGetElementByTag(Element root, String tag) {
        return oFirst(root.getElementsByTag(tag));
    }

    public static Element firstChild(Element root) throws ParseException {
        return first(root.children());
    }

    public static Element first(Elements elements) throws ParseException {
        return get(oFirst(elements), () -> ErrorUtil.parseError("Elements list is empty!"));
    }

    public static <T> T get(Optional<T> optional, Supplier<ParseException> exception) throws ParseException {
        if (!optional.isPresent())
            throw exception.get();

        return optional.get();
    }

    public static Optional<Element> oFirst(Elements elements) {
        if (elements.size() <= 0)
            return Optional.empty();
        Element element = elements.first();
        if (element == null)
            return Optional.empty();
        return Optional.of(element);
    }

    public static String attr(Element element, String attributeName) throws ParseException {
        return get(oAttr(element, attributeName),
                () -> ErrorUtil.parseError(String.format("Element has no %s attribute!", attributeName)));
    }

    public static Optional<String> oAttr(Element element, String attributeName) {
        String attribute = element.attr(attributeName);
        if (attribute.isEmpty())
            return Optional.empty();
        return Optional.of(attribute);
    }

    public static Optional<URL> oUrl(String absolute) {
        try {
            return Optional.of(new URL(absolute));
        } catch (MalformedURLException ex) {
            return Optional.empty();
        }
    }

    public static URL url(String absolute) throws ParseException {
        try {
            return new URL(absolute);
        } catch (MalformedURLException ex) {
            throw ErrorUtil.parseError(ex);
        }
    }

    public static URL url(Element element, String attribute) throws ParseException {
        String absolute = JsoupUtil.attr(element, ABSOLUTE_ATTRIBUTE_PREFIX + attribute);
        return JsoupUtil.url(absolute);
    }

    public static Optional<URL> oUrl(Optional<Element> element, String attribute) throws ParseException {
        if (!element.isPresent())
            return Optional.empty();
        return oUrl(element.get(), attribute);
    }

    public static Optional<URL> oUrl(Element element, String attribute) throws ParseException {
        Optional<String> absolute = JsoupUtil.oAttr(element, ABSOLUTE_ATTRIBUTE_PREFIX + attribute);
        if (!absolute.isPresent())
            return Optional.empty();
        return Optional.of(JsoupUtil.url(absolute.get()));
    }

    public static URL absUrl(Element element, String attribute) throws ParseException {
        String relative = JsoupUtil.attr(element, attribute);
        String absolute = element.absUrl(relative);
        return JsoupUtil.url(absolute);
    }

    public static int getId(Element element) throws ParseException {
        String idString = attr(element, ID_ATTRIBUTE);
        return parseId(idString);
    }

    public static int parseId(String idString) throws ParseException {
        int id;
        try {
            String[] split = idString.split(ID_ATTRIBUTE_SEPERATOR);
            if (split.length != 2)
                throw ErrorUtil.parseError("Id string format is invalid!");
            id = Integer.parseInt(split[1]);
        } catch (NumberFormatException ex) {
            throw ErrorUtil.parseError("Id string format is invalid!", ex);
        }
        return id;
    }
}
