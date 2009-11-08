package org.xeustechnologies.jcl.spring;

import java.lang.reflect.InvocationTargetException;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.runner.RunWith;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@RunWith(JUnit4ClassRunner.class)
public class SpringTest extends TestCase {
    @Test
    public void testWithSpring() throws ClassNotFoundException, IllegalArgumentException, SecurityException,
            IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext( "classpath:spring-test.xml" );

        // Bean loaded with JCL
        Object test1 = appContext.getBean( "test1" );

        // Bean loaded with parent CL
        Object test2 = appContext.getBean( "test2" );

        assertEquals( "org.xeustechnologies.jcl.JarClassLoader", test1.getClass().getClassLoader().getClass().getName() );
        assertEquals( "sun.misc.Launcher$AppClassLoader", test2.getClass().getClassLoader().getClass().getName() );
    }
}
