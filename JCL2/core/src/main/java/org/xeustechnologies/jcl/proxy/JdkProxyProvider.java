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

package org.xeustechnologies.jcl.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.xeustechnologies.jcl.JclUtils;

/**
 * Creates JDK proxies
 * 
 * @author Kamran Zafar
 * 
 */
public class JdkProxyProvider implements ProxyProvider {
    private class JdkProxyHandler implements InvocationHandler {
        private final Object delegate;

        public JdkProxyHandler(Object delegate) {
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
    }

    public Object createProxy(Object object, Class superClass, Class[] interfaces, ClassLoader cl) {
        JdkProxyHandler handler = new JdkProxyHandler( object );
        return Proxy.newProxyInstance( cl == null ? JclUtils.class.getClassLoader() : cl, interfaces, handler );
    }
}
