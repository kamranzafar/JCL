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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.xeustechnologies.jcl.exception.JclException;
import org.xeustechnologies.jcl.proxy.ProxyProviderFactory;
import org.kamranzafar.commons.cloner.ObjectCloner;

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