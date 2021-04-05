package org.xeustechnologies.jcl.spring;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.xeustechnologies.jcl.context.JclContext;

import java.lang.reflect.InvocationTargetException;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(JUnit4.class)
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
        assertThat(
                test2.getClass().getClassLoader().getClass().getName(),
                anyOf(
                        equalTo("sun.misc.Launcher$AppClassLoader"),
                        equalTo("jdk.internal.loader.ClassLoaders$AppClassLoader")
                )
        );
    }
}
