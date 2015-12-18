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
package org.fseek.simon.gameone.tv.overview;

import java.net.URL;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public class TVOverviewParser extends AbstractPageParser<TVOverviewEntry> {
    private static final String DEFAULT_START_PAGE = PageConstants.TV_URL;
    private static final String PAGED_URL = DEFAULT_START_PAGE + "?year=%s";

    private static final String ARCHIVE_ID = "archive";
    private static final String ACTIVE_SELECTOR = ".active";
    private static final String EPISODES_CLASS = "episodes";
    private static final String THUMBNAIL_LINK = "image_link";
    private static final String DATA_SRC_ATTRIBUTE = "data-src";
    private static final String DESCRIPTION_CLASS = "desc";
    private static final String ASIDE_CLASS = "aside";
    private static final String VIEWS_CLASS = "views";
    private static final String RATED_CLASS = "rated";
    private static final String RATING_CLASS = "rating";

    private static final Pattern VIEWS_PATTERN = Pattern.compile("Views: (.*)");

    @Override
    public TVOverviewEntry parseEntry(Element entry) throws ParseException {
        Element thumbnailImage = JsoupUtil.getElementByTag(JsoupUtil.getElementByClass(entry, THUMBNAIL_LINK),
                HTMLConstants.IMAGE_TAG);
        URL thumbnailURL = JsoupUtil.url(thumbnailImage, DATA_SRC_ATTRIBUTE);
        Element headlineElement = JsoupUtil.getElementByTag(JsoupUtil.getElementByTag(entry, HTMLConstants.H5_TAG),
                HTMLConstants.A_TAG);
        URL postURL = JsoupUtil.url(headlineElement, HTMLConstants.HREF_ATTRIBUTE);
        String headline = headlineElement.text();
        String description = JsoupUtil.getElementByClass(entry, DESCRIPTION_CLASS).text();

        Element aside = JsoupUtil.getElementByClass(entry, ASIDE_CLASS);
        String viewsText = JsoupUtil.getElementByClass(aside, VIEWS_CLASS).text();
        Matcher viewsMatcher = VIEWS_PATTERN.matcher(viewsText);
        if (!viewsMatcher.matches())
            throw ErrorUtil.parseError(String.format("Views text '%s' does not match pattern!", viewsText));
        int views;
        float rating;
        try {
            views = Integer.parseInt(viewsMatcher.group(1));
            rating = Float.parseFloat(JsoupUtil
                    .getElementByClass(JsoupUtil.getElementByClass(aside, RATED_CLASS), RATING_CLASS).attr("rel"));
        } catch (NumberFormatException ex) {
            throw ErrorUtil.parseError(ex);
        }
        // -1 ratins count - the info is theoretically available but it seems
        // broken currently only NaN is shown in the HTML
        // <span class="star-rating-result"><small>Durchschnitt:
        // <small>4.8</small> (NaN Bewertungen)</small></span>
        return new TVOverviewEntry(postURL, headline, description, views, -1, rating, thumbnailURL);
    }

    @Override
    public URL getStartPage() throws ParseException {
        return JsoupUtil.url(DEFAULT_START_PAGE);
    }

    @Override
    protected Element getEntryContainer(Document page) throws ParseException {
        return JsoupUtil.getElementById(page, ARCHIVE_ID);
    }

    @Override
    protected Elements getEntryElements(Element container) throws ParseException {
        Element episodesList = JsoupUtil.getElementByClass(container, EPISODES_CLASS);
        return episodesList.getElementsByTag(HTMLConstants.LIST_ITEM_TAG);
    }

    @Override
    protected Optional<URL> getNextPage(Element container) throws ParseException {
        Element nextPage = JsoupUtil.first(getPagination(container).select(ACTIVE_SELECTOR)).nextElementSibling();
        if (!nextPage.tagName().equals(HTMLConstants.H4_TAG))
            nextPage = null;
        return nextPage == null ? Optional.empty()
                : Optional.of(JsoupUtil.url(JsoupUtil.getElementByTag(nextPage, HTMLConstants.A_TAG),
                        HTMLConstants.HREF_ATTRIBUTE));
    }

    @Override
    public SizedStream<URL> getPages(Document startPage) throws ParseException {
        Elements pagination = getPagination(getEntryContainer(startPage));
        return new DefaultSizedStream<>(pagination.stream().map(ErrorUtil.rethrow(el -> {
            return JsoupUtil.url(String.format(PAGED_URL, el.text()));
        })), pagination.size());
    }

    protected Elements getPagination(Element container) {
        return container.getElementsByTag(HTMLConstants.H4_TAG);
    }
}
