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

import com.beust.jcommander.Parameter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class CommandLineOptions {
    @Parameter(names = { "-c", "-cache" }, description = "Directory where the online websites should be saved")
    private String cacheDirectory;

    @Parameter(names = { "-o",
            "-output" }, description = "Directory where the json output should be saved", required = true)
    private String outputDirectory;

    public Optional<Path> getCacheDirectory() {
        if (this.cacheDirectory == null || this.cacheDirectory.isEmpty())
            return Optional.empty();
        Path filePath = Paths.get(this.cacheDirectory);
        return Optional.of(filePath);
    }

    public Path getOutputDirectory() {
        return Paths.get(this.outputDirectory);
    }
}
