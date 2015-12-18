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

import java.util.ArrayList;
import java.util.List;

import org.fseek.simon.gameone.constants.HTMLConstants;
import org.fseek.simon.gameone.parse.ParseException;
import org.fseek.simon.gameone.parse.URLParser;
import org.fseek.simon.gameone.parse.full.DocumentParser;
import org.fseek.simon.gameone.util.JsoupUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class PostParser implements DocumentParser<Post> {
    private static final String POST_META_CLASS = "post_meta";
    private static final String[] MAIN_COLUMN_CLASS = new String[] { "main", "col" };
    private static final String[] POST_CLASS = new String[] { "post", "single" };

    private final PostPartParser partParser;
    private final PostMetaParser metaParser;

    public PostParser() {
        this.partParser = new PostPartParser();
        this.metaParser = new PostMetaParser();
    }

    public Post parse(Document document, URLParser parser) throws ParseException {
        Element mainColumn = getMainColumn(document);
        return parse(mainColumn, parser);
    }

    public Post parse(Element mainColumn, URLParser parser) throws ParseException {
        String headline = JsoupUtil.getElementByTag(mainColumn, HTMLConstants.H2_TAG).text();
        List<PostPart> parts = findParts(getPostPartElement(mainColumn), parser);
        PostMeta meta = getMetaParser().parseMeta(getPostMetaElement(mainColumn));
        return new Post(headline, parts, meta);
    }

    public PostPart parsePart(Element postPartElement, URLParser parser) throws ParseException {
        return getPartParser().parsePart(postPartElement, parser);
    }

    public List<PostPart> findParts(Element postPartElement, URLParser parser) throws ParseException {
        List<PostPart> parts = new ArrayList<>();
        while (postPartElement != null) {
            PostPart part = parsePart(postPartElement, parser);
            parts.add(part);
            postPartElement = null;
            if (part.getNextPost().isPresent()) {
                Document nextPostDocument = parser.parse(part.getNextPost().get());
                postPartElement = getPostPartElement(nextPostDocument);
            }
        }
        return parts;
    }

    protected Element getMainColumn(Document document) throws ParseException {
        return JsoupUtil.getElementByClass(document, MAIN_COLUMN_CLASS);
    }

    protected Element getPostPartElement(Document document) throws ParseException {
        return getPostPartElement(getMainColumn(document));
    }

    protected Element getPostPartElement(Element mainColumn) throws ParseException {
        return JsoupUtil.getElementByClass(mainColumn, POST_CLASS);
    }

    protected Element getPostMetaElement(Element mainColumn) throws ParseException {
        return JsoupUtil.getElementByClass(mainColumn, POST_META_CLASS);
    }

    public PostPartParser getPartParser() {
        return partParser;
    }

    public PostMetaParser getMetaParser() {
        return metaParser;
    }
}
