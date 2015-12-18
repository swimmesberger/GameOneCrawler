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

import org.fseek.simon.gameone.blog.video.FlashObject;
import org.fseek.simon.gameone.blog.video.RegexScriptParser;
import org.fseek.simon.gameone.parse.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RegexScriptParserTest {
    private static final String JAVASCRIPT_SAMPLE = "var rand = Math.floor((Math.random()*10000000000)+1);\n"
            + "      var so = new SWFObject(\"https://playermtvnn-a.akamaihd.net/g2/g2player_2.2.1.swf\", \"embeddedPlayer\", \"566\", \"318\", \"9.0.28.0\", \"#000000\");\n"
            + "      so.addVariable(\"mrss\", \"http://www.gameone.de/api/mrss/mgid:gameone:video:mtvnn.com:video_meta-356cb7abf11a4d6297e8a38f7f947810\");\n"
            + "      so.addVariable(\"config\", \"https://playermtvnn-a.akamaihd.net/g2/configs/gameone_de_DE.xml\");\n"
            + "      so.addVariable(\"adSite\", \"gameone.de\");\n"
            + "      so.addVariable(\"umaSite\", \"gameone.de\");\n" + "      so.addVariable(\"autoPlay\", \"\");\n"
            + "      so.addVariable(\"url\", \"http://www.gameone.de/blog/2011/7/how-to-wie-loese-ich-den-rubik-s-cube\");\n"
            + "      so.addVariable(\"tile\", \"slomo_cube\");\n" + "      so.addVariable(\"ord\", rand);\n"
            + "      so.addVariable(\"image\", \"http://s3.gameone.de/gameone/assets/video_metas/teaser_images/000/632/559/big/slomo.jpg\");\n"
            + "      so.addParam(\"wmode\", \"true\");\n" + "      so.addParam(\"enableJavascript\", \"true\");\n"
            + "      so.addParam(\"allowscriptaccess\", \"always\");\n"
            + "      so.addParam(\"swLiveConnect\", \"true\");\n"
            + "      so.addParam(\"allowfullscreen\", \"true\");\n"
            + "      so.write(\"flash_container_632559_video_meta_632559\");";

    private RegexScriptParser scriptParser;

    @Before
    public void setUp() {
        this.scriptParser = new RegexScriptParser();
    }

    @After
    public void tearDown() {
        this.scriptParser = null;
    }

    @Test
    public void testVideoJavascript() throws ParseException {
        FlashObject evaluateJavascript = getScriptParser().parse(JAVASCRIPT_SAMPLE);
        assertEquals(9, evaluateJavascript.getVariables().size());
    }

    public RegexScriptParser getScriptParser() {
        return scriptParser;
    }
}
