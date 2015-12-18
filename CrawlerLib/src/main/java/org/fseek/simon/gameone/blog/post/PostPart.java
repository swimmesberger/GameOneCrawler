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
package org.fseek.simon.gameone.blog.post;

import java.net.URL;
import java.util.Optional;

import org.fseek.simon.gameone.util.Check;

public class PostPart {
    private final URL url;
    private final String content;
    private final PostPartMeta meta;
    private final Optional<URL> nextPost;

    public PostPart(URL url, String content, PostPartMeta meta, Optional<URL> nextPost) {
        Check.requireNonNull(url, content, meta, nextPost);
        this.url = url;
        this.content = content;
        this.meta = meta;
        this.nextPost = nextPost;
    }

    public URL getUrl() {
        return url;
    }

    public String getContent() {
        return content;
    }

    public PostPartMeta getMeta() {
        return meta;
    }

    public Optional<URL> getNextPost() {
        return nextPost;
    }
}
