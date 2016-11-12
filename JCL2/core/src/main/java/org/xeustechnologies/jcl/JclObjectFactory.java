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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final transient Logger logger = LoggerFactory.getLogger(JclObjectFactory.class);

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

            logger.debug( "Class: {}", superClass );
            logger.debug( "Class Interfaces: {}", il );

            if (superClass == null && il.size() == 0) {
                throw new JclException( "Neither the class [" + object.getClass().getSuperclass().getName()
                        + "] nor all the implemented interfaces found in the current classloader" );
            }

            return JclUtils.createProxy( object, superClass, il.toArray( new Class[il.size()] ), null );
        }

        return object;
    }
}
