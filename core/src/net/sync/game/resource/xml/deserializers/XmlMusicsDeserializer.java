/*
 * Copyright (c) 2020 Vincenzo Fortunato.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.sync.game.resource.xml.deserializers;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import net.sync.game.resource.MapTheme;
import net.sync.game.resource.lazy.MusicResource;
import net.sync.game.resource.lazy.Resource;
import net.sync.game.resource.xml.XmlReferenceNotCompatibleException;
import net.sync.game.resource.xml.resolvers.XmlReferenceResolver;
import net.sync.game.util.xml.XmlDeserializeException;
import net.sync.game.util.xml.XmlElement;
import net.sync.game.util.xml.XmlParser;

public class XmlMusicsDeserializer extends XmlMapResourceDeserializer<Resource<Music>> {
    private static final String RESOURCE_ROOT_NAME = "musics";
    private static final String RESOURCE_TYPE_NAME = "music";

    /**
     * Creates a musics resource deserializer.
     * @param parser the XML parser.
     * @param file the resource file.
     * @param theme the theme.
     */
    public XmlMusicsDeserializer(XmlParser parser, FileHandle file, MapTheme theme) {
        super(parser, file, RESOURCE_ROOT_NAME);
    }


    @Override
    protected void validateRootChild(XmlElement element) {
        if(!element.getName().equals(RESOURCE_TYPE_NAME)) {
            throw new XmlDeserializeException(String.format(
                    "Unexpected element name '%s'! Expected to be '%s'!", element.getName(), RESOURCE_TYPE_NAME));
        }
    }

    @Override
    protected XmlReferenceResolver<Resource<Music>> getResolver(XmlElement element) {
        return musicResolver;
    }

    /* Resolvers */

    private XmlReferenceResolver<Resource<Music>> musicResolver = XmlReferenceResolver.of(
            //Create a music resource from the given file located in /musics/
            fileName -> new MusicResource(getFile().sibling("musics").sibling(fileName)),
            resourceId -> {
                Resource<Music> resource = getResolvedValueOrThrow(resourceId);
                if(resource instanceof MusicResource)
                    return new MusicResource((MusicResource) resource);
                throw new XmlReferenceNotCompatibleException(resource.getClass(), MusicResource.class);
            },
            RESOURCE_TYPE_NAME);

}
