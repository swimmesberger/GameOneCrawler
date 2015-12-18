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

import org.fseek.simon.gameone.blog.video.Video;
import org.fseek.simon.gameone.util.Check;

public class TVEntry extends TVBaseEntry {
    private final LocalDate postedAt;
    private final int episode;
    private final Video video;

    public TVEntry(URL post, String headline, String description, int views, int ratingsCount, float rating,
            LocalDate postedAt, int episode, Video video) {
        super(post, headline, description, views, ratingsCount, rating);
        Check.requireNonNull(postedAt);
        this.postedAt = postedAt;
        this.episode = episode;
        this.video = video;
    }

    public LocalDate getPostedAt() {
        return postedAt;
    }

    public int getEpisode() {
        return episode;
    }

    public Video getVideo() {
        return video;
    }
}
