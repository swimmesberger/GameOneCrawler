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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;

import org.fseek.simon.gameone.TestCachedURLParser;
import org.fseek.simon.gameone.blog.post.Post;
import org.fseek.simon.gameone.blog.post.PostParser;
import org.fseek.simon.gameone.parse.MediaOfflineException;
import org.fseek.simon.gameone.parse.OnlineURLParser;
import org.fseek.simon.gameone.parse.ParseException;
import org.fseek.simon.gameone.parse.URLParser;
import org.jsoup.nodes.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PostParserTest {
    private static final String VIDEO_SAMPLE_POST = "http://www.gameone.de/blog/2011/7/how-to-wie-loese-ich-den-rubik-s-cube";

    private static final String[] TEST_URLS = new String[] {
            "http://www.gameone.de/blog/2014/4/schnappt-euch-euren-wildstar-beta-key",
            "http://www.gameone.de/blog/2014/3/appklusiv-appcast-178",
            "http://www.gameone.de/blog/2012/3/app-klusiv-making-of-folge-199",
            "http://www.gameone.de/blog/2013/1/senf-ab-versager",
            "http://www.gameone.de/blog/2011/6/e3-2011-live-blog-donnerstag" };

    private PostParser postParser;
    private URLParser cachedUrlParser;

    @Before
    public void setUp() {
        this.postParser = new PostParser();
        this.cachedUrlParser = new TestCachedURLParser(new OnlineURLParser(), Paths.get("./cache"));
    }

    @After
    public void tearDown() {
        this.postParser = null;
        this.cachedUrlParser = null;
    }

    @Test
    public void testPostParse() throws ParseException, MalformedURLException, IOException {
        Document post = getCachedUrlParser().parse(new URL(VIDEO_SAMPLE_POST));
        Post parse = getPostParser().parse(post, getCachedUrlParser());
        assertEquals(6, parse.getParts().size());
        assertEquals(0, parse.getGalleries().size());
        assertEquals(1, parse.getImages().size());
        assertEquals("http://s3.gameone.de/gameone/assets/images/000/003/802/blog_list/simon_cube.jpg",
                parse.getImages().get(0).toString());
        assertEquals(59, parse.getVideos().size());
        assertEquals("How to: Wie löse ich den Rubik's Cube unter 20 Sekunden? Neue Videos!", parse.getHeadline());
    }

    @Test
    public void testPostParse2() throws ParseException, MalformedURLException, IOException {
        for (String url : TEST_URLS) {
            Document post;
            try {
                post = getCachedUrlParser().parse(new URL(url));
            } catch (MediaOfflineException ex) {
                return;
            }
            Post parse = getPostParser().parse(post, getCachedUrlParser());
            assertEquals(false, parse == null);
        }
    }

    public PostParser getPostParser() {
        return postParser;
    }

    public URLParser getCachedUrlParser() {
        return cachedUrlParser;
    }
}
