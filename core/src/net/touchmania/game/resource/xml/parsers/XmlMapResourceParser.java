/*
 * Copyright 2018 Vincenzo Fortunato
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

package net.touchmania.game.resource.xml.parsers;

import com.badlogic.gdx.files.FileHandle;
import net.touchmania.game.resource.xml.resolvers.XmlReferenceValueResolver;
import net.touchmania.game.util.xml.XmlParseException;
import net.touchmania.game.util.xml.XmlParser;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static net.touchmania.game.resource.xml.resolvers.XmlIdentifierResolver.GLOBAL_IDENTIFIER_RESOLVER;

/**
 * Parses resources that presents data as a map. The xml resource must
 * have only one root element and root element's children must have an id
 * and no children (children are ignored). Additional xml elements attributes
 * are parsed when the value is resolved.
 * @param <T> the type of the resource.
 */
public abstract class XmlMapResourceParser<T> extends XmlResourceParser<Map<String, T>> {
    private Map<String, T> resolved;
    private Map<String, UnresolvedResource> unresolved;

    /**
     * Create a resource parser from its file.
     * @param resourceFile the resource file.
     */
    public XmlMapResourceParser(FileHandle resourceFile) {
        super(resourceFile);
    }

    @Override
    public Map<String, T> parse(XmlParser.Element root) throws XmlParseException {
        //Initialize maps
        resolved = new HashMap<>(root.getChildCount());
        unresolved = new HashMap<>();

        for(int i = 0; i < root.getChildCount(); i++) {
            XmlParser.Element element = root.getChild(i);
            checkRootChild(element);

            //Get and validate id
            String id = GLOBAL_IDENTIFIER_RESOLVER.resolve(element.getAttribute("id"));

            //Check for duplicates
            if(resolved.containsKey(id) || unresolved.containsKey(id)) {
                throw new XmlParseException(String.format("Duplicated id '%s'", id));
            }

            //Attempt to resolve the resource value
            XmlReferenceValueResolver<T> resolver = getResolver(element);
            T value = resolver.resolve(element.getText());
            if(value != null) {
                //Mark as resolved
                markResolved(id, value, element);
            } else {
                //Mark as unresolved
                markUnresolved(id, resolver.getReferenceId(element.getText()), element);
            }
        }

        if(!unresolved.isEmpty()) {
            //Unresolved resources
            StringBuilder exceptionMessage = new StringBuilder();
            exceptionMessage.append("Referencing undeclared resources. Unresolved ids: ");
            Iterator<String> it = unresolved.keySet().iterator();
            while(it.hasNext()) {
                exceptionMessage.append(it.next());
                if(it.hasNext()) {
                    exceptionMessage.append(", ");
                }
            }
            throw new XmlParseException(exceptionMessage.toString());
        }

        return resolved;
    }

    private void markResolved(String id, T value, XmlParser.Element element) throws XmlParseException {
        resolved.put(id, value);

        if(element != null && element.getAttributes().size > 1) {
            //Has additional attributes
            parseAttributes(id, value, element);
        }

        if(!unresolved.isEmpty()) {
            //Attempt to resolve unresolved resources
            String unresolvedId;
            while((unresolvedId = findUnresolvedId(id)) != null) {
                //Remove from unresolved
                UnresolvedResource resource = unresolved.remove(unresolvedId);
                //Use resolver to resolve reference and create a copy of the referenced value
                markResolved(unresolvedId, getResolver(resource.element).resolveReference(resource.referenceId), resource.element);
            }
        }
    }

    private void markUnresolved(String id, String referenceId, XmlParser.Element element) throws XmlParseException {
        unresolved.put(id, new UnresolvedResource(referenceId, element));
    }

    private String findUnresolvedId(String referenceId) {
        for(Map.Entry<String, UnresolvedResource> entry : unresolved.entrySet()) {
            if(entry.getValue().referenceId.equals(referenceId)) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Checks root element child's validity
     * @throws XmlParseException if the child is invalid.
     */
    protected abstract void checkRootChild(XmlParser.Element element) throws XmlParseException;

    /**
     * Gets a resolver for the given xml element.
     * @param element the element that has the value to resolve.
     * @return the resolver.
     */
    protected abstract XmlReferenceValueResolver<T> getResolver(XmlParser.Element element);

    /**
     * Parse additional element attributes onto the given value of the resource with the given id.
     * @param id the resource id.
     * @param value the resource value.
     * @param element the xml element.
     */
    protected void parseAttributes(String id, T value, XmlParser.Element element) throws XmlParseException {}

    /**
     * Gets the map containing all the resolved resources, where the key
     * is the resource id and the value is the resource value.
     * @return the resolved resources map.
     */
    protected Map<String, T> getResolvedValues() {
        return resolved;
    }

    private static class UnresolvedResource {
        public String referenceId;
        public XmlParser.Element element;

        public UnresolvedResource(String referenceId, XmlParser.Element element) {
            this.referenceId = referenceId;
            this.element = element;
        }
    }
}