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
import java.time.ZonedDateTime;
import java.util.Optional;

import org.fseek.simon.gameone.util.Check;

public class MRSSVideoInfo {
    private final URL url;
    private final String title;
    private final URL teaserImage;
    private final Optional<ZonedDateTime> pubDate;
    private final String description;
    private final URL contentURL;

    // TODO: url and pubDate can be null when it's a directly embedded video -
    // should be reworked with 2 different data classes - one with MRSS and one
    // with direct embed
    public MRSSVideoInfo(URL url, String title, URL teaserImage, ZonedDateTime pubDate, String description,
            URL contentURL) {
        Check.requireNonNull(title, teaserImage, description, contentURL);
        this.url = url;
        this.title = title;
        this.teaserImage = teaserImage;
        this.pubDate = Optional.ofNullable(pubDate);
        this.description = description;
        this.contentURL = contentURL;
    }

    public URL getURL() {
        return url;
    }

    public URL getContentURL() {
        return contentURL;
    }

    public String getDescription() {
        return description;
    }

    public Optional<ZonedDateTime> getPubDate() {
        return pubDate;
    }

    public URL getTeaserImage() {
        return teaserImage;
    }

    public String getTitle() {
        return title;
    }
}
