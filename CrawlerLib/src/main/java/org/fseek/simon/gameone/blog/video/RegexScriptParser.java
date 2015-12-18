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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fseek.simon.gameone.parse.ParseException;
import org.fseek.simon.gameone.util.ErrorUtil;

/**
 * Simple ScriptParser using RegEx. This parser is much faster than the
 * {@link NashornScriptParser} but also more error prone.
 * 
 * Dynamic variables evaluation in addVariable assignments is not supported but
 * dynamic values are detected null is used in such cases.
 */
public class RegexScriptParser implements ScriptParser {
    private static final String KEY_KEY = "KEY";
    private static final String VALUE_KEY = "VALUE";
    private static final String DYNAMIC_VALUE_KEY = "DYNVAL";
    private static final Pattern VARIABLE_PATTERN = Pattern.compile(".*addVariable\\(\"(?<" + KEY_KEY
            + ">.+)\" *, *(?:\"(?<" + VALUE_KEY + ">.*)\"|(?<" + DYNAMIC_VALUE_KEY + ">[a-zA-Z]*)).*");

    @Override
    public FlashObject parse(String javascriptText) throws ParseException {
        DefaultFlashObject object = new DefaultFlashObject();
        Matcher matcher = VARIABLE_PATTERN.matcher(javascriptText);
        while (matcher.find()) {
            String key = matcher.group(KEY_KEY);
            if (key == null) {
                throw ErrorUtil.parseError("No key found for match '" + javascriptText + "'");
            }
            String value = matcher.group(VALUE_KEY);
            String dynamicValue = matcher.group(DYNAMIC_VALUE_KEY);
            if (value != null) {
                object.addVariable(key, value);
            } else if (dynamicValue != null) {
                object.addVariable(key, null);
            } else {
                throw ErrorUtil.parseError("No value found for key '" + key + "'");
            }

        }
        return object;
    }
}
