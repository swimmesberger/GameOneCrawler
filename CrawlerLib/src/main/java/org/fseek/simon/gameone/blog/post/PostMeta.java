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
import java.time.ZonedDateTime;
import java.util.List;

import org.fseek.simon.gameone.util.Check;

public class PostMeta {
    private final String user;
    private final URL userImage;
    private final List<String> tags;
    private final List<String> categories;
    private final ZonedDateTime postedAt;

    public PostMeta(String user, URL userImage, List<String> tags, List<String> categories, ZonedDateTime postedAt) {
        Check.requireNonNull(user, userImage, tags, categories, postedAt);
        this.user = user;
        this.userImage = userImage;
        this.tags = tags;
        this.categories = categories;
        this.postedAt = postedAt;
    }

    public String getUser() {
        return user;
    }

    public URL getUserImage() {
        return userImage;
    }

    public List<String> getTags() {
        return tags;
    }

    public List<String> getCategories() {
        return categories;
    }

    public ZonedDateTime getPostedAt() {
        return postedAt;
    }
}
