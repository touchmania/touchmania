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

package net.sync.game.resource.xml.deserializers.actors;

import com.badlogic.gdx.scenes.scene2d.Group;
import net.sync.game.resource.xml.deserializers.XmlLayoutDeserializer;
import net.sync.game.util.xml.XmlDeserializeException;
import net.sync.game.util.xml.XmlParser;

public abstract class XmlGroupDeserializer<T extends Group> extends XmlActorDeserializer<T> {
    public XmlGroupDeserializer(XmlLayoutDeserializer layoutParser) {
        super(layoutParser);
    }

    @Override
    protected void parseChildren(T group, XmlParser.Element element) throws XmlDeserializeException {
        for(int i = 0; i < element.getChildCount(); i++) {
            XmlParser.Element child = element.getChild(i);
            XmlActorDeserializer<?> parser = getLayoutParser().getActorElementParser(child.getName());
            group.addActor(parser.parse(child));
        }
    }
}