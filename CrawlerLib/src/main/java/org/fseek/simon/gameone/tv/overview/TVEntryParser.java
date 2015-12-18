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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fseek.simon.gameone.blog.video.Video;
import org.fseek.simon.gameone.blog.video.VideoParser;
import org.fseek.simon.gameone.constants.HTMLConstants;
import org.fseek.simon.gameone.parse.ParseException;
import org.fseek.simon.gameone.parse.URLParser;
import org.fseek.simon.gameone.parse.full.DocumentParser;
import org.fseek.simon.gameone.util.ErrorUtil;
import org.fseek.simon.gameone.util.JsoupUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;

public class TVEntryParser implements DocumentParser<TVEntry> {
    private static final String MAIN_COLUMN_ID = "show-site";
    private static final String SHOW_VIDEO_ID = "show-video";
    private static final String SHOW_SIDEBAR_ID = "show-sidebar";
    private static final String SHOW_DESCRIPTION_ID = "long_description";

    private static final String META_CLASS = "meta";
    private static final String RATING_CLASS = "rating";
    private static final String VIEWS_CLASS = "views";

    private static final String RATING_ATTRIBUTE = "rel";
    private static final String RATING_COUNT_ATTRIBUTE = "rev";

    private static final String EPISODE_PATTERN_KEY = "EPISODE";
    private static final String DATE_PATTERN_KEY = "DATE";
    // e.g. Folge 307 vom 24.12.2014
    private static final Pattern HEADLINE_META_PATTERN = Pattern
            .compile("Folge (?<" + EPISODE_PATTERN_KEY + ">\\d*) vom (?<" + DATE_PATTERN_KEY + ">[.\\d]*)");

    public static final String EPISODE_DATE_PATTENR = "dd.MM.yyyy";
    public static final DateTimeFormatter EPISODE_DATE_FORMATTER = DateTimeFormatter.ofPattern(EPISODE_DATE_PATTENR);

    private final VideoParser videoParser;

    public TVEntryParser() {
        this.videoParser = new VideoParser();
    }

    public TVEntry parse(URL url, URLParser parser) throws ParseException {
        return parse(parser.parse(url), parser);
    }

    public TVEntry parse(Document document, URLParser parser) throws ParseException {
        Element mainColumn = getMainColumn(document);
        return parse(mainColumn, parser);
    }

    public TVEntry parse(Element mainColumn, URLParser parser) throws ParseException {
        Element showVideo = JsoupUtil.getElementById(mainColumn, SHOW_VIDEO_ID);
        Element showSidebar = JsoupUtil.getElementById(mainColumn, SHOW_SIDEBAR_ID);
        Element showDesc = JsoupUtil.getElementById(showSidebar, SHOW_DESCRIPTION_ID);
        Element metaElement = JsoupUtil.getElementByClass(mainColumn, META_CLASS);
        Element ratingElement = JsoupUtil.getElementByClass(metaElement, RATING_CLASS);
        Element viewsElement = JsoupUtil.getElementByClass(metaElement, VIEWS_CLASS);

        URL postURL = JsoupUtil.url(mainColumn.baseUri());
        String headline = JsoupUtil.getElementByTag(showVideo, HTMLConstants.H2_TAG).text();
        Optional<Element> descrElement = JsoupUtil.oGetElementByTag(showDesc, HTMLConstants.P_TAG);
        String description;
        if (descrElement.isPresent()) {
            description = descrElement.get().text();
        } else {
            List<TextNode> textNodes = showDesc.textNodes();
            description = textNodes.get(textNodes.size() - 1).text();
        }

        List<Video> parseVideo = getVideoParser().find(showVideo, parser);
        if (parseVideo.size() <= 0)
            throw ErrorUtil.parseError("Video page without a video!");
        if (parseVideo.size() > 1)
            throw ErrorUtil.parseError("More than one video found on tv page?!");
        Video show = parseVideo.iterator().next();

        float rating = parseFloat(JsoupUtil.attr(ratingElement, RATING_ATTRIBUTE));
        int ratingCount = parseInt(JsoupUtil.attr(ratingElement, RATING_COUNT_ATTRIBUTE));
        int views = parseInt(JsoupUtil.getElementByTag(viewsElement, HTMLConstants.SPAN_TAG).text());

        Matcher headlineMatcher = HEADLINE_META_PATTERN.matcher(headline);
        if (!headlineMatcher.matches())
            throw ErrorUtil.parseError(String.format("TV page headline '%s' does not match pattern!", headline));
        int episode = parseInt(headlineMatcher.group(EPISODE_PATTERN_KEY));
        LocalDate date = LocalDate.parse(headlineMatcher.group(DATE_PATTERN_KEY), EPISODE_DATE_FORMATTER);

        return new TVEntry(postURL, headline, description, views, ratingCount, rating, date, episode, show);
    }

    protected Element getMainColumn(Document document) throws ParseException {
        return JsoupUtil.getElementById(document, MAIN_COLUMN_ID);
    }

    protected VideoParser getVideoParser() {
        return videoParser;
    }

    // wrap NumberFormatException
    protected float parseFloat(String s) throws ParseException {
        try {
            return Float.parseFloat(s);
        } catch (NumberFormatException ex) {
            throw new ParseException(ex);
        }
    }

    // wrap NumberFormatException
    protected int parseInt(String s) throws ParseException {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException ex) {
            throw new ParseException(ex);
        }
    }
}
