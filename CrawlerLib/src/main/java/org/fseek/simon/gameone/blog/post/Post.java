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
import java.util.stream.Collectors;

import org.fseek.simon.gameone.blog.gallery.Gallery;
import org.fseek.simon.gameone.blog.video.Video;
import org.fseek.simon.gameone.util.Check;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Post {
    private final String headline;
    private final List<PostPart> parts;
    private final PostMeta meta;

    public Post(String headline, List<PostPart> parts, PostMeta meta) {
        Check.requireNonNull(headline, parts, meta);
        this.headline = headline;
        this.parts = parts;
        this.meta = meta;
    }

    public String getHeadline() {
        return headline;
    }

    public List<PostPart> getParts() {
        return parts;
    }

    @JsonIgnore
    public List<PostPartMeta> getPartMeta() {
        return getParts().stream().map(part -> part.getMeta()).collect(Collectors.toList());
    }

    @JsonIgnore
    public List<Video> getVideos() {
        return getPartMeta().stream().flatMap(pMeta -> pMeta.getVideos().stream()).collect(Collectors.toList());
    }

    @JsonIgnore
    public List<URL> getImages() {
        return getPartMeta().stream().flatMap(pMeta -> pMeta.getImages().stream()).collect(Collectors.toList());
    }

    @JsonIgnore
    public List<Gallery> getGalleries() {
        return getPartMeta().stream().flatMap(pMeta -> pMeta.getGalleries().stream()).collect(Collectors.toList());
    }

    public PostMeta getMeta() {
        return meta;
    }
}
