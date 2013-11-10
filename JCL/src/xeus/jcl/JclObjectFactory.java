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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

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

    /**
     * private constructor
     */
    private JclObjectFactory() {
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
     * Creates the object of the specified class from the specified class loader
     * by invoking the default constructor
     * 
     * @param jcl
     * @param className
     * @return Object
     * @throws IllegalArgumentException
     * @throws SecurityException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    public Object create(JarClassLoader jcl, String className) throws IllegalArgumentException, SecurityException,
            IOException, ClassNotFoundException, InstantiationException, IllegalAccessException,
            InvocationTargetException, NoSuchMethodException {
        return create( jcl, className, null );
    }

    /**
     * Creates the object of the specified class from the specified class loader
     * by invoking the right arguments-constructor
     * 
     * @param jcl
     * @param className
     * @param args
     * @return Object
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws SecurityException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    public Object create(JarClassLoader jcl, String className, Object[] args) throws IOException,
            ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException,
            SecurityException, InvocationTargetException, NoSuchMethodException {
        if( args == null || args.length == 0 )
            return jcl.loadClass( className ).newInstance();

        Class[] types = new Class[args.length];

        for( int i = 0; i < args.length; i++ )
            types[i] = args[i].getClass();

        return jcl.loadClass( className ).getConstructor( types ).newInstance( args );
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
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws SecurityException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    public Object create(JarClassLoader jcl, String className, String methodName, Object[] args) throws IOException,
            ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException,
            SecurityException, InvocationTargetException, NoSuchMethodException {
        if( args == null || args.length == 0 )
            return jcl.loadClass( className ).getMethod( methodName ).invoke( null );

        Class[] types = new Class[args.length];

        for( int i = 0; i < args.length; i++ )
            types[i] = args[i].getClass();

        return jcl.loadClass( className ).getMethod( methodName, types ).invoke( null, args );
    }
}
