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
package org.fseek.simon.gameone.parse.full;

import java.util.List;

import org.fseek.simon.gameone.parse.page.Pages;
import org.fseek.simon.gameone.util.Check;

/**
 * Container class for the complete parsed information.
 */
public class DefaultFullPage<T, E> implements FullPage<T, E> {
    private final Pages<T> overview;
    private final List<E> entries;

    public DefaultFullPage(Pages<T> overview, List<E> posts) {
        Check.requireNonNull(overview, posts);
        this.overview = overview;
        this.entries = posts;
    }

    @Override
    public Pages<T> getOverview() {
        return overview;
    }

    @Override
    public List<E> getEntries() {
        return entries;
    }
}
