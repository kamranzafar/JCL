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

package org.xeustechnologies.jcl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.xeustechnologies.jcl.exception.JclException;
import org.xeustechnologies.jcl.exception.ResourceNotFoundException;
import org.xeustechnologies.jcl.utils.Utils;

/**
 * Abstract class loader that can load classes from different resources
 * 
 * @author Kamran Zafar
 * 
 */
@SuppressWarnings("unchecked")
public abstract class AbstractClassLoader extends ClassLoader {

    private static final String JAVA_PACKAGE = "java.";

    protected final List<ProxyClassLoader> loaders = new ArrayList<ProxyClassLoader>();

    private final ProxyClassLoader systemLoader = new SystemLoader();
    private final ProxyClassLoader parentLoader = new ParentLoader();
    private final ProxyClassLoader currentLoader = new CurrentLoader();
    private final ProxyClassLoader threadLoader = new ThreadContextLoader();
    private final ProxyClassLoader osgiBootLoader = new OsgiBootLoader();

    /**
     * No arguments constructor
     */
    public AbstractClassLoader() {
        loaders.add( systemLoader );
        loaders.add( parentLoader );
        loaders.add( currentLoader );
        loaders.add( threadLoader );
        loaders.add( osgiBootLoader );
    }

    public void addLoader(ProxyClassLoader loader) {
        loaders.add( loader );
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.ClassLoader#loadClass(java.lang.String)
     */
    @Override
    public Class loadClass(String className) throws ClassNotFoundException {
        return ( loadClass( className, true ) );
    }

    /**
     * Overrides the loadClass method to load classes from other resources,
     * JarClassLoader is the only subclass in this project that loads classes
     * from jar files
     * 
     * @see java.lang.ClassLoader#loadClass(java.lang.String, boolean)
     */
    @Override
    public Class loadClass(String className, boolean resolveIt) throws ClassNotFoundException {
        if( className == null || className.trim().equals( "" ) )
            return null;

        Collections.sort( loaders );

        Class clazz = null;

        for( ProxyClassLoader l : loaders ) {
            if( l.isEnabled() ) {
                clazz = l.loadClass( className, resolveIt );
                if( clazz != null )
                    break;
            }
        }

        if( clazz == null )
            throw new ClassNotFoundException( className );

        return clazz;
    }

    /**
     * Overrides the getResourceAsStream method to load non-class resources from
     * other sources, JarClassLoader is the only subclass in this project that
     * loads non-class resources from jar files
     * 
     * @see java.lang.ClassLoader#getResourceAsStream(java.lang.String)
     */
    @Override
    public InputStream getResourceAsStream(String name) {
        if( name == null || name.trim().equals( "" ) )
            return null;

        Collections.sort( loaders );

        InputStream is = null;

        for( ProxyClassLoader l : loaders ) {
            if( l.isEnabled() ) {
                is = l.loadResource( name );
                if( is != null )
                    break;
            }
        }

        if( is == null )
            throw new ResourceNotFoundException( "Resource " + name + " not found." );

        return is;

    }

    /**
     * System class loader
     * 
     */
    class SystemLoader extends ProxyClassLoader {

        private final Logger logger = Logger.getLogger( SystemLoader.class );

        public SystemLoader() {
            order = 5;
            enabled = Configuration.isSystemLoaderEnabled();
        }

        @Override
        public Class loadClass(String className, boolean resolveIt) {
            Class result;

            try {
                result = findSystemClass( className );
            } catch (ClassNotFoundException e) {
                return null;
            }

            if( logger.isTraceEnabled() )
                logger.trace( "Returning system class " + className );

            return result;
        }

        @Override
        public InputStream loadResource(String name) {
            InputStream is = getSystemResourceAsStream( name );

            if( is != null ) {
                if( logger.isTraceEnabled() )
                    logger.trace( "Returning system resource " + name );

                return is;
            }

            return null;
        }
    }

    /**
     * Parent class loader
     * 
     */
    class ParentLoader extends ProxyClassLoader {
        private final Logger logger = Logger.getLogger( ParentLoader.class );

        public ParentLoader() {
            order = 3;
            enabled = Configuration.isParentLoaderEnabled();
        }

        @Override
        public Class loadClass(String className, boolean resolveIt) {
            Class result;

            try {
                result = getParent().loadClass( className );
            } catch (ClassNotFoundException e) {
                return null;
            }

            if( logger.isTraceEnabled() )
                logger.trace( "Returning class " + className + " loaded with parent classloader" );

            return result;
        }

        @Override
        public InputStream loadResource(String name) {
            InputStream is = getParent().getResourceAsStream( name );

            if( is != null ) {
                if( logger.isTraceEnabled() )
                    logger.trace( "Returning resource " + name + " loaded with parent classloader" );

                return is;
            }
            return null;
        }

    }

    /**
     * Current class loader
     * 
     */
    class CurrentLoader extends ProxyClassLoader {
        private final Logger logger = Logger.getLogger( CurrentLoader.class );

        public CurrentLoader() {
            order = 2;
            enabled = Configuration.isCurrentLoaderEnabled();
        }

        @Override
        public Class loadClass(String className, boolean resolveIt) {
            Class result;

            try {
                result = getClass().getClassLoader().loadClass( className );
            } catch (ClassNotFoundException e) {
                return null;
            }

            if( logger.isTraceEnabled() )
                logger.trace( "Returning class " + className + " loaded with current classloader" );

            return result;
        }

        @Override
        public InputStream loadResource(String name) {
            InputStream is = getClass().getClassLoader().getResourceAsStream( name );

            if( is != null ) {
                if( logger.isTraceEnabled() )
                    logger.trace( "Returning resource " + name + " loaded with current classloader" );

                return is;
            }

            return null;
        }

    }

    /**
     * Current class loader
     * 
     */
    class ThreadContextLoader extends ProxyClassLoader {
        private final Logger logger = Logger.getLogger( ThreadContextLoader.class );

        public ThreadContextLoader() {
            order = 4;
            enabled = Configuration.isThreadContextLoaderEnabled();
        }

        @Override
        public Class loadClass(String className, boolean resolveIt) {
            Class result;
            try {
                result = Thread.currentThread().getContextClassLoader().loadClass( className );
            } catch (ClassNotFoundException e) {
                return null;
            }

            if( logger.isTraceEnabled() )
                logger.trace( "Returning class " + className + " loaded with thread context classloader" );

            return result;
        }

        @Override
        public InputStream loadResource(String name) {
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream( name );

            if( is != null ) {
                if( logger.isTraceEnabled() )
                    logger.trace( "Returning resource " + name + " loaded with thread context classloader" );

                return is;
            }

            return null;
        }

    }

    /**
     * Osgi boot loader
     * 
     */
    class OsgiBootLoader extends ProxyClassLoader {
        private final Logger logger = Logger.getLogger( OsgiBootLoader.class );

        private boolean strictLoading;
        private String[] bootDelagation;

        public OsgiBootLoader() {
            enabled = Configuration.isOsgiBootDelegationEnabled();
            strictLoading = Configuration.isOsgiBootDelegationStrict();
            bootDelagation = Configuration.getOsgiBootDelegation();
            order = 0;
        }

        @Override
        public Class loadClass(String className, boolean resolveIt) {
            Class clazz = null;

            if( enabled && isPartOfOsgiBootDelegation( className ) ) {
                clazz = getParentLoader().loadClass( className, resolveIt );

                if( clazz == null && strictLoading ) {
                    throw new JclException( new ClassNotFoundException( "JCL OSGi Boot Delegation: Class " + className
                            + " not found." ) );
                }

                if( logger.isTraceEnabled() )
                    logger.trace( "Class " + className + " loaded via OSGi boot delegation." );
            }

            return clazz;
        }

        @Override
        public InputStream loadResource(String name) {
            InputStream is = null;

            if( enabled && isPartOfOsgiBootDelegation( name ) ) {
                is = getParentLoader().loadResource( name );

                if( is == null && strictLoading ) {
                    throw new ResourceNotFoundException( "JCL OSGi Boot Delegation: Resource " + name + " not found." );
                }

                if( logger.isTraceEnabled() )
                    logger.trace( "Resource " + name + " loaded via OSGi boot delegation." );
            }

            return is;
        }

        /**
         * Check if the class/resource is part of OSGi boot delegation
         * 
         * @param resourceName
         * @return
         */
        private boolean isPartOfOsgiBootDelegation(String resourceName) {
            if( resourceName.startsWith( JAVA_PACKAGE ) )
                return true;

            String[] bootPkgs = bootDelagation;

            if( bootPkgs != null ) {
                for( String bc : bootPkgs ) {
                    Pattern pat = Pattern.compile( Utils.wildcardToRegex( bc ), Pattern.CASE_INSENSITIVE );

                    Matcher matcher = pat.matcher( resourceName );
                    if( matcher.find() ) {
                        return true;
                    }
                }
            }

            return false;
        }

        public boolean isStrictLoading() {
            return strictLoading;
        }

        public void setStrictLoading(boolean strictLoading) {
            this.strictLoading = strictLoading;
        }

        public String[] getBootDelagation() {
            return bootDelagation;
        }

        public void setBootDelagation(String[] bootDelagation) {
            this.bootDelagation = bootDelagation;
        }
    }

    public ProxyClassLoader getSystemLoader() {
        return systemLoader;
    }

    public ProxyClassLoader getParentLoader() {
        return parentLoader;
    }

    public ProxyClassLoader getCurrentLoader() {
        return currentLoader;
    }

    public ProxyClassLoader getThreadLoader() {
        return currentLoader;
    }

    public ProxyClassLoader getOsgiBootLoader() {
        return osgiBootLoader;
    }
}
