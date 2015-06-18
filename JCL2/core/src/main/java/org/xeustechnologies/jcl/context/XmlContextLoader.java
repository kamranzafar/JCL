/**
 *
 * Copyright 2015 Kamran Zafar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.xeustechnologies.jcl.context;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xeustechnologies.jcl.AbstractClassLoader;
import org.xeustechnologies.jcl.JarClassLoader;
import org.xeustechnologies.jcl.ProxyClassLoader;
import org.xeustechnologies.jcl.exception.JclContextException;
import org.xeustechnologies.jcl.utils.PathResolver;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * The class loads the JclContext from XML file. See the documentation and
 * schema for more details on how to write the JCL context xml.
 * 
 * @author Kamran
 * 
 */
public class XmlContextLoader implements JclContextLoader {
    private static final String CLASSPATH = "classpath:";
    private static final String ELEMENT_JCL = "jcl";
    private static final String ELEMENT_SOURCES = "sources";
    private static final String ELEMENT_SOURCE = "source";
    private static final String ELEMENT_LOADERS = "loaders";
    private static final String ELEMENT_LOADER = "loader";
    private static final String ELEMENT_ENABLED = "enabled";
    private static final String ELEMENT_ORDER = "order";
    private static final String ELEMENT_STRICT = "strict";
    private static final String ELEMENT_BOOT_DELEGATION = "bootDelegation";
    private static final String ATTRIBUTE_CLASS = "class";
    private static final String ATTRIBUTE_NAME = "name";

    private static final String JCL_BOOTOSGI = "jcl.bootosgi";
    private static final String JCL_SYSTEM = "jcl.system";
    private static final String JCL_THREAD = "jcl.thread";
    private static final String JCL_LOCAL = "jcl.local";
    private static final String JCL_CURRENT = "jcl.current";
    private static final String JCL_PARENT = "jcl.parent";

    private static final String XML_SCHEMA_LANG = "http://www.w3.org/2001/XMLSchema";
    private static final String JCL_CONTEXT_SCHEMA = "org/xeustechnologies/jcl/context/jcl-context.xsd";

    private final String file;
    private final JclContext jclContext;

    private final List<PathResolver> pathResolvers = new ArrayList<PathResolver>();

    private final transient Logger logger = LoggerFactory.getLogger(XmlContextLoader.class);

    public XmlContextLoader(String file) {
        this.file = file;
        jclContext = new JclContext();
    }

    /**
     * Loads the JCL context from XML file
     * 
     * @see org.xeustechnologies.jcl.context.JclContextLoader#loadContext()
     */ 
    public void loadContext() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating( false );
        factory.setNamespaceAware( true );

        SchemaFactory schemaFactory = SchemaFactory.newInstance( XML_SCHEMA_LANG );

        try {
            factory.setSchema( schemaFactory.newSchema( new Source[] { new StreamSource( getClass().getClassLoader()
                    .getResourceAsStream( JCL_CONTEXT_SCHEMA ) ) } ) );
        } catch (SAXException e) {
            throw new JclContextException( e );
        }

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document d = null;

            if (file.startsWith( CLASSPATH ))
                d = builder.parse( getClass().getClassLoader().getResourceAsStream( file.split( CLASSPATH )[1] ) );
            else {
                d = builder.parse( file );
            }

            NodeList nl = d.getElementsByTagName( ELEMENT_JCL );
            for (int i = 0; i < nl.getLength(); i++) {
                Node n = nl.item( i );

                String name = n.getAttributes().getNamedItem( ATTRIBUTE_NAME ).getNodeValue();

                JarClassLoader jcl = new JarClassLoader();

                NodeList config = n.getChildNodes();

                for (int j = 0; j < config.getLength(); j++) {
                    Node c = config.item( j );
                    if (c.getNodeName().equals( ELEMENT_LOADERS )) {
                        processLoaders( jcl, c );
                    } else if (c.getNodeName().equals( ELEMENT_SOURCES )) {
                        processSources( jcl, c );
                    }
                }

                jclContext.addJcl( name, jcl );

                logger.debug( "JarClassLoader[{}] loaded into context.", name );
            }

        } catch (SAXParseException e) {
            JclContextException we = new JclContextException( e.getMessage() + " [" + file + " (" + e.getLineNumber()
                    + ", " + e.getColumnNumber() + ")]" );
            we.setStackTrace( e.getStackTrace() );

            throw we;
        } catch (JclContextException e) {
            throw e;
        } catch (Exception e) {
            throw new JclContextException( e );
        }
    }

    /**
     * Unloads the context
     * 
     * @see org.xeustechnologies.jcl.context.JclContextLoader#unloadContext()
     */ 
    public void unloadContext() {
        JclContext.destroy();
    }

    private void processSources(JarClassLoader jcl, Node c) {
        NodeList sources = c.getChildNodes();
        for (int k = 0; k < sources.getLength(); k++) {
            Node s = sources.item( k );

            if (s.getNodeName().equals( ELEMENT_SOURCE )) {
                String path = s.getTextContent();
                Object[] res = null;

                for (PathResolver pr : pathResolvers) {
                    res = pr.resolvePath( path );

                    if (res != null) {
                        for (Object r : res)
                            jcl.add( r );

                        break;
                    }
                }

                if (res == null)
                    jcl.add( path );
            }
        }
    }

    private void processLoaders(JarClassLoader jcl, Node c) {
        NodeList loaders = c.getChildNodes();
        for (int k = 0; k < loaders.getLength(); k++) {
            Node l = loaders.item( k );
            if (l.getNodeName().equals( ELEMENT_LOADER )) {
                if (l.getAttributes().getNamedItem( ATTRIBUTE_NAME ).getNodeValue().equals( JCL_PARENT )) {
                    processLoader( jcl.getParentLoader(), l );
                } else if (l.getAttributes().getNamedItem( ATTRIBUTE_NAME ).getNodeValue().equals( JCL_CURRENT )) {
                    processLoader( jcl.getCurrentLoader(), l );
                } else if (l.getAttributes().getNamedItem( ATTRIBUTE_NAME ).getNodeValue().equals( JCL_LOCAL )) {
                    processLoader( jcl.getLocalLoader(), l );
                } else if (l.getAttributes().getNamedItem( ATTRIBUTE_NAME ).getNodeValue().equals( JCL_THREAD )) {
                    processLoader( jcl.getThreadLoader(), l );
                } else if (l.getAttributes().getNamedItem( ATTRIBUTE_NAME ).getNodeValue().equals( JCL_SYSTEM )) {
                    processLoader( jcl.getSystemLoader(), l );
                } else if (l.getAttributes().getNamedItem( ATTRIBUTE_NAME ).getNodeValue().equals( JCL_BOOTOSGI )) {
                    processLoader( jcl.getOsgiBootLoader(), l );
                } else {
                    Objenesis objenesis = new ObjenesisStd();

                    Class<?> clazz = null;
                    try {
                        clazz = getClass().getClassLoader().loadClass(
                                l.getAttributes().getNamedItem( ATTRIBUTE_CLASS ).getNodeValue() );
                    } catch (Exception e) {
                        throw new JclContextException( e );
                    }

                    ProxyClassLoader pcl = (ProxyClassLoader) objenesis.newInstance( clazz );
                    jcl.addLoader( pcl );

                    processLoader( pcl, l );
                }
            }
        }
    }

    private void processLoader(ProxyClassLoader loader, Node node) {
        NodeList oe = node.getChildNodes();
        for (int i = 0; i < oe.getLength(); i++) {
            Node noe = oe.item( i );
            if (noe.getNodeName().equals( ELEMENT_ORDER ) && !( loader instanceof AbstractClassLoader.OsgiBootLoader )) {
                loader.setOrder( Integer.parseInt( noe.getTextContent() ) );
            } else if (noe.getNodeName().equals( ELEMENT_ENABLED )) {
                loader.setEnabled( Boolean.parseBoolean( noe.getTextContent() ) );
            } else if (noe.getNodeName().equals( ELEMENT_STRICT )
                    && loader instanceof AbstractClassLoader.OsgiBootLoader) {
                ( (AbstractClassLoader.OsgiBootLoader) loader ).setStrictLoading( Boolean.parseBoolean( noe
                        .getTextContent() ) );
            } else if (noe.getNodeName().equals( ELEMENT_BOOT_DELEGATION )
                    && loader instanceof AbstractClassLoader.OsgiBootLoader) {
                ( (AbstractClassLoader.OsgiBootLoader) loader ).setBootDelagation( noe.getTextContent().split( "," ) );
            }
        }

        logger.debug( "Loader[{}] configured: [{}, {}]", loader.getClass().getName(), loader.getOrder(), loader.isEnabled() );
    }

    public void addPathResolver(PathResolver pr) {
        pathResolvers.add( pr );
    }
}
