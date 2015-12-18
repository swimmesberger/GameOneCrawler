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
import java.util.Comparator;
import java.util.List;

import org.fseek.simon.gameone.util.Check;

public class MediaGen {
    private final URL url;
    private final List<VideoInfo> videos;

    // TODO: url can be null when it's a directly embedded video - should be
    // reworked with 2 different data classes - one with MRSS and one with
    // direct embed
    public MediaGen(URL url, List<VideoInfo> videos) {
        Check.requireNonNull(videos);
        if (videos.size() <= 0)
            throw new IllegalArgumentException("Videos list must contain at least one element!");
        this.url = url;
        this.videos = videos;
    }

    public URL getURL() {
        return url;
    }

    public List<VideoInfo> getVideos() {
        return videos;
    }

    public VideoInfo getBestQuality() {
        return getVideos().stream().max(VideoInfo.QUALITY_COMPARATOR).get();
    }

    public static class VideoInfo {
        private static final Comparator<VideoInfo> QUALITY_COMPARATOR;

        static {
            QUALITY_COMPARATOR = (v1, v2) -> {
                int comp = Integer.compare(v1.getBitrate(), v2.getBitrate());
                if (comp != 0)
                    return comp;
                comp = Integer.compare(v1.getWidth(), v2.getWidth());
                if (comp != 0)
                    return comp;
                comp = Integer.compare(v1.getHeight(), v2.getHeight());
                if (comp != 0)
                    return comp;
                return 0;
            };
        }

        private final URL url;
        private final int width;
        private final int height;
        private final int bitrate;
        private final double duration;

        public VideoInfo(URL url, int width, int height, int bitrate, double duration) {
            this.url = url;
            this.width = width;
            this.height = height;
            this.bitrate = bitrate;
            this.duration = duration;
        }

        public int getBitrate() {
            return bitrate;
        }

        public double getDuration() {
            return duration;
        }

        public int getHeight() {
            return height;
        }

        public URL getUrl() {
            return url;
        }

        public int getWidth() {
            return width;
        }
    }
}
