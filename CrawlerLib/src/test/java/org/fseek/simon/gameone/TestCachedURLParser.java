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
package org.fseek.simon.gameone;

import java.net.URL;
import java.nio.file.Path;
import org.fseek.simon.gameone.parse.CachedURLParser;
import org.fseek.simon.gameone.parse.ParseException;
import org.fseek.simon.gameone.parse.URLParser;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestCachedURLParser extends CachedURLParser {
    private static final Logger logger = LoggerFactory.getLogger(TestCachedURLParser.class);

    public TestCachedURLParser(URLParser delegate) {
        super(delegate);
    }

    public TestCachedURLParser(URLParser delegate, Path cacheDir) {
        super(delegate, cacheDir);
    }

    @Override
    protected Document parseOffline(Path file, String baseUri, boolean xml) throws ParseException {
        logger.info("Using offline file: " + file.toAbsolutePath());
        return super.parseOffline(file, baseUri, xml);
    }

    @Override
    protected Document parseDelegate(URL url, boolean xml) throws ParseException {
        logger.info("Using delegate file: " + url);
        return super.parseDelegate(url, xml);
    }
}
