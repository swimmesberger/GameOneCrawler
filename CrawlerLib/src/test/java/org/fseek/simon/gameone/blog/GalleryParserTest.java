package org.fseek.simon.gameone.blog;

import static org.junit.Assert.assertEquals;

import java.nio.file.Paths;
import java.util.List;

import org.fseek.simon.gameone.TestCachedURLParser;
import org.fseek.simon.gameone.blog.gallery.Gallery;
import org.fseek.simon.gameone.blog.gallery.GalleryParser;
import org.fseek.simon.gameone.parse.OnlineURLParser;
import org.fseek.simon.gameone.parse.ParseException;
import org.fseek.simon.gameone.parse.URLParser;
import org.fseek.simon.gameone.util.JsoupUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GalleryParserTest {
    private static final String NON_GRID_URL = "http://www.gameone.de/blog/203/6/app-klusiv-game-one-hautnah-30";
    private static final String VOTEABLE_URL = "http://www.gameone.de/blog/2014/11/die-grosse-game-one-halloween-sause";
    private static final String GRID_URL = "http://www.gameone.de/blog/2014/11/appklusiv-hautnah-80";

    private static final String VOTEABLE_IMAGE_URL = "http://s3.gameone.de/gameone/assets/gallery_pictures/000/018/964/medium/Alwin.jpg";

    private GalleryParser galleryParser;
    private URLParser cachedUrlParser;

    @Before
    public void setUp() {
        this.galleryParser = new GalleryParser();
        this.cachedUrlParser = new TestCachedURLParser(new OnlineURLParser(), Paths.get("./cache"));
    }

    @After
    public void tearDown() {
        this.galleryParser = null;
        this.cachedUrlParser = null;
    }

    @SuppressWarnings("null")
    @Test
    public void testNonGridGallery() throws ParseException {
        List<Gallery> galleries = getGalleries(NON_GRID_URL);
        assertEquals(false, galleries == null);
        assertEquals(1, galleries.size());
        assertEquals(68, galleries.get(0).getImages().size());
    }

    @SuppressWarnings("null")
    @Test
    public void testGridGallery() throws ParseException {
        List<Gallery> galleries = getGalleries(GRID_URL);
        assertEquals(false, galleries == null);
        assertEquals(1, galleries.size());
        assertEquals(22, galleries.get(0).getImages().size());
    }

    @SuppressWarnings("null")
    @Test
    public void testVoteableGallery() throws ParseException {
        List<Gallery> galleries = getGalleries(VOTEABLE_URL);
        assertEquals(false, galleries == null);
        assertEquals(1, galleries.size());
        assertEquals(3, galleries.get(0).getImages().size());
        assertEquals(VOTEABLE_IMAGE_URL, galleries.get(0).getImages().get(0).getUrl().toString());
    }

    protected List<Gallery> getGalleries(String url) throws ParseException {
        Document doc = getCachedUrlParser().parse(JsoupUtil.url(url));
        Element post = JsoupUtil.getElementByClass(doc, "post", "single");
        List<Gallery> galleries = getGalleryParser().findGalleries(post);
        return galleries;
    }

    public GalleryParser getGalleryParser() {
        return galleryParser;
    }

    public URLParser getCachedUrlParser() {
        return cachedUrlParser;
    }
}
