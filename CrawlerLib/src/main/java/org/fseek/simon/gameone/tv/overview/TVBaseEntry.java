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

import org.fseek.simon.gameone.parse.full.OverviewEntry;
import org.fseek.simon.gameone.util.Check;

public class TVBaseEntry implements OverviewEntry {
    private final URL post;
    private final String headline;
    private final String description;
    private final int views;
    private final int ratingsCount;
    private final float rating;

    public TVBaseEntry(URL post, String headline, String description, int views, int ratingsCount, float rating) {
        Check.requireNonNull(post, headline, description);
        this.post = post;
        this.headline = headline;
        this.description = description;
        this.views = views;
        this.ratingsCount = ratingsCount;
        this.rating = rating;
    }

    public URL getPost() {
        return post;
    }

    public String getDescription() {
        return description;
    }

    public String getHeadline() {
        return headline;
    }

    public float getRating() {
        return rating;
    }

    public int getViews() {
        return views;
    }

    public int getRatingsCount() {
        return ratingsCount;
    }

    @Override
    public URL getEntryURL() {
        return getPost();
    }
}
