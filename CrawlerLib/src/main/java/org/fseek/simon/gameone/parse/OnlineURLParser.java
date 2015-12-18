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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import org.fseek.simon.gameone.util.ErrorUtil;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

public class OnlineURLParser implements URLParser {
    private static final int TIMEOUT = 5000;

    @Override
    public Document parse(URL url, boolean xml) throws ParseException {
        String baseUri = url.toExternalForm();
        return parseOnline(url, baseUri, xml);
    }

    protected Document parseOnline(URL url, String baseUri, boolean xml) throws ParseException {
        try {
            Document result;
            if (xml) {
                URLConnection con = url.openConnection();
                con.setConnectTimeout(TIMEOUT);
                con.setReadTimeout(TIMEOUT);
                result = Jsoup.parse(con.getInputStream(), null, baseUri, Parser.xmlParser());
            } else {
                Connection con = HttpConnection.connect(url);
                con.followRedirects(false);
                con.ignoreHttpErrors(false);
                con.timeout(TIMEOUT);
                Connection.Response response = con.execute();
                if (response.statusCode() == 301) {
                    throw new FileNotFoundException("Original content was moved!");
                }
                result = response.parse();
            }
            return result;
        } catch (FileNotFoundException | HttpStatusException ex) {
            throw ErrorUtil.offlineError(ex);
        } catch (IOException ex) {
            throw ErrorUtil.parseError(ex);
        }
    }
}
