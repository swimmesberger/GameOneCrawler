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

import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.fseek.simon.gameone.blog.Blog;
import org.fseek.simon.gameone.blog.BlogParser;
import org.fseek.simon.gameone.blog.post.Post;
import org.fseek.simon.gameone.parse.OnlineURLParser;
import org.fseek.simon.gameone.parse.ParseException;
import org.fseek.simon.gameone.parse.URLParser;
import org.fseek.simon.gameone.parse.full.AbstractFullPageParser.FullPageParseListener;
import org.fseek.simon.gameone.tv.overview.TV;
import org.fseek.simon.gameone.tv.overview.TVEntry;
import org.fseek.simon.gameone.tv.overview.TVParser;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class Main {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Main.class);

    /**
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
        try {
            if (args.length <= 0) {
                args = new String[] { "-c", "H:\\gameone\\cache", "-o", "H:\\gameone\\output" };
            }
            CommandLineOptions options = new CommandLineOptions();
            new JCommander(options, args);

            URLParser tParser = new OnlineURLParser();
            Optional<Path> cacheDir = options.getCacheDirectory();
            if (cacheDir.isPresent()) {
                tParser = new LoggerCachedURLParser(tParser, cacheDir.get());
            } else {
                tParser = new LoggerCachedURLParser(tParser);
            }
            URLParser parser = tParser;
            Path outputDir = options.getOutputDirectory();
            parseBlog(outputDir, parser);
            parseTV(outputDir, parser);
        } catch (ParseException | IOException ex) {
            logger.error(ex.getMessage(), ex);
            System.exit(-1);
        }
    }

    protected static void parseBlog(final Path outputDirectory, final URLParser parser)
            throws ParseException, IOException {
        final BlogParser blogParser = new BlogParser();
        final List<URL> failedPosts = new ArrayList<>();
        final List<URL> offlinePosts = new ArrayList<>();
        Blog blog = blogParser.parse(parser, Optional.of(new FullPageParseListener<Post>() {
            @Override
            public void onEntry(Post post) {
            }

            @Override
            public void onEntryFailed(URL postURL, Throwable ex) {
                failedPosts.add(postURL);
            }

            @Override
            public void onEntryOffline(URL postURL) {
                offlinePosts.add(postURL);
            }
        }));
        toJson(outputDirectory.resolve("blog_overview.json"), blog.getOverview());

        logger.info("Parsed {} posts", blog.getEntries().size());
        failedPosts.stream().forEach((failed) -> {
            logger.warn("Parsing failed for post: " + failed);
        });
        offlinePosts.stream().forEach((failed) -> {
            logger.info("Blog post is offline: " + failed);
        });
        toJson(outputDirectory.resolve("blog_posts.json"), blog.getEntries());
    }

    protected static void parseTV(final Path outputDirectory, final URLParser parser)
            throws ParseException, IOException {
        final TVParser tvParser = new TVParser();
        final List<URL> failedPosts = new ArrayList<>();
        final List<URL> offlinePosts = new ArrayList<>();
        TV tv = tvParser.parse(parser, Optional.of(new FullPageParseListener<TVEntry>() {
            @Override
            public void onEntry(TVEntry entry) {
            }

            @Override
            public void onEntryFailed(URL postURL, Throwable ex) {
                failedPosts.add(postURL);
            }

            @Override
            public void onEntryOffline(URL postURL) {
                offlinePosts.add(postURL);
            }
        }));
        toJson(outputDirectory.resolve("tv_overview.json"), tv.getOverview());

        logger.info("Parsed {} tv entries", tv.getEntries().size());
        failedPosts.stream().forEach((failed) -> {
            logger.warn("Parsing failed for tv entry: " + failed);
        });
        offlinePosts.stream().forEach((failed) -> {
            logger.info("TV entry is offline: " + failed);
        });
        toJson(outputDirectory.resolve("tv_posts.json"), tv.getEntries());
    }

    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.registerModule(new Jdk8Module());
        mapper.registerModule(new JavaTimeModule());
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
    }

    protected static void toJson(Path file, Object data) throws IOException {
        logger.info("Writing json file: " + file.toAbsolutePath());
        if (!Files.exists(file.getParent())) {
            logger.info("Creating directory: " + file.getParent());
            Files.createDirectories(file.getParent());
        }
        try (Writer w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            mapper.writeValue(w, data);
        }
    }
}
