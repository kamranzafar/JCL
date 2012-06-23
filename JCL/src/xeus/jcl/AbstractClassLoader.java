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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import xeus.jcl.exception.ResourceNotFoundException;
import xeus.jcl.loader.Loader;

/**
 * Abstract class loader that can load classes from different resources
 * 
 * @author Kamran Zafar
 * 
 */
@SuppressWarnings("unchecked")
public abstract class AbstractClassLoader extends ClassLoader {

    protected final List<Loader> loaders = new ArrayList<Loader>();

    private final Loader systemLoader = new SystemLoader();
    private final Loader parentLoader = new ParentLoader();
    private final Loader currentLoader = new CurrentLoader();

    /**
     * No arguments constructor
     */
    public AbstractClassLoader() {
        loaders.add( systemLoader );
        loaders.add( parentLoader );
        loaders.add( currentLoader );
    }

    public void addLoader(Loader loader) {
        loaders.add( loader );
    }

    /**
     * Override loadClass
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
        Collections.sort( loaders );
        Class clazz = null;
        for( Loader l : loaders ) {
            if( l.isEnabled() ) {
                clazz = l.load( className, resolveIt );
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
        Collections.sort( loaders );
        InputStream is = null;
        for( Loader l : loaders ) {
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
    class SystemLoader extends Loader {

        private final Logger logger = Logger.getLogger( SystemLoader.class );

        public SystemLoader() {
            order = 4;
            enabled = Configuration.isSystemLoaderEnabled();
        }

        @Override
        public Class load(String className, boolean resolveIt) {
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
    class ParentLoader extends Loader {
        private final Logger logger = Logger.getLogger( ParentLoader.class );

        public ParentLoader() {
            order = 3;
            enabled = Configuration.isParentLoaderEnabled();
        }

        @Override
        public Class load(String className, boolean resolveIt) {
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
    class CurrentLoader extends Loader {
        private final Logger logger = Logger.getLogger( CurrentLoader.class );

        public CurrentLoader() {
            order = 2;
            enabled = Configuration.isCurrentLoaderEnabled();
        }

        @Override
        public Class load(String className, boolean resolveIt) {
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

    public Loader getSystemLoader() {
        return systemLoader;
    }

    public Loader getParentLoader() {
        return parentLoader;
    }

    public Loader getCurrentLoader() {
        return currentLoader;
    }
}
