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
package org.fseek.simon.gameone.blog.video;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.fseek.simon.gameone.constants.HTMLConstants;
import org.fseek.simon.gameone.parse.MediaOfflineException;
import org.fseek.simon.gameone.parse.ParseException;
import org.fseek.simon.gameone.parse.URLParser;
import org.fseek.simon.gameone.util.ErrorUtil;
import org.fseek.simon.gameone.util.JsoupUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class VideoParser {
    private static final String VIDEO_CLASS = "player_swf";

    private static final String MRSS_VARIBALE_KEY = "mrss";
    private static final String FILE_VARIABLE_KEY = "file";
    private static final String MRSS_ITEM_SELECTOR = "channel > item";
    private static final String MRSS_TITLE_TAG = "title";
    private static final String MRSS_DESC_TAG = "description";
    private static final String MRSS_TEASER_TAG = "image";
    private static final String MRSS_TEASER_URL_ATTRIBUTE = "url";
    private static final String MRSS_PUB_DATE_TAG = "pubDate";
    private static final String MRSS_MEDIA_GROUP_TAG = "media:group";
    private static final String MRSS_MEDIA_CONTENT_TAG = "media:content";
    private static final String MRSS_MEDIA_CONTENT_URL_ATTRIBUTE = "url";

    private static final String MEDIA_RENDITION_SELECTOR = "video > item > rendition";
    private static final String MEDIA_DURATION_ATTRIBUTE = "duration";
    private static final String MEDIA_WIDTH_ATTRIBUTE = "width";
    private static final String MEDIA_HEIGHT_ATTRIBUTE = "height";
    private static final String MEDIA_BITRATE_ATTRIBUTE = "bitrate";
    private static final String MEDIA_RENDITION_SOURCE_TAG = "src";

    private static final String MEDIA_SOURCE_REPLACE_PATTENR = "^.*?/r2/";
    private static final String MEDIA_SOURCE_REPLACE_URL = "http://cdn.riptide-mtvn.com/r2/";

    private static final String DIRECT_EMBED_TITLE_KEY = "title";
    // in some cases there is a typo for that parameter
    private static final String DIRECT_EMBED_TITLE_WRONG_KEY = "tile";
    private static final String DIRECT_EMBED_TEASER_KEY = "image";

    private static final DateTimeFormatter MRSS_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z");

    private final ScriptParser scriptParser;

    public VideoParser() {
        // this(new NashornScriptParser());
        // default so faster RegEx script parser
        this(new RegexScriptParser());
    }

    public VideoParser(ScriptParser scriptParser) {
        this.scriptParser = scriptParser;
    }

    public List<Video> find(Element postPart, URLParser parser) throws ParseException {
        List<Video> videos = postPart.getElementsByClass(VIDEO_CLASS).stream().map(ErrorUtil.rethrow(el -> {
            Optional<Video> video = parseVideo(el, parser);
            if (!video.isPresent())
                return null;
            return video.get();
        })).filter(v -> v != null).collect(Collectors.toList());
        return videos;
    }

    /**
     * Parses video information. Can return null when the video is not available
     * anymore.
     *
     * @param video
     * @param parser
     * @return
     * @throws ParseException
     */
    public Optional<Video> parseVideo(Element video, URLParser parser) throws ParseException {
        Element javascript = JsoupUtil.getElementByTag(video, HTMLConstants.SCRIPT_TAG);
        String javascriptText = javascript.html();
        FlashObject object = getScriptParser().parse(javascriptText);
        String sMrssUrl = (String) object.getVariable(MRSS_VARIBALE_KEY);
        if (sMrssUrl == null || sMrssUrl.isEmpty()) {
            String file = (String) object.getVariable(FILE_VARIABLE_KEY);
            if (file == null || file.isEmpty()) {
                throw ErrorUtil.parseError("Mrss variable null!");
            } else {
                return Optional.of(parseVideoWithoutMrss(file, object));
            }
        }
        return parseVideoMrss(sMrssUrl, parser);
    }

    /**
     * Parse video via the mrss API
     *
     * @param sMrssUrl
     * @param parser
     * @return
     * @throws ParseException
     */
    protected Optional<Video> parseVideoMrss(String sMrssUrl, URLParser parser) throws ParseException {
        URL mrssURL;
        try {
            mrssURL = new URL(sMrssUrl);
        } catch (MalformedURLException ex) {
            throw ErrorUtil.parseError("Mrss URL can't be parsed!", ex);
        }
        try {
            MRSSVideoInfo mrss = parseMRSS(parser.parse(mrssURL, true));
            MediaGen mediaInfo = parseMediaGen(parser.parse(mrss.getContentURL(), true));
            return Optional.of(new Video(mrss, mediaInfo));
        } catch (MediaOfflineException ex) {
            return Optional.empty();
        }
    }

    /**
     * Handle directly embedded videos
     *
     * @param file
     * @param object
     * @return
     * @throws ParseException
     */
    protected Video parseVideoWithoutMrss(String file, FlashObject object) throws ParseException {
        String title = (String) object.getVariable(DIRECT_EMBED_TITLE_KEY);
        if (title == null || title.isEmpty()) {
            title = (String) object.getVariable(DIRECT_EMBED_TITLE_WRONG_KEY);
            if (title == null || title.isEmpty()) {
                title = "";
            }
        }
        URL teaser = JsoupUtil.url((String) object.getVariable(DIRECT_EMBED_TEASER_KEY));
        URL videoFile = JsoupUtil.url(file);
        MRSSVideoInfo mrss = new MRSSVideoInfo(null, title, teaser, null, "", videoFile);
        MediaGen gen = new MediaGen(null, Arrays.asList(new MediaGen.VideoInfo(videoFile, -1, -1, -1, -1)));
        return new Video(mrss, gen);
    }

    public MRSSVideoInfo parseMRSS(Document parsedMrss) throws ParseException {
        URL url = JsoupUtil.url(parsedMrss.baseUri());

        Element itemTag = parsedMrss.select(MRSS_ITEM_SELECTOR).first();
        if (itemTag == null)
            throw ErrorUtil.parseError("Mrss page does not contain a item tag!");
        String title = JsoupUtil.getElementByTag(itemTag, MRSS_TITLE_TAG).text();
        String description = JsoupUtil.getElementByTag(itemTag, MRSS_DESC_TAG).text();
        URL teaserImage = JsoupUtil.url(JsoupUtil.getElementByTag(itemTag, MRSS_TEASER_TAG), MRSS_TEASER_URL_ATTRIBUTE);
        String pubDateString = JsoupUtil.getElementByTag(itemTag, MRSS_PUB_DATE_TAG).text();
        ZonedDateTime pubDate = ZonedDateTime.parse(pubDateString, MRSS_DATE_FORMAT);
        Element media = JsoupUtil.getElementByTag(itemTag, MRSS_MEDIA_GROUP_TAG);
        Element mediaContent = JsoupUtil.getElementByTag(media, MRSS_MEDIA_CONTENT_TAG);
        URL contentURL = JsoupUtil.url(mediaContent, MRSS_MEDIA_CONTENT_URL_ATTRIBUTE);

        return new MRSSVideoInfo(url, title, teaserImage, pubDate, description, contentURL);
    }

    public MediaGen parseMediaGen(Document mediaGen) throws ParseException {
        URL url = JsoupUtil.url(mediaGen.baseUri());
        Elements rendition = mediaGen.select(MEDIA_RENDITION_SELECTOR);
        if (rendition.isEmpty())
            throw ErrorUtil.parseError("No rendition information!");
        List<MediaGen.VideoInfo> videos = rendition.stream().map(ErrorUtil.rethrow(this::parseRendition))
                .collect(Collectors.toList());
        return new MediaGen(url, videos);
    }

    public MediaGen.VideoInfo parseRendition(Element rendition) throws ParseException {
        try {
            double duration = Double.parseDouble(JsoupUtil.attr(rendition, MEDIA_DURATION_ATTRIBUTE));
            int width = Integer.parseInt(JsoupUtil.attr(rendition, MEDIA_WIDTH_ATTRIBUTE));
            int height = Integer.parseInt(JsoupUtil.attr(rendition, MEDIA_HEIGHT_ATTRIBUTE));
            int bitrate = Integer.parseInt(JsoupUtil.attr(rendition, MEDIA_BITRATE_ATTRIBUTE));
            String rtmpSrc = JsoupUtil.getElementByTag(rendition, MEDIA_RENDITION_SOURCE_TAG).text();
            URL url = JsoupUtil.url(convertRtmpSrc(rtmpSrc));
            return new MediaGen.VideoInfo(url, width, height, bitrate, duration);
        } catch (NumberFormatException ex) {
            throw ErrorUtil.parseError(ex);
        }
    }

    protected String convertRtmpSrc(String rtmpSrc) {
        String cdnURL = rtmpSrc.replaceFirst(MEDIA_SOURCE_REPLACE_PATTENR, MEDIA_SOURCE_REPLACE_URL);
        return cdnURL;
    }

    protected ScriptParser getScriptParser() {
        return scriptParser;
    }
}
