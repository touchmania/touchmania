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

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import net.sync.game.resource.MapTheme;
import net.sync.game.resource.lazy.Resource;
import net.sync.game.resource.lazy.SoundResource;
import net.sync.game.resource.xml.XmlReferenceNotCompatibleException;
import net.sync.game.resource.xml.resolvers.XmlReferenceResolver;
import net.sync.game.util.xml.XmlDeserializeException;
import net.sync.game.util.xml.XmlElement;
import net.sync.game.util.xml.XmlParser;

public class XmlSoundsDeserializer extends XmlMapResourceDeserializer<Resource<Sound>> {
    private static final String RESOURCE_ROOT_NAME = "sounds";
    private static final String RESOURCE_TYPE_NAME = "sound";

    /**
     * Creates a sounds resource deserializer.
     * @param parser the XML parser.
     * @param file the resource file.
     * @param theme the theme.
     */
    public XmlSoundsDeserializer(XmlParser parser, FileHandle file, MapTheme theme) {
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
    protected XmlReferenceResolver<Resource<Sound>> getResolver(XmlElement element) {
        return soundResolver;
    }

    /* Resolvers */

    private XmlReferenceResolver<Resource<Sound>> soundResolver = XmlReferenceResolver.of(
            //Create a sound resource from the given file located in /sounds/
            fileName -> new SoundResource(getFile().sibling("sounds").sibling(fileName)),
            resourceId -> {
                Resource<Sound> resource = getResolvedValueOrThrow(resourceId);
                if(resource instanceof SoundResource)
                    return new SoundResource((SoundResource) resource);
                throw new XmlReferenceNotCompatibleException(resource.getClass(), SoundResource.class);
            },
            RESOURCE_TYPE_NAME);
}
