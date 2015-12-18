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
import java.util.List;
import java.util.Optional;

import org.fseek.simon.gameone.blog.gallery.Gallery;
import org.fseek.simon.gameone.blog.gallery.GalleryParser;
import org.fseek.simon.gameone.blog.video.Video;
import org.fseek.simon.gameone.blog.video.VideoParser;
import org.fseek.simon.gameone.constants.HTMLConstants;
import org.fseek.simon.gameone.parse.ParseException;
import org.fseek.simon.gameone.parse.URLParser;
import org.fseek.simon.gameone.util.JsoupUtil;
import org.jsoup.nodes.Element;

public class PostPartParser {
    private static final String PART_NAVIGATION_CLASS = "part_navigation";
    private static final String FORWARD_NAVIGFATION_CLASS = "forwards";
    private static final String HREF_ATTRIBUTE = HTMLConstants.HREF_ATTRIBUTE;
    private static final String FORWARD_NAVIGATION_SELECTOR = "." + PART_NAVIGATION_CLASS + " > " + "."
            + FORWARD_NAVIGFATION_CLASS;

    private final GalleryParser galleryParser;
    private final VideoParser videoParser;

    public PostPartParser() {
        this.galleryParser = new GalleryParser();
        this.videoParser = new VideoParser();
    }

    public PostPart parsePart(Element postPart, URLParser parser) throws ParseException {
        URL url = JsoupUtil.url(postPart.baseUri());
        String content = postPart.html();
        PostPartMeta parsePartMeta = parsePartMeta(postPart, parser);
        Optional<URL> oUrl = JsoupUtil.oUrl(JsoupUtil.oFirst(postPart.select(FORWARD_NAVIGATION_SELECTOR)),
                HREF_ATTRIBUTE);
        return new PostPart(url, content, parsePartMeta, oUrl);
    }

    public PostPartMeta parsePartMeta(Element postPart, URLParser parser) throws ParseException {
        List<Gallery> galleries = getGalleryParser().findGalleries(postPart);
        List<Video> videos = getVideoParser().find(postPart, parser);
        List<URL> otherImages = getGalleryParser().findImages(postPart);
        return new PostPartMeta(videos, galleries, otherImages);
    }

    public GalleryParser getGalleryParser() {
        return galleryParser;
    }

    public VideoParser getVideoParser() {
        return videoParser;
    }
}
