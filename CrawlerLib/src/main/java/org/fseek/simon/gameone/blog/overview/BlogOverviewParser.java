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
package org.fseek.simon.gameone.blog.overview;

import java.net.URL;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import org.fseek.simon.gameone.constants.HTMLConstants;
import org.fseek.simon.gameone.constants.PageConstants;
import org.fseek.simon.gameone.parse.ParseException;
import org.fseek.simon.gameone.parse.page.AbstractPageParser;
import org.fseek.simon.gameone.parse.page.DefaultSizedStream;
import org.fseek.simon.gameone.parse.page.SizedStream;
import org.fseek.simon.gameone.util.ErrorUtil;
import org.fseek.simon.gameone.util.JsoupUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BlogOverviewParser extends AbstractPageParser<BlogOverviewEntry> {
    private static final String DEFAULT_START_PAGE = PageConstants.BLOG_URL;
    private static final String PAGED_URL = DEFAULT_START_PAGE + "?page=%s";

    private static final String POST_CLASS = "post";
    private static final String TEASERS_CLASS = "teasers";
    private static final String NEXT_PAGE_CLASS = "next_page";
    private static final String IMAGE_LINK_CLASS = "image_link";
    private static final String OVERLAY_CLASS = "overlay";
    private static final String DATA_SRC_ATTRIBUTE = "data-src";

    private static final String USER_REGEX_NAME = "USER";
    private static final String DATE_REGEX_NAME = "DATE";
    // Posted: mtv_team, Dienstag, 24. November 2015, 13:54 Uhr
    private static final Pattern POSTED_REGEX = Pattern
            .compile(".?Posted:\\W(?<" + USER_REGEX_NAME + ">[a-zA-Z0-9_. ]*), (?<" + DATE_REGEX_NAME + ">.*) Uhr");

    // Montag, 22. Dezember 2014, 07:25
    private static final DateTimeFormatter POSTED_DATE_FORMAT = PageConstants.PUBLISH_DATE_FORMATTER;

    @Override
    protected Element getEntryContainer(Document page) throws ParseException {
        return JsoupUtil.getElementByClass(page, TEASERS_CLASS);
    }

    @Override
    protected Elements getEntryElements(Element container) throws ParseException {
        return container.getElementsByClass(POST_CLASS);
    }

    @Override
    protected Optional<URL> getNextPage(Element container) throws ParseException {
        Optional<Element> nextPage = getNextPageElement(container);
        Optional<URL> nextPageURL = Optional.empty();
        if (nextPage.isPresent()) {
            return JsoupUtil.oUrl(nextPage, HTMLConstants.HREF_ATTRIBUTE);
        }
        return nextPageURL;
    }

    protected int getPageCount(Document page) throws ParseException {
        Optional<Element> nextPage = getNextPageElement(getEntryContainer(page));
        if (nextPage.isPresent()) {
            Element sibling = nextPage.get().previousElementSibling();
            if (sibling == null) {
                throw ErrorUtil.parseError("Next page element has no previous sibling!");
            }
            try {
                return Integer.parseInt(sibling.text());
            } catch (NumberFormatException ex) {
                throw ErrorUtil.parseError(ex);
            }
        }
        return -1;
    }

    @Override
    public SizedStream<URL> getPages(Document startPage) throws ParseException {
        final int pageCount = getPageCount(startPage);
        return new DefaultSizedStream<>(IntStream.rangeClosed(1, pageCount).mapToObj(i -> {
            try {
                return JsoupUtil.url(String.format(PAGED_URL, String.valueOf(i)));
            } catch (ParseException ex) {
                return ErrorUtil.sneakyThrow(ex);
            }
        }), pageCount);
    }

    @Override
    public BlogOverviewEntry parseEntry(Element entry) throws ParseException {
        int id = JsoupUtil.getId(entry);
        Element imageLinkElement = JsoupUtil.getElementByClass(entry, IMAGE_LINK_CLASS);
        Element imageLinkImage = JsoupUtil.getElementByTag(imageLinkElement, HTMLConstants.IMAGE_TAG);
        URL imageLink = JsoupUtil.url(imageLinkImage, DATA_SRC_ATTRIBUTE);

        Element overlayElement = JsoupUtil.getElementByClass(entry, OVERLAY_CLASS);

        Element headline = JsoupUtil.firstChild(JsoupUtil.getElementByTag(overlayElement, HTMLConstants.H3_TAG));
        URL postLink = JsoupUtil.url(headline, HTMLConstants.HREF_ATTRIBUTE);
        String headlineText = headline.text();

        Element subTitleElement = JsoupUtil.getElementByTag(overlayElement, HTMLConstants.P_TAG);
        String subTitleText = subTitleElement.text();

        Element postedElement = JsoupUtil.getElementByTag(overlayElement, HTMLConstants.SMALL_TAG);
        String postedText = postedElement.text();
        Matcher matcher = POSTED_REGEX.matcher(postedText);
        if (!matcher.matches())
            throw ErrorUtil.parseError(String.format("%s does not match posted format!", postedText));
        String user = matcher.group(USER_REGEX_NAME);
        String dateString = matcher.group(DATE_REGEX_NAME);

        ZonedDateTime postedAt = ZonedDateTime.parse(dateString, POSTED_DATE_FORMAT);

        return new BlogOverviewEntry(id, headlineText, subTitleText, user, postedAt, imageLink, postLink);
    }

    @Override
    public URL getStartPage() throws ParseException {
        return JsoupUtil.url(DEFAULT_START_PAGE);
    }

    protected Optional<Element> getNextPageElement(Element container) throws ParseException {
        return JsoupUtil.oGetElementByClass(container, NEXT_PAGE_CLASS);
    }
}
