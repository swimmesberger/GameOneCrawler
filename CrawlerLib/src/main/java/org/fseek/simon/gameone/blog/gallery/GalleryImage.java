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

import org.fseek.simon.gameone.util.Check;

/**
 * Container class containing a image in a gallery
 */
public class GalleryImage {
    private final String caption;
    // url of the image
    private final URL url;

    public GalleryImage(String caption, URL url) {
        Check.requireNonNull(caption, url);
        this.caption = caption;
        this.url = url;
    }

    public String getCaption() {
        return caption;
    }

    public URL getUrl() {
        return url;
    }
}
