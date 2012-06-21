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

package org.xeustechnologies.jcl.utils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.xeustechnologies.jcl.exception.JclException;

/**
 * @author Kamran Zafar
 * 
 */
public class ObjectCloner {
    private final Objenesis objenesis;
    private final Set<Class<?>> ignoredClasses = new HashSet<Class<?>>();
    private final Map<Object, Boolean> ignoredInstances = new IdentityHashMap<Object, Boolean>();
    private final ConcurrentHashMap<Class<?>, List<Field>> fieldsMap = new ConcurrentHashMap<Class<?>, List<Field>>();

    public ObjectCloner() {
        objenesis = new ObjenesisStd();
        init();
    }

    private void init() {
        ignoreKnownJdkImmutableClasses();
    }

    /**
     * Ignore the constant
     * 
     * @param c
     * @param privateFieldName
     */
    @SuppressWarnings("unchecked")
    public void ignoreConstant(final Class<?> c, final String privateFieldName) {
        try {
            final Field field = c.getDeclaredField( privateFieldName );

            AccessController.doPrivileged( new PrivilegedAction() {
                public Object run() {
                    field.setAccessible( true );
                    return null;
                }
            } );

            Object v = field.get( null );
            ignoredInstances.put( v, true );
        } catch (Throwable e) {
            throw new JclException( e );
        }
    }

    /**
     * Ignore some Jdk immutable classes
     */
    private void ignoreKnownJdkImmutableClasses() {
        ignoreClass( Integer.class, Long.class, Boolean.class, Class.class, Float.class, Double.class, Character.class,
                Byte.class, Short.class, Void.class, BigDecimal.class, BigInteger.class, URI.class, URL.class,
                UUID.class, Pattern.class );
    }

    /**
     * Add to the ignore-list
     * 
     * @param clazz
     */
    public void ignoreClass(final Class<?>... clazz) {
        for( Class<?> c : clazz )
            ignoredClasses.add( c );
    }

    /**
     * Creates a new instance of the Class
     * 
     * @param c
     * @return T
     */
    @SuppressWarnings("unchecked")
    protected <T> T newInstance(final Class<T> c) {
        return (T) objenesis.newInstance( c );
    }

    /**
     * @param original
     * @return T
     */
    public <T> T deepClone(final T original) {
        if( original == null )
            return null;

        final Map<Object, Object> clones = new IdentityHashMap<Object, Object>();
        try {
            return clone( original, clones );
        } catch (IllegalAccessException e) {
            throw new JclException( "Error during cloning of " + original, e );
        }
    }

    /**
     * @param original
     * @return T
     */
    public <T> T shallowClone(final T original) {
        if( original == null )
            return null;

        try {
            return clone( original, null );
        } catch (IllegalAccessException e) {
            throw new JclException( "Error during cloning of " + original, e );
        }
    }

    /**
     * @param T
     *            original
     * @param Map
     *            clones
     * @return
     * @throws IllegalAccessException
     */
    @SuppressWarnings("unchecked")
    private <T> T clone(final T original, final Map<Object, Object> clones) throws IllegalAccessException {
        final Class<T> clz = (Class<T>) original.getClass();

        if( ignoredInstances.containsKey( original ) || clz.isEnum() || ignoredClasses.contains( clz ) )
            return original;

        if( clones != null && clones.get( original ) != null ) {
            return (T) clones.get( original );
        }

        if( clz.isArray() ) {
            int length = Array.getLength( original );
            T newInstance = (T) Array.newInstance( clz.getComponentType(), length );

            clones.put( original, newInstance );

            for( int i = 0; i < length; i++ ) {
                Object v = Array.get( original, i );
                Object clone = clones != null ? clone( v, clones ) : v;
                Array.set( newInstance, i, clone );
            }

            return newInstance;
        }

        final T newInstance = newInstance( clz );

        if( clones != null ) {
            clones.put( original, newInstance );
        }

        final List<Field> fields = allFields( clz );

        for( Field field : fields ) {
            if( !Modifier.isStatic( field.getModifiers() ) ) {
                field.setAccessible( true );
                Object fieldObject = field.get( original );
                Object fieldObjectClone = clones != null ? clone( fieldObject, clones ) : fieldObject;
                field.set( newInstance, fieldObjectClone );
            }
        }

        return newInstance;
    }

    /**
     * @param List
     *            l
     * @param Field
     *            [] fields
     */
    private void addAll(final List<Field> l, final Field[] fields) {
        for( final Field field : fields ) {
            l.add( field );
        }
    }

    /**
     * @param Class
     *            c
     * @return List
     */
    private List<Field> allFields(final Class<?> c) {
        List<Field> l = fieldsMap.get( c );

        if( l == null ) {
            l = new LinkedList<Field>();
            Field[] fields = c.getDeclaredFields();

            addAll( l, fields );

            Class<?> sc = c;

            while (( sc = sc.getSuperclass() ) != Object.class && sc != null) {
                addAll( l, sc.getDeclaredFields() );
            }

            fieldsMap.putIfAbsent( c, l );
        }
        return l;
    }
}
