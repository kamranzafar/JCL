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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xeustechnologies.jcl.exception.JclException;

/**
 * JarResources reads jar files and loads the class content/bytes in a HashMap
 * 
 * @author Kamran Zafar
 * 
 */
public class JarResources {

    protected Map<String, JclJarEntry> jarEntryContents;
    protected boolean collisionAllowed;

    private final transient Logger logger = LoggerFactory.getLogger( JarResources.class );

    /**
     * Default constructor
     */
    public JarResources() {
        jarEntryContents = new HashMap<String, JclJarEntry>();
        collisionAllowed = Configuration.suppressCollisionException();
    }

    /**
     * @param name
     * @return URL
     */
    public URL getResourceURL(String name) {

      JclJarEntry entry = jarEntryContents.get(name);
        if (entry != null) {
          if (entry.getBaseUrl() == null) {
            throw new JclException( "non-URL accessible resource" );
        }          
            try {
                return new URL( entry.getBaseUrl().toString() + name );
            } catch (MalformedURLException e) {
                throw new JclException( e );
            }
        }

        return null;
    }

    /**
     * @param name
     * @return byte[]
     */
    public byte[] getResource(String name) {
      JclJarEntry entry = jarEntryContents.get(name);
      if (entry != null) {
        return entry.getResourceBytes();
      }
      else {
        return null;
      }
    }

    /**
     * Returns an immutable Map of all jar resources
     * 
     * @return Map
     */
    public Map<String, byte[]> getResources() {
      
      Map<String, byte[]> resourcesAsBytes = new HashMap<String, byte[]>(jarEntryContents.size());
      
      for (Map.Entry<String, JclJarEntry> entry : jarEntryContents.entrySet()) {
        resourcesAsBytes.put(entry.getKey(), entry.getValue().getResourceBytes());
      }

      return resourcesAsBytes;
    }

    /**
     * Reads the specified jar file
     * 
     * @param jarFile
     */
    public void loadJar(String jarFile) {
        logger.debug( "Loading jar: {}", jarFile );

        FileInputStream fis = null;
        try {
            File file = new File( jarFile );
            String baseUrl = "jar:" + file.toURI().toString() + "!/";
            fis = new FileInputStream( file );
            loadJar(baseUrl, fis);
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
     * Reads the jar file from a specified URL
     * 
     * @param url
     */
    public void loadJar(URL url) {
        logger.debug( "Loading jar: {}", url.toString() );

        InputStream in = null;
        try {
            String baseUrl = "jar:" + url.toString() + "!/";
            in = url.openStream();
            loadJar( baseUrl, in );
        } catch (IOException e) {
            throw new JclException( e );
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                    throw new JclException( e );
                }
        }
    }

    public void loadJar(String argBaseUrl, InputStream jarStream) {
        loadJar(argBaseUrl, jarStream, true);
    }

    /**
     * Load the jar contents from InputStream
     * @param argBaseUrl 
     * 
     */
    public void loadJar(String argBaseUrl, InputStream jarStream, boolean closeStream) {

        BufferedInputStream bis = null;
        JarInputStream jis = null;

        try {
            bis = new BufferedInputStream( jarStream );
            jis = new JarInputStream( bis );

            JarEntry jarEntry = null;
            while (( jarEntry = jis.getNextJarEntry() ) != null) {
                logger.debug( dump( jarEntry ) );

                if (jarEntry.isDirectory()) {
                    continue;
                }

                if (jarEntryContents.containsKey( jarEntry.getName() )) {
                    if (!collisionAllowed)
                        throw new JclException( "Class/Resource " + jarEntry.getName() + " already loaded" );
                    else {
                        logger.debug( "Class/Resource {} already loaded; ignoring entry...", jarEntry.getName() );
                        continue;
                    }
                }

                logger.debug( "Entry Name: {}, Entry Size: {}", jarEntry.getName(), jarEntry.getSize() );

                byte[] b = new byte[2048];
                ByteArrayOutputStream out = new ByteArrayOutputStream();

                int len = 0;
                while (( len = jis.read( b ) ) > 0) {
                    out.write( b, 0, len );
                }

                // add to internal resource HashMap
                JclJarEntry entry = new JclJarEntry();
                entry.setBaseUrl(argBaseUrl);
                entry.setResourceBytes(out.toByteArray());
                jarEntryContents.put( jarEntry.getName(), entry );

                logger.debug("{}: size={}, csize={}", jarEntry.getName(), out.size(), jarEntry.getCompressedSize());

                out.close();
            }
        } catch (IOException e) {
            throw new JclException( e );
        } catch (NullPointerException e) {
            logger.debug( "Done loading." );
        } finally {
            if(closeStream) {
                if (jis != null)
                    try {
                        jis.close();
                    } catch (IOException e) {
                        throw new JclException(e);
                    }

                if (bis != null)
                    try {
                        bis.close();
                    } catch (IOException e) {
                        throw new JclException(e);
                    }
            }
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
        if (je.isDirectory()) {
            sb.append( "d " );
        } else {
            sb.append( "f " );
        }

        if (je.getMethod() == JarEntry.STORED) {
            sb.append( "stored   " );
        } else {
            sb.append( "defalted " );
        }

        sb.append( je.getName() );
        sb.append( "\t" );
        sb.append( "" + je.getSize() );
        if (je.getMethod() == JarEntry.DEFLATED) {
            sb.append( "/" + je.getCompressedSize() );
        }

        return ( sb.toString() );
    }
}
