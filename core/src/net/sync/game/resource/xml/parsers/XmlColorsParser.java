/*
 * Copyright 2020 Vincenzo Fortunato
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.sync.game.resource.xml.parsers;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import net.sync.game.resource.xml.XmlReferenceNotFoundException;
import net.sync.game.resource.xml.XmlTheme;
import net.sync.game.resource.xml.resolvers.XmlColorResolver;
import net.sync.game.resource.xml.resolvers.XmlReferenceResolver;
import net.sync.game.util.xml.XmlParseException;
import net.sync.game.util.xml.XmlParser;

public class XmlColorsParser extends XmlMapResourceParser<Color> {
    private net.sync.game.resource.xml.resolvers.XmlColorResolver colorResolver = new XmlColorResolver() {
        @Override
        public Color resolveReference(String resourceId) throws XmlReferenceNotFoundException {
            Color color = getResolvedValueOrThrow(resourceId);
            return new Color(color.r, color.g, color.b, color.a);
        }
    };

    /**
     * Create a resource parser from its file.
     * @param resourceFile the resource file.
     */
    public XmlColorsParser(FileHandle resourceFile, XmlTheme theme) {
        super(resourceFile);
    }

    @Override
    protected void checkRoot(XmlParser.Element root) throws XmlParseException {
        if(!root.getName().equals("colors")) {
            throw new XmlParseException("Unexpected xml root element name. Expected to be 'colors'!");
        }
    }

    @Override
    protected void checkRootChild(XmlParser.Element element) throws XmlParseException {
        if(!element.getName().equals("color")) {
            throw new XmlParseException(String.format("Unexpected element name '%s'! Expected to be 'color'!", element.getName()));
        }
    }

    @Override
    protected XmlReferenceResolver<Color> getResolver(XmlParser.Element element) {
        return colorResolver;
    }
}