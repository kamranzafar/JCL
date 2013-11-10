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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import xeus.jcl.exception.JclException;
import xeus.jcl.exception.ResourceNotFoundException;
import xeus.jcl.loader.Loader;

/**
 * Reads the class bytes from jar files and other resources using
 * ClasspathResources
 * 
 * @author Kamran Zafar
 * 
 */
@SuppressWarnings("unchecked")
public class JarClassLoader extends AbstractClassLoader {
    protected final Map<String, Class> classes;
    protected final ClasspathResources classpathResources;
    protected char classNameReplacementChar;
    private static Logger logger = Logger.getLogger( JarClassLoader.class );
    private final Loader localLoader = new LocalLoader();

    public JarClassLoader() {
        classpathResources = new ClasspathResources();
        classes = Collections.synchronizedMap( new HashMap<String, Class>() );
        loaders.add( localLoader );
    }

    /**
     * Loads classes from different sources
     * 
     * @param resources
     * @throws IOException
     */
    public JarClassLoader(Object[] resources) throws IOException {
        this();

        for( Object resource : resources ) {
            if( resource instanceof InputStream )
                add( (InputStream) resource );
            else if( resource instanceof URL )
                add( (URL) resource );
            else if( resource instanceof String )
                add( (String) resource );
            else
                throw new JclException( "Unknown Resource type" );
        }
    }

    /**
     * Loads local/remote resource
     * 
     * @param resourceName
     * @throws IOException
     */
    public void add(String resourceName) throws IOException {
        classpathResources.loadResource( resourceName );
    }

    /**
     * Loads resource from InputStream
     * 
     * @param jarStream
     * @throws IOException
     */
    public void add(InputStream jarStream) throws IOException {
        classpathResources.loadJar( jarStream );
    }

    /**
     * Loads local/remote resource
     * 
     * @param url
     * @throws IOException
     */
    public void add(URL url) throws IOException {
        classpathResources.loadResource( url );
    }

    /**
     * 
     * Reads the class bytes from different local and remote resources using
     * ClasspathResources
     * 
     * @param className
     * @return class bytes
     */
    protected byte[] loadClassBytes(String className) {
        className = formatClassName( className );

        return classpathResources.getResource( className );
    }

    /**
     * @param className
     * @return String
     */
    protected String formatClassName(String className) {
        if( classNameReplacementChar == '\u0000' ) {
            // '/' is used to map the package to the path
            return className.replace( '.', '/' ) + ".class";
        } else {
            // Replace '.' with custom char, such as '_'
            return className.replace( '.', classNameReplacementChar ) + ".class";
        }
    }

    /**
     * Attempts to unload class, it only unloads the locally loaded classes by
     * JCL
     * 
     * @param className
     */
    public void unloadClass(String className) {
        if( logger.isTraceEnabled() )
            logger.trace( "Unloading class " + className );

        if( classes.containsKey( className ) ) {
            if( logger.isTraceEnabled() )
                logger.trace( "Removing loaded class " + className );
            classes.remove( className );
            try {
                classpathResources.unload( formatClassName( className ) );
            } catch (ResourceNotFoundException e) {
                throw new JclException( "Something is very wrong!!!"
                        + "The locally loaded classes must be in synch with ClasspathResources", e );
            }
        } else {
            try {
                classpathResources.unload( formatClassName( className ) );
            } catch (ResourceNotFoundException e) {
                throw new JclException( "Class could not be unloaded "
                        + "[Possible reason: Class belongs to the system]", e );
            }
        }
    }

    /**
     * @param replacement
     */
    public void setClassNameReplacementChar(char replacement) {
        classNameReplacementChar = replacement;
    }

    /**
     * @return char
     */
    public char getClassNameReplacementChar() {
        return classNameReplacementChar;
    }

    /**
     * Local class loader
     * 
     */
    class LocalLoader extends Loader {

        private final Logger logger = Logger.getLogger( LocalLoader.class );

        public LocalLoader() {
            order = 1;
            enabled = Configuration.isLocalLoaderEnabled();
        }

        @Override
        public Class load(String className, boolean resolveIt) {
            Class result = null;
            byte[] classBytes;
            // if( logger.isTraceEnabled() )
            // logger.trace( "Loading class: " + className + ", " + resolveIt +
            // "" );

            result = classes.get( className );
            if( result != null ) {
                if( logger.isTraceEnabled() )
                    logger.trace( "Returning local loaded class " + className );
                return result;
            }

            classBytes = loadClassBytes( className );
            if( classBytes == null ) {
                return null;
            }

            result = defineClass( className, classBytes, 0, classBytes.length );

            if( result == null ) {
                return null;
            }

            if( resolveIt )
                resolveClass( result );

            classes.put( className, result );
            if( logger.isTraceEnabled() )
                logger.trace( "Return newly loaded class " + className );
            return result;
        }

        @Override
        public InputStream loadResource(String name) {
            byte[] arr = classpathResources.getResource( name );
            if( arr != null ) {
                if( logger.isTraceEnabled() )
                    logger.trace( "Returning newly loaded resource " + name );

                return new ByteArrayInputStream( arr );
            }

            return null;
        }
    }

    /**
     * @return Local JCL Loader
     */
    public Loader getLocalLoader() {
        return localLoader;
    }

    /**
     * Returns all JCL-loaded-classes as an immutable Map
     * 
     * @return Map
     */
    public Map<String, Class> getLoadedClasses() {
        return Collections.unmodifiableMap( classes );
    }
}
