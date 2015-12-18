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

import org.fseek.simon.gameone.blog.gallery.Gallery;
import org.fseek.simon.gameone.blog.video.Video;

public class PostPartMeta {
    private final List<Video> videos;
    private final List<Gallery> galleries;
    private final List<URL> images;

    public PostPartMeta(List<Video> videos, List<Gallery> galleries, List<URL> images) {
        this.videos = videos;
        this.galleries = galleries;
        this.images = images;
    }

    public List<Gallery> getGalleries() {
        return galleries;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public List<URL> getImages() {
        return images;
    }
}
