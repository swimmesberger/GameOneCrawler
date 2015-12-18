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

import static org.junit.Assert.assertEquals;

import java.nio.file.Paths;

import org.fseek.simon.gameone.TestCachedURLParser;
import org.fseek.simon.gameone.parse.OnlineURLParser;
import org.fseek.simon.gameone.parse.ParseException;
import org.fseek.simon.gameone.parse.URLParser;
import org.fseek.simon.gameone.parse.full.DefaultFullPage;
import org.fseek.simon.gameone.tv.overview.TVEntry;
import org.fseek.simon.gameone.tv.overview.TVOverviewEntry;
import org.fseek.simon.gameone.tv.overview.TVParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TVParserTest {
    private TVParser parser;
    private URLParser cachedUrlParser;

    @Before
    public void setUp() {
        this.parser = new TVParser();
        this.cachedUrlParser = new TestCachedURLParser(new OnlineURLParser(), Paths.get("./cache"));
    }

    @After
    public void tearDown() {
        this.parser = null;
        this.cachedUrlParser = null;
    }

    @Test
    public void testOverviewOffline() throws ParseException {
        DefaultFullPage<TVOverviewEntry, TVEntry> blog = getParser().parse(getCachedUrlParser());
        assertEquals(307, blog.getOverview().getEntryCount());
        assertEquals(9, blog.getOverview().getPages().size());
        // 2 episodes are offline at the time of writing this test
        // (http://www.gameone.de/tv/117, http://www.gameone.de/tv/271)
        assertEquals(305, blog.getEntries().size());
    }

    public URLParser getCachedUrlParser() {
        return cachedUrlParser;
    }

    public TVParser getParser() {
        return parser;
    }
}
