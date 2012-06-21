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

package org.xeustechnologies.jcl.proxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.xeustechnologies.jcl.JclUtils;

/**
 * Creates cglib proxies
 * 
 * @author Kamran Zafar
 * 
 */
public class CglibProxyProvider implements ProxyProvider {

    private class CglibProxyHandler implements MethodInterceptor {
        private final Object delegate;

        public CglibProxyHandler(Object delegate) {
            this.delegate = delegate;
        }

        /**
         * 
         * @see net.sf.cglib.proxy.MethodInterceptor#intercept(java.lang.Object,
         *      java.lang.reflect.Method, java.lang.Object[],
         *      net.sf.cglib.proxy.MethodProxy)
         */
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            Method delegateMethod = delegate.getClass().getMethod( method.getName(), method.getParameterTypes() );
            return delegateMethod.invoke( delegate, args );
        }
    }

    public Object createProxy(Object object, Class superClass, Class[] interfaces, ClassLoader cl) {
        CglibProxyHandler handler = new CglibProxyHandler( object );

        Enhancer enhancer = new Enhancer();

        if( superClass != null ) {
            enhancer.setSuperclass( superClass );
        }

        enhancer.setCallback( handler );

        if( interfaces != null ) {
            List<Class> il = new ArrayList<Class>();

            for( Class i : interfaces ) {
                if( i.isInterface() ) {
                    il.add( i );
                }
            }

            enhancer.setInterfaces( il.toArray( new Class[il.size()] ) );
        }

        enhancer.setClassLoader( cl == null ? JclUtils.class.getClassLoader() : cl );

        return enhancer.create();
    }
}
