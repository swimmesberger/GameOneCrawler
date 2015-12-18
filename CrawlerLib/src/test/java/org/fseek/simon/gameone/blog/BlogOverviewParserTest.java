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
package org.fseek.simon.gameone.blog;

import org.fseek.simon.gameone.TestCachedURLParser;
import org.fseek.simon.gameone.parse.OnlineURLParser;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import org.fseek.simon.gameone.blog.overview.BlogOverviewEntry;
import org.fseek.simon.gameone.blog.overview.BlogOverviewParser;
import org.fseek.simon.gameone.constants.PageConstants;
import org.fseek.simon.gameone.parse.ParseException;
import org.fseek.simon.gameone.parse.URLParser;
import org.fseek.simon.gameone.parse.page.Page;
import org.fseek.simon.gameone.parse.page.Pages;
import org.jsoup.nodes.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class BlogOverviewParserTest {
    private static final String SAMPLE_OVERVIEW_PAGE = "http://www.gameone.de/blog?page=2";

    private BlogOverviewParser parser;
    private URLParser cachedUrlParser;
    private URLParser onlineUrlParser;

    @Before
    public void setUp() {
        this.parser = new BlogOverviewParser();
        this.onlineUrlParser = new OnlineURLParser();
        this.cachedUrlParser = new TestCachedURLParser(getOnlineUrlParser(), Paths.get("./cache"));
    }

    @After
    public void tearDown() {
        this.parser = null;
        this.onlineUrlParser = null;
        this.cachedUrlParser = null;
    }

    @Test
    public void testParseOffilne() throws IOException, ParseException {
        Document doc = getCachedUrlParser().parse(new URL(PageConstants.BLOG_URL));
        Page<BlogOverviewEntry> parse = this.parser.parsePage(doc);
        assertEquals(false, parse == null);
    }

    // @Test
    public void testParseOnline() throws ParseException, IOException {
        Document doc = getOnlineUrlParser().parse(new URL(SAMPLE_OVERVIEW_PAGE));
        Page<BlogOverviewEntry> parse = this.parser.parsePage(doc);
        assertEquals(false, parse == null);
    }

    /**
     * Tests all overview pages directly via the online site.
     *
     * @throws IOException
     * @throws ParseException
     */
    // @Test
    public void testOverviewOnline() throws IOException, ParseException {
        Pages<BlogOverviewEntry> overview = getParser().parse(getOnlineUrlParser());
        int entryCount = overview.getEntryCount();
        assertEquals(2383, entryCount);
    }

    @Test
    public void testOverviewOffline() throws ParseException {
        Pages<BlogOverviewEntry> overview = getParser().parse(getCachedUrlParser());
        int entryCount = overview.getEntryCount();
        assertEquals(2383, entryCount);
    }

    public URLParser getCachedUrlParser() {
        return cachedUrlParser;
    }

    public URLParser getOnlineUrlParser() {
        return onlineUrlParser;
    }

    public BlogOverviewParser getParser() {
        return parser;
    }
}
