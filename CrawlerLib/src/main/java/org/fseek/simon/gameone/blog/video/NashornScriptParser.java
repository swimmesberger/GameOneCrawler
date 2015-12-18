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

import java.util.HashMap;
import java.util.Map;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.fseek.simon.gameone.parse.ParseException;
import org.fseek.simon.gameone.util.ErrorUtil;

import jdk.nashorn.api.scripting.NashornScriptEngineFactory;

/**
 * This script parser implementation uses the java 8 nashorn script engine to
 * evaluate the passed javascript.
 */
public class NashornScriptParser implements ScriptParser {
    private static final String JS_WRAP_FUNCTION_NAME = "swf";

    // the real swfobject code will be wrapped in this function to later call it
    // from java code
    private static final String JAVASCRIPT_WRAP_CODE = "" + "function " + JS_WRAP_FUNCTION_NAME + "(){\n"
            + "var SWFObject = Java.type(\"" + SWFObject.class.getName() + "\");\n" + "%s" + "return so;\n" + "}";

    // static because init is slow - access is synchronized
    private static ScriptEngine scriptEngine;

    @Override
    public SWFObject parse(String javascriptText) throws ParseException {
        return evaluateJavascript(javascriptText);
    }

    /**
     * This is some nashorn javascript engine magic. It assumes that the passed
     * string is a SWFObject init code. The code will be wrapped in a function
     * which will be invoked to grab all variables and parameters.
     *
     * Quick benchmarks showed that here we loose most of the time (obviously
     * because we are running a fully fledged JS engine here...) so with a bit
     * of regex we could parse out the stuff we need but that would be less fun
     * :)
     *
     * @param javascriptText
     * @return
     * @throws ParseException
     */
    protected SWFObject evaluateJavascript(String javascriptText) throws ParseException {
        if (javascriptText == null || javascriptText.isEmpty()) {
            throw new ParseException("No javascript code passed!");
        }
        javascriptText = String.format(JAVASCRIPT_WRAP_CODE, javascriptText);
        try {
            SWFObject sReturn;
            ScriptEngine jsEngine = getScriptEngine();
            synchronized (jsEngine) {
                jsEngine.eval(javascriptText);
                Invocable invocable = (Invocable) jsEngine;
                try {
                    sReturn = (SWFObject) invocable.invokeFunction(JS_WRAP_FUNCTION_NAME);
                } catch (NoSuchMethodException | RuntimeException ex) {
                    Throwable fEx = ex;
                    if (fEx instanceof RuntimeException && fEx.getCause() != null) {
                        fEx = fEx.getCause();
                    }
                    throw ErrorUtil.parseError("JavaScript execution error.", fEx);
                }
            }
            return sReturn;
        } catch (ScriptException ex) {
            throw ErrorUtil.parseError(String.format("JavaScript parse error - executed JS: %s", javascriptText), ex);
        }
    }

    protected ScriptEngine getScriptEngine() {
        if (NashornScriptParser.scriptEngine == null) {
            NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
            // ensure only the SWFObject class can be used in the passed
            // JavaScript to prevent malicious js code.
            NashornScriptParser.scriptEngine = factory
                    .getScriptEngine((String string) -> string.equals(SWFObject.class.getName()));
        }
        return scriptEngine;
    }

    public static class SWFObject extends DefaultFlashObject {
        private final String playerURL;
        private final String playerType;
        private final String width;
        private final String height;
        private final String version;
        private final String colorHex;

        private final Map<String, String> params;

        private String destID;

        public SWFObject(String playerURL, String playerType, String width, String height, String version,
                String colorHex) {
            this.playerURL = playerURL;
            this.playerType = playerType;
            this.width = width;
            this.height = height;
            this.version = version;
            this.colorHex = colorHex;
            this.params = new HashMap<>();
        }

        public void addParam(String key, String value) {
            this.params.put(key, value);
        }

        public void write(String destID) {
            this.destID = destID;
        }

        public String getColorHex() {
            return colorHex;
        }

        public String getDestID() {
            return destID;
        }

        public String getHeight() {
            return height;
        }

        public Map<String, String> getParams() {
            return params;
        }

        public String getPlayerType() {
            return playerType;
        }

        public String getPlayerURL() {
            return playerURL;
        }

        public String getVersion() {
            return version;
        }

        public String getWidth() {
            return width;
        }
    }
}
