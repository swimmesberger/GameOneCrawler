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

/**
 * @param <T>
 *            Overview entry type
 * @param <E>
 *            Entry type
 */
public interface FullPage<T, E> {
    public Pages<T> getOverview();

    public List<E> getEntries();
}
