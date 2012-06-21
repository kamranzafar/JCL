/**
 *  JCL (Jar Class Loader)
 *
 *  Copyright (C) 2011  Kamran Zafar
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

package org.xeustechnologies.jcl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xeustechnologies.jcl.exception.JclException;
import org.xeustechnologies.jcl.exception.ResourceNotFoundException;

/**
 * Reads the class bytes from jar files and other resources using
 * ClasspathResources
 * 
 * @author Kamran Zafar
 * 
 */
@SuppressWarnings("unchecked")
public class JarClassLoader extends AbstractClassLoader {
    /**
     * Class cache
     */
    protected final Map<String, Class> classes;

    protected final ClasspathResources classpathResources;
    private char classNameReplacementChar;
    private final ProxyClassLoader localLoader = new LocalLoader();

    private static Logger logger = Logger.getLogger( JarClassLoader.class.getName() );

    public JarClassLoader() {
        classpathResources = new ClasspathResources();
        classes = Collections.synchronizedMap( new HashMap<String, Class>() );
        initialize();
    }

    /**
     * Some initialisations
     * 
     */
    public void initialize() {
        loaders.add( localLoader );
    }

    /**
     * Loads classes from different sources
     * 
     * @param sources
     */
    public JarClassLoader(Object[] sources) {
        this();
        addAll( sources );
    }

    /**
     * Loads classes from different sources
     * 
     * @param sources
     */
    public JarClassLoader(List sources) {
        this();
        addAll( sources );
    }

    /**
     * Add all jar/class sources
     * 
     * @param sources
     */
    public void addAll(Object[] sources) {
        for (Object source : sources) {
            add( source );
        }
    }

    /**
     * Add all jar/class sources
     * 
     * @param sources
     */
    public void addAll(List sources) {
        for (Object source : sources) {
            add( source );
        }
    }

    /**
     * Loads local/remote source
     * 
     * @param source
     */
    public void add(Object source) {
        if (source instanceof InputStream)
            add( (InputStream) source );
        else if (source instanceof URL)
            add( (URL) source );
        else if (source instanceof String)
            add( (String) source );
        else
            throw new JclException( "Unknown Resource type" );

    }

    /**
     * Loads local/remote resource
     * 
     * @param resourceName
     */
    public void add(String resourceName) {
        classpathResources.loadResource( resourceName );
    }

    /**
     * Loads classes from InputStream
     * 
     * @param jarStream
     */
    public void add(InputStream jarStream) {
        classpathResources.loadJar( jarStream );
    }

    /**
     * Loads local/remote resource
     * 
     * @param url
     */
    public void add(URL url) {
        classpathResources.loadResource( url );
    }

    /**
     * Reads the class bytes from different local and remote resources using
     * ClasspathResources
     * 
     * @param className
     * @return byte[]
     */
    protected byte[] loadClassBytes(String className) {
        className = formatClassName( className );

        return classpathResources.getResource( className );
    }

    /**
     * Attempts to unload class, it only unloads the locally loaded classes by
     * JCL
     * 
     * @param className
     */
    public void unloadClass(String className) {
        if (logger.isLoggable( Level.FINEST ))
            logger.finest( "Unloading class " + className );

        if (classes.containsKey( className )) {
            if (logger.isLoggable( Level.FINEST ))
                logger.finest( "Removing loaded class " + className );
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
     * @param className
     * @return String
     */
    protected String formatClassName(String className) {
        className = className.replace( '/', '~' );

        if (classNameReplacementChar == '\u0000') {
            // '/' is used to map the package to the path
            className = className.replace( '.', '/' ) + ".class";
        } else {
            // Replace '.' with custom char, such as '_'
            className = className.replace( '.', classNameReplacementChar ) + ".class";
        }

        className = className.replace( '~', '/' );
        return className;
    }

    /**
     * Local class loader
     * 
     */
    class LocalLoader extends ProxyClassLoader {

        private final Logger logger = Logger.getLogger( LocalLoader.class.getName() );

        public LocalLoader() {
            order = 10;
            enabled = Configuration.isLocalLoaderEnabled();
        }

        @Override
        public Class loadClass(String className, boolean resolveIt) {
            Class result = null;
            byte[] classBytes;

            result = classes.get( className );
            if (result != null) {
                if (logger.isLoggable( Level.FINEST ))
                    logger.finest( "Returning local loaded class [" + className + "] from cache" );
                return result;
            }

            classBytes = loadClassBytes( className );
            if (classBytes == null) {
                return null;
            }

            result = defineClass( className, classBytes, 0, classBytes.length );

            if (result == null) {
                return null;
            }

            /*
             * Preserve package name.
             */
            if (result.getPackage() == null) {
                int lastDotIndex = className.lastIndexOf( '.' );
                String packageName = (lastDotIndex >= 0) ? className.substring( 0, lastDotIndex) : "";
                definePackage( packageName, null, null, null, null, null, null, null );
            }

            if (resolveIt)
                resolveClass( result );

            classes.put( className, result );
            if (logger.isLoggable( Level.FINEST ))
                logger.finest( "Return new local loaded class " + className );
            return result;
        }

        @Override
        public InputStream loadResource(String name) {
            byte[] arr = classpathResources.getResource( name );
            if (arr != null) {
                if (logger.isLoggable( Level.FINEST ))
                    logger.finest( "Returning newly loaded resource " + name );

                return new ByteArrayInputStream( arr );
            }

            return null;
        }
    }

    public char getClassNameReplacementChar() {
        return classNameReplacementChar;
    }

    public void setClassNameReplacementChar(char classNameReplacementChar) {
        this.classNameReplacementChar = classNameReplacementChar;
    }

    /**
     * Returns all loaded classes and resources
     * 
     * @return Map
     */
    public Map<String, byte[]> getLoadedResources() {
        return classpathResources.getResources();
    }

    /**
     * @return Local JCL ProxyClassLoader
     */
    public ProxyClassLoader getLocalLoader() {
        return localLoader;
    }

    /**
     * Returns all JCL-loaded classes as an immutable Map
     * 
     * @return Map
     */
    public Map<String, Class> getLoadedClasses() {
        return Collections.unmodifiableMap( classes );
    }
}
