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
package org.fseek.simon.gameone.tv;

import java.nio.file.Paths;
import org.fseek.simon.gameone.TestCachedURLParser;
import org.fseek.simon.gameone.parse.OnlineURLParser;
import org.fseek.simon.gameone.parse.ParseException;
import org.fseek.simon.gameone.parse.URLParser;
import org.fseek.simon.gameone.tv.overview.TVEntry;
import org.fseek.simon.gameone.tv.overview.TVEntryParser;
import org.fseek.simon.gameone.util.JsoupUtil;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

public class TVEntryParserTest {
    private static final String[] TEST_URLS = new String[] { "http://www.gameone.de/tv/307",
            "http://www.gameone.de/tv/113", "http://www.gameone.de/tv/1" };

    private TVEntryParser parser;
    private URLParser cachedUrlParser;

    @Before
    public void setUp() {
        this.parser = new TVEntryParser();
        this.cachedUrlParser = new TestCachedURLParser(new OnlineURLParser(), Paths.get("./cache"));
    }

    @After
    public void tearDown() {
        this.parser = null;
        this.cachedUrlParser = null;
    }

    @Test
    public void testOverviewOffline() throws ParseException {
        for (String s : TEST_URLS) {
            TVEntry parse = getParser().parse(JsoupUtil.url(s), getCachedUrlParser());
            assertEquals(false, parse == null);
        }
    }

    public URLParser getCachedUrlParser() {
        return cachedUrlParser;
    }

    public TVEntryParser getParser() {
        return parser;
    }
}
