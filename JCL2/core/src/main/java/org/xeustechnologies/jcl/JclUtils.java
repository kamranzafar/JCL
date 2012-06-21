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
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.xeustechnologies.jcl.exception.JclException;
import org.xeustechnologies.jcl.proxy.ProxyProviderFactory;
import org.xeustechnologies.jcl.utils.ObjectCloner;

/**
 * This class has some important utility methods commonly required when using
 * JCL
 * 
 * @author Kamran
 * 
 */
@SuppressWarnings("unchecked")
public class JclUtils {

    public static Object createProxy(Object object, Class superClass, Class[] interfaces, ClassLoader cl) {
        return ProxyProviderFactory.create().createProxy( object, superClass, interfaces, cl );
    }

    /**
     * Casts the object ref to the passed interface class ref. It actually
     * returns a dynamic proxy for the passed object
     * 
     * @param object
     * @param clazz
     * @return castable
     */
    public static Object toCastable(Object object, Class clazz) {
        return createProxy( object, clazz, new Class[] { clazz }, null );
    }

    /**
     * Casts the object ref to the passed interface class ref. It actually
     * returns a dynamic proxy for the passed object
     * 
     * @param object
     * @param clazz
     *            []
     * @return castable
     */
    public static Object toCastable(Object object, Class[] clazz) {
        return createProxy( object, clazz[0], clazz, null );
    }

    /**
     * Casts the object ref to the passed interface class ref
     * 
     * @param object
     * @param clazz
     * @param cl
     * @return castable
     */
    public static Object toCastable(Object object, Class clazz, ClassLoader cl) {
        return createProxy( object, clazz, new Class[] { clazz }, cl );
    }

    /**
     * Casts the object ref to the passed interface class ref
     * 
     * @param object
     * @param clazz
     *            []
     * @param cl
     * @return castable
     */
    public static Object toCastable(Object object, Class[] clazz, ClassLoader cl) {
        return createProxy( object, clazz[0], clazz, cl );
    }

    /**
     * Casts the object ref to the passed interface class ref and returns it
     * 
     * @param object
     * @param clazz
     * @return T reference
     * @return casted
     */
    public static <T> T cast(Object object, Class<T> clazz) {
        return (T) toCastable( object, clazz, null );
    }

    /**
     * Casts the object ref to the passed interface class ref and returns it
     * 
     * @param object
     * @param clazz
     * @param cl
     * @return T reference
     * @return casted
     */
    public static <T> T cast(Object object, Class<T> clazz, ClassLoader cl) {
        return (T) toCastable( object, clazz, cl );
    }

    /**
     * Deep clones the Serializable objects in the current classloader. This
     * method is slow and uses Object streams to clone Serializable objects.
     * 
     * This method is now deprecated because of its inefficiency and the
     * limitation to clone Serializable objects only. The use of deepClone or
     * shallowClone is now recommended
     * 
     * @param original
     * @return clone
     * @deprecated As of release 2.0, replaced by
     *             {@link #deepClone(Object original)}
     */
    @Deprecated
    public static Object clone(Object original) {
        Object clone = null;

        try {
            // Increased buffer size
            ByteArrayOutputStream bos = new ByteArrayOutputStream( 5120 );
            ObjectOutputStream out = new ObjectOutputStream( bos );
            out.writeObject( original );
            out.flush();
            out.close();

            ObjectInputStream in = new ObjectInputStream( new ByteArrayInputStream( bos.toByteArray() ) );
            clone = in.readObject();

            in.close();
            bos.close();
        } catch (Exception e) {
            throw new JclException( e );
        }

        return clone;
    }

    /**
     * Deep clones any object
     * 
     * @param original
     * @return clone
     */
    public static Object deepClone(Object original) {
        ObjectCloner cloner = new ObjectCloner();

        return cloner.deepClone( original );
    }

    /**
     * Shallow clones any object
     * 
     * @param original
     * @return clone
     */
    public static Object shallowClone(Object original) {
        ObjectCloner cloner = new ObjectCloner();

        return cloner.shallowClone( original );
    }
}