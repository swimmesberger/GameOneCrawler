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
package org.fseek.simon.gameone.constants;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.fseek.simon.gameone.parse.ParseException;
import org.fseek.simon.gameone.util.ErrorUtil;

public interface PageConstants {
    public static final String ROOT_URL = "http://www.gameone.de";
    public static final String BLOG_URL = ROOT_URL + "/blog";
    public static final String TV_URL = ROOT_URL + "/tv";

    public static final ZoneId TIMEZONE = ZoneId.of("Europe/Berlin");
    public static final String HUMAN_SIMPLE_DATE_PATTERN = "EEEE, dd. MMMM yyyy, HH:mm";
    public static final DateTimeFormatter PUBLISH_DATE_FORMATTER = DateTimeFormatter
            .ofPattern(HUMAN_SIMPLE_DATE_PATTERN, Locale.GERMAN).withZone(PageConstants.TIMEZONE);

    public static URL absolute(String relative) throws ParseException {
        try {
            return new URL(ROOT_URL + relative);
        } catch (MalformedURLException ex) {
            throw ErrorUtil.parseError("Can't convert relative URL to absolute URL!", ex);
        }
    }
}
