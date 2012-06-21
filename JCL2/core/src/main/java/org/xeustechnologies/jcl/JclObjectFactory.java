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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xeustechnologies.jcl.exception.JclException;

/**
 * A factory class that loads classes from specified JarClassLoader and tries to
 * instantiate their objects
 * 
 * @author Kamran Zafar
 * 
 */
@SuppressWarnings("unchecked")
public class JclObjectFactory {
    private static JclObjectFactory jclObjectFactory = new JclObjectFactory();
    private static boolean autoProxy;
    private final Logger logger = Logger.getLogger( JclObjectFactory.class.getName() );

    /**
     * private constructor
     */
    private JclObjectFactory() {
        autoProxy = Configuration.autoProxy();
    }

    /**
     * Returns the instance of the singleton factory
     * 
     * @return JclObjectFactory
     */
    public static JclObjectFactory getInstance() {
        return jclObjectFactory;
    }

    /**
     * Returns the instance of the singleton factory that can be used to create
     * auto proxies for jcl-created objects
     * 
     * @return JclObjectFactory
     */
    public static JclObjectFactory getInstance(boolean autoProxy) {
        JclObjectFactory.autoProxy = autoProxy;
        return jclObjectFactory;
    }

    /**
     * Creates the object of the specified class from the specified class loader
     * by invoking the default constructor
     * 
     * @param jcl
     * @param className
     * @return Object
     */
    public Object create(JarClassLoader jcl, String className) {
        return create( jcl, className, (Object[]) null );
    }

    /**
     * Creates the object of the specified class from the specified class loader
     * by invoking the right arguments-constructor
     * 
     * @param jcl
     * @param className
     * @param args
     * @return Object
     */
    public Object create(JarClassLoader jcl, String className, Object... args) {
        if (args == null || args.length == 0) {
            try {
                return newInstance( jcl.loadClass( className ).newInstance() );
            } catch (Throwable e) {
                throw new JclException( e );
            }
        }

        Class[] types = new Class[args.length];

        for (int i = 0; i < args.length; i++)
            types[i] = args[i].getClass();

        return create( jcl, className, args, types );
    }

    /**
     * Creates the object of the specified class from the specified class loader
     * by invoking the right arguments-constructor based on the passed types
     * parameter
     * 
     * @param jcl
     * @param className
     * @param args
     * @param types
     * @return Object
     */
    public Object create(JarClassLoader jcl, String className, Object[] args, Class[] types) {
        Object obj = null;

        if (args == null || args.length == 0) {
            try {
                obj = jcl.loadClass( className ).newInstance();
            } catch (Throwable e) {
                throw new JclException( e );
            }
        } else {
            try {
                obj = jcl.loadClass( className ).getConstructor( types ).newInstance( args );
            } catch (Exception e) {
                throw new JclException( e );
            }
        }

        return newInstance( obj );
    }

    /**
     * Creates the object of the specified class from the specified class loader
     * by invoking the right static factory method
     * 
     * @param jcl
     * @param className
     * @param methodName
     * @param args
     * @return Object
     */
    public Object create(JarClassLoader jcl, String className, String methodName, Object... args) {
        if (args == null || args.length == 0) {
            try {
                return newInstance( jcl.loadClass( className ).getMethod( methodName ).invoke( null ) );
            } catch (Exception e) {
                throw new JclException( e );
            }
        }
        Class[] types = new Class[args.length];

        for (int i = 0; i < args.length; i++)
            types[i] = args[i].getClass();

        return create( jcl, className, methodName, args, types );
    }

    /**
     * Creates the object of the specified class from the specified class loader
     * by invoking the right static factory method based on the types parameter
     * 
     * @param jcl
     * @param className
     * @param methodName
     * @param args
     * @param types
     * @return Object
     */
    public Object create(JarClassLoader jcl, String className, String methodName, Object[] args, Class[] types) {
        Object obj = null;
        if (args == null || args.length == 0) {
            try {
                obj = jcl.loadClass( className ).getMethod( methodName ).invoke( null );
            } catch (Exception e) {
                throw new JclException( e );
            }
        } else {
            try {
                obj = jcl.loadClass( className ).getMethod( methodName, types ).invoke( null, args );
            } catch (Exception e) {
                throw new JclException( e );
            }
        }

        return newInstance( obj );
    }

    /**
     * Creates a proxy
     * 
     * @param object
     * @return
     */
    private Object newInstance(Object object) {
        if (autoProxy) {

            Class superClass = null;

            // Check class
            try {
                Class.forName( object.getClass().getSuperclass().getName() );
                superClass = object.getClass().getSuperclass();
            } catch (ClassNotFoundException e) {
            }

            Class[] interfaces = object.getClass().getInterfaces();

            List<Class> il = new ArrayList<Class>();

            // Check available interfaces
            for (Class i : interfaces) {
                try {
                    Class.forName( i.getClass().getName() );
                    il.add( i );
                } catch (ClassNotFoundException e) {
                }
            }

            if (logger.isLoggable( Level.FINER )) {
                logger.finer( "Class: " + superClass );
                logger.finer( "Class Interfaces: " + il );
            }

            if (superClass == null && il.size() == 0) {
                throw new JclException( "Neither the class [" + object.getClass().getSuperclass().getName()
                        + "] nor all the implemented interfaces found in the current classloader" );
            }

            return JclUtils.createProxy( object, superClass, il.toArray( new Class[il.size()] ), null );
        }

        return object;
    }
}
