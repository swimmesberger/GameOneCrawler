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

package org.fseek.simon.gameone.blog.overview;

import java.net.URL;
import java.time.ZonedDateTime;

import org.fseek.simon.gameone.parse.full.OverviewEntry;
import org.fseek.simon.gameone.util.Check;

public class BlogOverviewEntry implements OverviewEntry {
    private final int id;
    private final String title;
    private final String subtitle;
    private final String user;
    private final URL image;
    private final URL post;
    private final ZonedDateTime postedAt;

    public BlogOverviewEntry(int id, String title, String subtitle, String user, ZonedDateTime postedAt, URL image,
            URL post) {
        Check.requireNonNull(title, subtitle, user, postedAt, image, post);
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.user = user;
        this.postedAt = postedAt;
        this.image = image;
        this.post = post;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getUser() {
        return user;
    }

    public ZonedDateTime getPostedAt() {
        return postedAt;
    }

    public URL getImage() {
        return image;
    }

    public URL getPost() {
        return post;
    }

    @Override
    public URL getEntryURL() {
        return getPost();
    }
}
