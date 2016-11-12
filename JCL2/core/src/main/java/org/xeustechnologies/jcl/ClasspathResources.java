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
package org.xeustechnologies.jcl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xeustechnologies.jcl.exception.JclException;
import org.xeustechnologies.jcl.exception.ResourceNotFoundException;

/**
 * Class that builds a local classpath by loading resources from different
 * files/paths
 * 
 * @author Kamran Zafar
 * 
 */
public class ClasspathResources extends JarResources {

    private final transient Logger logger = LoggerFactory.getLogger(ClasspathResources.class);
    private boolean ignoreMissingResources;

    public ClasspathResources() {
        super();
        ignoreMissingResources = Configuration.suppressMissingResourceException();
    }

    /**
     * Reads the resource content
     * 
     * @param resource
     */
    private void loadResourceContent(String resource, String pack) {
        File resourceFile = new File( resource );
        String entryName = "";
        FileInputStream fis = null;
        byte[] content = null;
        try {
            fis = new FileInputStream( resourceFile );
            content = new byte[(int) resourceFile.length()];

            if (fis.read( content ) != -1) {

                if (pack.length() > 0) {
                    entryName = pack + "/";
                }

                entryName += resourceFile.getName();

                if (jarEntryContents.containsKey( entryName )) {
                    if (!collisionAllowed)
                        throw new JclException( "Resource " + entryName + " already loaded" );
                    else {
                        logger.debug( "Resource {} already loaded; ignoring entry...", entryName );
                        return;
                    }
                }

                logger.debug( "Loading resource: {}", entryName );
                
                JclJarEntry entry = new JclJarEntry();
                File parentFile = resourceFile.getAbsoluteFile().getParentFile();
                if (parentFile == null) {
                    // I don't believe this is actually possible with an absolute path. With no parent, we must be at the root of the filesystem.
                    entry.setBaseUrl("file:/");
                } else {
                    entry.setBaseUrl(parentFile.toURI().toString());
                }
                entry.setResourceBytes(content);

                jarEntryContents.put( entryName, entry );
            }
        } catch (IOException e) {
            throw new JclException( e );
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
                throw new JclException( e );
            }
        }
    }

    /**
     * Attempts to load a remote resource (jars, properties files, etc)
     * 
     * @param url
     */
    private void loadRemoteResource(URL url) {
        logger.debug( "Attempting to load a remote resource." );

        if (url.toString().toLowerCase().endsWith( ".jar" )) {
            loadJar( url );
            return;
        }

        InputStream stream = null;
        ByteArrayOutputStream out = null;
        try {
            stream = url.openStream();
            out = new ByteArrayOutputStream();

            int byt;
            while (( ( byt = stream.read() ) != -1 )) {
                out.write( byt );
            }

            byte[] content = out.toByteArray();

            if (jarEntryContents.containsKey( url.toString() )) {
                if (!collisionAllowed)
                    throw new JclException( "Resource " + url.toString() + " already loaded" );
                else {
                    logger.debug( "Resource {} already loaded; ignoring entry...", url.toString() );
                    return;
                }
            }

            logger.debug( "Loading remote resource." );
            
            JclJarEntry entry = new JclJarEntry();
            entry.setResourceBytes(content);
            jarEntryContents.put( url.toString(), entry );            
        } catch (IOException e) {
            throw new JclException( e );
        } finally {
            if (out != null)
                try {
                    out.close();
                } catch (IOException e) {
                    throw new JclException( e );
                }
            if (stream != null)
                try {
                    stream.close();
                } catch (IOException e) {
                    throw new JclException( e );
                }
        }
    }

    /**
     * Reads the class content
     * 
     * @param clazz
     * @param pack
     */
    private void loadClassContent(String clazz, String pack) {
        File cf = new File( clazz );
        FileInputStream fis = null;
        String entryName = "";
        byte[] content = null;

        try {
            fis = new FileInputStream( cf );
            content = new byte[(int) cf.length()];

            if (fis.read( content ) != -1) {
                entryName = pack + "/" + cf.getName();

                if (jarEntryContents.containsKey( entryName )) {
                    if (!collisionAllowed)
                        throw new JclException( "Class " + entryName + " already loaded" );
                    else {
                        logger.debug( "Class {} already loaded; ignoring entry...", entryName );
                        return;
                    }
                }

                logger.debug( "Loading class: {}", entryName );
                
                JclJarEntry entry = new JclJarEntry();
                entry.setResourceBytes(content);
                jarEntryContents.put( entryName, entry );                
            }
        } catch (IOException e) {
            throw new JclException( e );
        } finally {
            if (fis != null)
                try {
                    fis.close();
                } catch (IOException e) {
                    throw new JclException( e );
                }
        }

    }

    /**
     * Reads local and remote resources
     * 
     * @param url
     */
    public void loadResource(URL url) {
        try {
            // Is Local
            loadResource( new File( url.toURI() ), "" );
        } catch (IllegalArgumentException iae) {
            // Is Remote
            loadRemoteResource( url );
        } catch (URISyntaxException e) {
            throw new JclException( "URISyntaxException", e );
        }
    }

    /**
     * Reads local resources from - Jar files - Class folders - Jar Library
     * folders
     * 
     * @param path
     */
    public void loadResource(String path) {
        logger.debug( "Resource: {}", path );

        File fp = new File( path );

        if (!fp.exists() && !ignoreMissingResources) {
            throw new JclException( "File/Path does not exist" );
        }

        loadResource( fp, "" );
    }

    /**
     * Reads local resources from - Jar files - Class folders - Jar Library
     * folders
     * 
     * @param fol
     * @param packName
     */
    private void loadResource(File fol, String packName) {
        if (fol.isFile()) {
            if (fol.getName().toLowerCase().endsWith( ".class" )) {
                loadClassContent( fol.getAbsolutePath(), packName );
            } else {
                if (fol.getName().toLowerCase().endsWith( ".jar" )) {
                    loadJar( fol.getAbsolutePath() );
                } else {
                    loadResourceContent( fol.getAbsolutePath(), packName );
                }
            }

            return;
        }

        if (fol.list() != null) {
            for (String f : fol.list()) {
                File fl = new File( fol.getAbsolutePath() + "/" + f );

                String pn = packName;

                if (fl.isDirectory()) {

                    if (!pn.equals( "" ))
                        pn = pn + "/";

                    pn = pn + fl.getName();
                }

                loadResource( fl, pn );
            }
        }
    }

    /**
     * Removes the loaded resource
     * 
     * @param resource
     */
    public void unload(String resource) {
        if (jarEntryContents.containsKey( resource )) {
            logger.debug( "Removing resource {}", resource );
            jarEntryContents.remove( resource );
        } else {
            throw new ResourceNotFoundException( resource, "Resource not found in local ClasspathResources" );
        }
    }

    public boolean isCollisionAllowed() {
        return collisionAllowed;
    }

    public void setCollisionAllowed(boolean collisionAllowed) {
        this.collisionAllowed = collisionAllowed;
    }

    public boolean isIgnoreMissingResources() {
        return ignoreMissingResources;
    }

    public void setIgnoreMissingResources(boolean ignoreMissingResources) {
        this.ignoreMissingResources = ignoreMissingResources;
    }
}
