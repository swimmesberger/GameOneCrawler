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

import static org.junit.Assert.assertEquals;

import java.nio.file.Paths;

import org.fseek.simon.gameone.TestCachedURLParser;
import org.fseek.simon.gameone.blog.overview.BlogOverviewEntry;
import org.fseek.simon.gameone.blog.post.Post;
import org.fseek.simon.gameone.parse.OnlineURLParser;
import org.fseek.simon.gameone.parse.ParseException;
import org.fseek.simon.gameone.parse.URLParser;
import org.fseek.simon.gameone.parse.full.DefaultFullPage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BlogParserTest {
    private BlogParser parser;
    private URLParser cachedUrlParser;

    @Before
    public void setUp() {
        this.parser = new BlogParser();
        OnlineURLParser onlineUrlParser = new OnlineURLParser();
        this.cachedUrlParser = new TestCachedURLParser(onlineUrlParser, Paths.get("./cache"));
    }

    @After
    public void tearDown() {
        this.parser = null;
        this.cachedUrlParser = null;
    }

    @Test
    public void testBlogParse() throws ParseException {
        DefaultFullPage<BlogOverviewEntry, Post> blog = getBlogParser().parse(getCachedUrlParser());
        assertEquals(2380, blog.getEntries().size());
        assertEquals(298, blog.getOverview().getPages().size());
    }

    public BlogParser getBlogParser() {
        return parser;
    }

    public URLParser getCachedUrlParser() {
        return cachedUrlParser;
    }

}
