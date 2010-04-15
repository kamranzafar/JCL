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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.xeustechnologies.jcl.exception.JclException;
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

    /**
     * Casts the object ref to the passed interface class ref. It actually
     * returns a dynamic jdk proxy for the passed object in the given
     * classloader
     * 
     * @param object
     * @param classes
     * @param cl
     * @return castable
     */
    public static Object toCastable(Object object, Class[] classes, ClassLoader cl) {
        JclProxyHandler handler = new JclProxyHandler( object );
        return Proxy.newProxyInstance( cl == null ? JclUtils.class.getClassLoader() : cl, classes, handler );
    }

    /**
     * Creates a cglib proxy
     * 
     * @param object
     * @param cl
     * @return
     */
    public static Object toCastable(Object object, ClassLoader cl) {
        JclProxyHandler handler = new JclProxyHandler( object );

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass( object.getClass() );
        enhancer.setCallback( handler );
        enhancer.setClassLoader( cl == null ? JclUtils.class.getClassLoader() : cl );

        return enhancer.create();
    }

    /**
     * Creates a cglib proxy
     * 
     * @param object
     * @return
     */
    public static Object toCastable(Object object) {
        return toCastable( object, (ClassLoader) null );
    }

    /**
     * Casts the object ref to the passed interface class ref. It actually
     * returns a dynamic proxy for the passed object
     * 
     * @param object
     * @param classes
     * @return castable
     */
    public static Object toCastable(Object object, Class[] classes) {
        return toCastable( object, classes, null );
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
        return toCastable( object, clazz, null );
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
        return toCastable( object, new Class[] { clazz }, cl );
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

    /**
     * proxy method invocation handler
     * 
     */
    private static class JclProxyHandler implements InvocationHandler, MethodInterceptor {
        private final Object delegate;

        public JclProxyHandler(Object delegate) {
            this.delegate = delegate;
        }

        /**
         * 
         * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
         *      java.lang.reflect.Method, java.lang.Object[])
         */
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Method delegateMethod = delegate.getClass().getMethod( method.getName(), method.getParameterTypes() );
            return delegateMethod.invoke( delegate, args );
        }

        /**
         * 
         * @see net.sf.cglib.proxy.MethodInterceptor#intercept(java.lang.Object,
         *      java.lang.reflect.Method, java.lang.Object[],
         *      net.sf.cglib.proxy.MethodProxy)
         */
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            return invoke( obj, method, args );
        }
    }
}