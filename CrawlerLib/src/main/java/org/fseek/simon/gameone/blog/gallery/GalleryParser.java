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
package org.fseek.simon.gameone.blog.gallery;

import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import org.fseek.simon.gameone.constants.HTMLConstants;
import org.fseek.simon.gameone.parse.ParseException;
import org.fseek.simon.gameone.util.ErrorUtil;
import org.fseek.simon.gameone.util.JsoupUtil;
import org.jsoup.nodes.Element;

public class GalleryParser {
    private static final String GALLERY_CLASS = "gallery";
    private static final String GALLERY_IMAGE_CLASS = "gallery_image";
    private static final String GALLERY_IMAGE_CAPTION_CLASS = "caption";

    public List<Gallery> findGalleries(Element postPart) throws ParseException {
        List<Gallery> galleries = postPart.getElementsByClass(GALLERY_CLASS).stream()
                .map(ErrorUtil.rethrow(this::parse)).collect(Collectors.toList());
        return galleries;
    }

    public Gallery parse(Element gallery) throws ParseException {
        List<GalleryImage> images = gallery.getElementsByClass(GALLERY_IMAGE_CLASS).stream()
                .map(ErrorUtil.rethrow(this::parseGalleryImage)).collect(Collectors.toList());
        int id = JsoupUtil.getId(gallery);
        return new Gallery(id, images);
    }

    public GalleryImage parseGalleryImage(Element galleryImage) throws ParseException {
        String caption = JsoupUtil.getElementByClass(galleryImage, GALLERY_IMAGE_CAPTION_CLASS).text();
        URL imageURL = JsoupUtil.url(JsoupUtil.getElementByTag(galleryImage, HTMLConstants.A_TAG),
                HTMLConstants.HREF_ATTRIBUTE);
        return new GalleryImage(caption, imageURL);
    }

    /**
     * Find all image URLs in the passed post element which are not in a
     * gallery.
     * 
     * @param postPart
     * @return
     */
    public List<URL> findImages(Element postPart) {
        return postPart.select(HTMLConstants.IMAGE_TAG).stream().filter((Element el) -> {
            return !el.parents().stream().anyMatch(parent -> parent.hasClass(GALLERY_CLASS));
        }).map(ErrorUtil.rethrow((Element el) -> {
            return JsoupUtil.url(el, HTMLConstants.SRC_ATTRIBUTE);
        })).collect(Collectors.toList());
    }
}
