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
package org.fseek.simon.gameone.blog.post;

import java.net.URL;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.fseek.simon.gameone.constants.HTMLConstants;
import org.fseek.simon.gameone.constants.PageConstants;
import org.fseek.simon.gameone.parse.ParseException;
import org.fseek.simon.gameone.util.ErrorUtil;
import org.fseek.simon.gameone.util.JsoupUtil;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PostMetaParser {
    private static final String AUTHOR_IMAGE_SELECTOR = ".img > img";

    private static final String DATE_GROUP_NAME = "DATE";
    // Dieser Eintrag wurde am\nFreitag, 11. Mai 2012, 11:00
    // Uhr\nveröffentlicht.
    private static final Pattern PUBLISHED_DATE_PATTERN = Pattern.compile(
            "Dieser Eintrag wurde am[\\n\\r ](?<" + DATE_GROUP_NAME + ">.*)[\\n\\r ]Uhr[\\n\\r ]veröffentlicht\\.",
            Pattern.CASE_INSENSITIVE);

    private static final DateTimeFormatter PUBLISHED_DATE_FORMATTER = PageConstants.PUBLISH_DATE_FORMATTER;

    public PostMeta parseMeta(Element meta) throws ParseException {
        Element tbodyElement = JsoupUtil.getElementByTag(meta, HTMLConstants.TBODY_TAG);
        Elements rows = tbodyElement.children();
        Element authorRow = rows.get(0);
        Element authorImage = JsoupUtil.first(authorRow.select(AUTHOR_IMAGE_SELECTOR));

        URL authorImageURL = JsoupUtil.url(authorImage, HTMLConstants.SRC_ATTRIBUTE);
        String authorName = JsoupUtil.getElementByTag(authorRow.child(2), HTMLConstants.A_TAG).text();

        Element tagsColumn = rows.get(1).child(1);
        List<String> tags = getLinkTextChilds(tagsColumn);

        Element categoriesColumn = rows.get(2).child(1);
        List<String> categories = getLinkTextChilds(categoriesColumn);

        Element dateElement = rows.get(3).child(0);
        String dateText = dateElement.text();
        Matcher dateMatcher = PUBLISHED_DATE_PATTERN.matcher(dateText);
        if (!dateMatcher.matches())
            throw ErrorUtil.parseError(String.format("%s does not match published date pattern!", dateText));
        String dateSingle = dateMatcher.group(DATE_GROUP_NAME);
        ZonedDateTime publishedDate = ZonedDateTime.parse(dateSingle, PUBLISHED_DATE_FORMATTER);

        return new PostMeta(authorName, authorImageURL, tags, categories, publishedDate);
    }

    protected List<String> getLinkTextChilds(Element element) {
        return element.getElementsByTag(HTMLConstants.A_TAG).stream().map(el -> el.text()).collect(Collectors.toList());
    }
}
