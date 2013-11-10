/**
 *  JCL (Jar Class Loader)
 *
 *  Copyright (C) 2009  Xeus Technologies
 *
 *  This file is part of Jar Class Loader (JCL).
 *  Jar Class Loader (JCL) is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  JarClassLoader is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with JCL.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  @author Kamran Zafar
 *
 *  Contact Info:
 *  Email:  xeus.man@gmail.com
 *  Web:    http://xeustech.blogspot.com
 */

package xeus.jcl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.log4j.Logger;

import xeus.jcl.exception.JclException;
import xeus.jcl.exception.ResourceNotFoundException;

/**
 * Class that builds a local classpath by loading resources from different
 * files/paths
 * 
 * @author Kamran Zafar
 * 
 */
public class ClasspathResources extends JarResources {

    private static Logger logger = Logger.getLogger( ClasspathResources.class );

    /**
     * Reads the resource content
     * 
     * @param resource
     * @throws IOException
     */
    private void loadResourceContent(String resource) throws IOException {
        File resourceFile = new File( resource );

        FileInputStream fis = new FileInputStream( resourceFile );

        byte[] content = new byte[(int) resourceFile.length()];
        fis.read( content );

        if( jarEntryContents.containsKey( resourceFile.getName() ) ) {
            if( !Configuration.supressCollisionException() )
                throw new JclException( "Resource " + resourceFile.getName() + " already loaded" );
            else {
                if( logger.isTraceEnabled() )
                    logger.trace( "Resource " + resourceFile.getName() + " already loaded; ignoring entry..." );
                return;
            }
        }

        fis.close();

        if( logger.isTraceEnabled() )
            logger.trace( "Loading resource: " + resourceFile.getName() );
        jarEntryContents.put( resourceFile.getName(), content );
    }

    /**
     * Attempts to load a remote resource (jars, properties files, etc)
     * 
     * @param url
     * @throws IOException
     */
    private void loadRemoteResource(URL url) throws IOException {
        if( logger.isTraceEnabled() )
            logger.trace( "Attempting to load a remote resource." );

        if( url.toString().toLowerCase().endsWith( ".jar" ) ) {
            loadJar( url );
            return;
        }

        InputStream stream = url.openStream();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        int byt;
        while(( ( byt = stream.read() ) != -1 )) {
            out.write( byt );
        }

        byte[] content = out.toByteArray();

        if( jarEntryContents.containsKey( url.toString() ) ) {
            if( !Configuration.supressCollisionException() )
                throw new JclException( "Resource " + url.toString() + " already loaded" );
            else {
                if( logger.isTraceEnabled() )
                    logger.trace( "Resource " + url.toString() + " already loaded; ignoring entry..." );
                return;
            }
        }

        if( logger.isTraceEnabled() )
            logger.trace( "Loading remote resource." );
        jarEntryContents.put( url.toString(), content );

        out.close();
        stream.close();
    }

    /**
     * Reads the class content
     * 
     * @param clazz
     * @param pack
     * @throws IOException
     */
    private void loadClassContent(String clazz, String pack) throws IOException {
        File cf = new File( clazz );
        FileInputStream fis = new FileInputStream( cf );

        byte[] content = new byte[(int) cf.length()];
        fis.read( content );

        String entryName = pack + "/" + cf.getName();

        if( jarEntryContents.containsKey( entryName ) ) {
            if( !Configuration.supressCollisionException() )
                throw new JclException( "Class " + entryName + " already loaded" );
            else {
                if( logger.isTraceEnabled() )
                    logger.trace( "Class " + entryName + " already loaded; ignoring entry..." );
                return;
            }
        }

        fis.close();

        if( logger.isTraceEnabled() )
            logger.trace( "Loading class: " + entryName );
        jarEntryContents.put( entryName, content );
    }

    /**
     * Reads local and remote resources
     * 
     * @param url
     * @throws IOException
     */
    public void loadResource(URL url) throws IOException {
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
     * @throws IOException
     */
    public void loadResource(String path) throws IOException {
        if( logger.isTraceEnabled() )
            logger.trace( "Resource: " + path );
        loadResource( new File( path ), "" );
    }

    /**
     * Reads local resources from - Jar files - Class folders - Jar Library
     * folders
     * 
     * @param fol
     * @param packName
     * @throws IOException
     */
    private void loadResource(File fol, String packName) throws IOException {
        if( fol.isFile() ) {
            if( fol.getName().toLowerCase().endsWith( ".class" ) ) {
                loadClassContent( fol.getAbsolutePath(), packName );
            } else {
                if( fol.getName().toLowerCase().endsWith( ".jar" ) ) {
                    loadJar( fol.getAbsolutePath() );
                } else {
                    loadResourceContent( fol.getAbsolutePath() );
                }
            }

            return;
        }

        if( fol.list() != null ) {
            for( String f : fol.list() ) {
                File fl = new File( fol.getAbsolutePath() + "/" + f );

                String pn = packName;

                if( fl.isDirectory() ) {

                    if( !pn.equals( "" ) )
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
     * @throws ResourceNotFoundException
     */
    public void unload(String resource) throws ResourceNotFoundException {
        if( jarEntryContents.containsKey( resource ) ) {
            if( logger.isTraceEnabled() )
                logger.trace( "Removing resource " + resource );
            jarEntryContents.remove( resource );
        } else {
            throw new ResourceNotFoundException( resource, "Resource not found in local ClasspathResources" );
        }
    }
}
