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
package org.fseek.simon.gameone.parse;

import java.net.URL;

import org.fseek.simon.gameone.util.ErrorUtil.FunctionException;
import org.jsoup.nodes.Document;

public interface URLParser extends FunctionException<URL, Document> {
    public Document parse(URL url, boolean xml) throws ParseException;

    public default Document parse(URL url) throws ParseException {
        return parse(url, false);
    }

    @Override
    public default Document apply(URL t) throws ParseException {
        return parse(t);
    }
}
