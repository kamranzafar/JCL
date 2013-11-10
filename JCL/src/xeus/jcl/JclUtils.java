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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import xeus.jcl.exception.JclException;

@SuppressWarnings("unchecked")
public class JclUtils {

    /**
     * Casts the object ref to the passed interface class ref. It actually
     * returns a dynamic proxy for the passed object
     * 
     * @param object
     * @param classes
     * @return castable
     * @return casted
     */
    public static Object toCastable(Object object, Class[] classes) {
        JclProxyHandler handler = new JclProxyHandler( object );
        return Proxy.newProxyInstance( JclUtils.class.getClassLoader(), classes, handler );
    }

    /**
     * Casts the object ref to the passed interface class ref
     * 
     * @param object
     * @param clazz
     * @return castable
     * @return casted
     */
    public static Object toCastable(Object object, Class clazz) {
        return toCastable( object, new Class[] { clazz } );
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
        return (T) toCastable( object, clazz );
    }

    /**
     * Deep clones the Serializable objects in the current classloader
     * 
     * @param original
     * @return clone
     */
    public static Object clone(Object original) {
        Object clone = null;
        try {
            // Increased buffer size to speed up writing
            ByteArrayOutputStream bos = new ByteArrayOutputStream( 5120 );
            ObjectOutputStream out = new ObjectOutputStream( bos );
            out.writeObject( original );
            out.flush();
            out.close();

            ObjectInputStream in = new ObjectInputStream( new ByteArrayInputStream( bos.toByteArray() ) );
            clone = in.readObject();

            in.close();
            bos.close();

            return clone;
        } catch (IOException e) {
            throw new JclException( e );
        } catch (ClassNotFoundException cnfe) {
            throw new JclException( cnfe );
        }
    }

    /**
     * proxy method invocation handler
     * 
     */
    private static class JclProxyHandler implements InvocationHandler {
        private final Object delegate;

        public JclProxyHandler(Object delegate) {
            this.delegate = delegate;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
         *      java.lang.reflect.Method, java.lang.Object[])
         */
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Method delegateMethod = delegate.getClass().getMethod( method.getName(), method.getParameterTypes() );
            return delegateMethod.invoke( delegate, args );
        }
    }

}