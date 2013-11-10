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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.apache.log4j.Logger;

import xeus.jcl.exception.JclException;

/**
 * JarResources reads jar files and loads the class content/bytes in a HashMap
 * 
 * @author Kamran Zafar
 * 
 */
public class JarResources {

    protected Map<String, byte[]> jarEntryContents;

    private static Logger logger = Logger.getLogger( JarResources.class );

    /**
     * @throws IOException
     */
    public JarResources() {
        jarEntryContents = new HashMap<String, byte[]>();
    }

    /**
     * @param name
     * @return byte[]
     */
    public byte[] getResource(String name) {
        return jarEntryContents.get( name );
    }

    /**
     * Returns an immutable Map of all jar resources
     * 
     * @return Map
     */
    public Map<String, byte[]> getResources() {
        return Collections.unmodifiableMap( jarEntryContents );
    }

    /**
     * Reads the specified jar file
     * 
     * @param jarFile
     * @throws IOException
     */
    public void loadJar(String jarFile) throws IOException {
        if( logger.isTraceEnabled() )
            logger.trace( "Loading jar: " + jarFile );
        FileInputStream fis = new FileInputStream( jarFile );
        loadJar( fis );
        fis.close();
    }

    /**
     * Reads the jar file from a specified URL
     * 
     * @param url
     * @throws IOException
     */
    public void loadJar(URL url) throws IOException {
        if( logger.isTraceEnabled() )
            logger.trace( "Loading jar: " + url.toString() );
        InputStream in = url.openStream();
        loadJar( in );
        in.close();
    }

    /**
     * Load the jar contents from InputStream
     * 
     * @throws IOException
     */
    public void loadJar(InputStream jarStream) throws IOException {

        BufferedInputStream bis = null;
        JarInputStream jis = null;

        try {
            bis = new BufferedInputStream( jarStream );
            jis = new JarInputStream( bis );

            JarEntry jarEntry = null;
            while(( jarEntry = jis.getNextJarEntry() ) != null) {
                if( logger.isTraceEnabled() )
                    logger.trace( dump( jarEntry ) );

                if( jarEntry.isDirectory() ) {
                    continue;
                }

                if( jarEntryContents.containsKey( jarEntry.getName() ) ) {
                    if( !Configuration.supressCollisionException() )
                        throw new JclException( "Class/Resource " + jarEntry.getName() + " already loaded" );
                    else {
                        if( logger.isTraceEnabled() )
                            logger
                                    .trace( "Class/Resource " + jarEntry.getName()
                                            + " already loaded; ignoring entry..." );
                        continue;
                    }
                }

                if( logger.isTraceEnabled() )
                    logger.trace( "Entry Name: " + jarEntry.getName() + ", " + "Entry Size: " + jarEntry.getSize() );

                byte[] b = new byte[2048];
                ByteArrayOutputStream out = new ByteArrayOutputStream();

                int len = 0;
                while(( len = jis.read( b ) ) > 0) {
                    out.write( b, 0, len );
                }

                // add to internal resource HashMap
                jarEntryContents.put( jarEntry.getName(), out.toByteArray() );

                if( logger.isTraceEnabled() )
                    logger.trace( jarEntry.getName() + ": size=" + out.size() + " ,csize="
                            + jarEntry.getCompressedSize() );

                out.close();
            }
        } catch (NullPointerException e) {
            if( logger.isTraceEnabled() )
                logger.trace( "Done loading." );
        } finally {
            jis.close();
            bis.close();
        }
    }

    /**
     * For debugging
     * 
     * @param je
     * @return String
     */
    private String dump(JarEntry je) {
        StringBuffer sb = new StringBuffer();
        if( je.isDirectory() ) {
            sb.append( "d " );
        } else {
            sb.append( "f " );
        }

        if( je.getMethod() == JarEntry.STORED ) {
            sb.append( "stored   " );
        } else {
            sb.append( "defalted " );
        }

        sb.append( je.getName() );
        sb.append( "\t" );
        sb.append( "" + je.getSize() );
        if( je.getMethod() == JarEntry.DEFLATED ) {
            sb.append( "/" + je.getCompressedSize() );
        }

        return ( sb.toString() );
    }
}
