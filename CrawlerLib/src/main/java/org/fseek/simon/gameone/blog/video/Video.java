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
package org.fseek.simon.gameone.blog.video;

import java.net.URL;
import java.util.Objects;

public class Video {
    private final MRSSVideoInfo mrss;
    private final MediaGen mediaInfo;

    public Video(MRSSVideoInfo mrss, MediaGen mediaInfo) {
        Objects.requireNonNull(mrss);
        Objects.requireNonNull(mediaInfo);
        this.mrss = mrss;
        this.mediaInfo = mediaInfo;
    }

    /**
     * Get the video url with the best quality.
     * 
     * @return
     */
    public URL getUrl() {
        return getMediaInfo().getBestQuality().getUrl();
    }

    public MediaGen getMediaInfo() {
        return mediaInfo;
    }

    public MRSSVideoInfo getMrss() {
        return mrss;
    }
}
