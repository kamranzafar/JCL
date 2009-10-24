package org.xeustechnologies.jcl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.runner.RunWith;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.xeustechnologies.jcl.exception.JclException;

@SuppressWarnings("all")
@RunWith(JUnit4ClassRunner.class)
public class LoadTest extends TestCase {

    private static Logger logger = Logger.getLogger( LoadTest.class );

    @Test
    public void testWithResourceName() throws IOException, InstantiationException, IllegalAccessException,
            ClassNotFoundException, IllegalArgumentException, SecurityException, InvocationTargetException,
            NoSuchMethodException, JclException {
        JarClassLoader jc = new JarClassLoader( new String[] { "test-jcl.jar", "./target/test-classes" } );

        // New class
        Object testObj = jc.loadClass( "org.xeustechnologies.jcl.test.Test" ).newInstance();
        assertNotNull( testObj );

        // Locally loaded
        testObj = jc.loadClass( "org.xeustechnologies.jcl.test.Test" ).newInstance();
        assertNotNull( testObj );
    }

    @Test
    public void testWithClassFolder() throws IOException, InstantiationException, IllegalAccessException,
            ClassNotFoundException, IllegalArgumentException, SecurityException, InvocationTargetException,
            NoSuchMethodException, JclException {
        JarClassLoader jc = new JarClassLoader( new String[] { "./target/test-classes" } );

        Object testObj = jc.loadClass( "org.xeustechnologies.jcl.test.Test" ).newInstance();
        assertNotNull( testObj );
    }

    @Test
    public void testWithUrl() throws IOException, InstantiationException, IllegalAccessException,
            ClassNotFoundException, IllegalArgumentException, SecurityException, InvocationTargetException,
            NoSuchMethodException, JclException, URISyntaxException {
        // URL url = new URL("http://localhost:8080/blank/test-jcl.jar");
        File f = new File( "test-jcl.jar" );

        JarClassLoader jc = new JarClassLoader( new URL[] { f.toURI().toURL() } );
        Object testObj = jc.loadClass( "org.xeustechnologies.jcl.test.Test" ).newInstance();
        assertNotNull( testObj );
    }

    @Test
    public void testWithInputStream() throws IOException, InstantiationException, IllegalAccessException,
            ClassNotFoundException, IllegalArgumentException, SecurityException, InvocationTargetException,
            NoSuchMethodException, JclException {
        FileInputStream fis = new FileInputStream( "./target/test-jcl.jar" );
        JarClassLoader jc = new JarClassLoader( new FileInputStream[] { fis } );
        Object testObj = jc.loadClass( "org.xeustechnologies.jcl.test.Test" ).newInstance();
        assertNotNull( testObj );
        fis.close();
    }

    @Test
    public void testAddingClassSources() throws IOException, InstantiationException, IllegalAccessException,
            ClassNotFoundException, IllegalArgumentException, SecurityException, InvocationTargetException,
            NoSuchMethodException, JclException {
        JarClassLoader jc = new JarClassLoader();
        jc.add( "./target/test-classes" );
        Object testObj = jc.loadClass( "org.xeustechnologies.jcl.test.Test" ).newInstance();
        assertNotNull( testObj );
    }

    @Test
    public void testChangeClassLoadingOrder() throws IOException, InstantiationException, IllegalAccessException,
            ClassNotFoundException, IllegalArgumentException, SecurityException, InvocationTargetException,
            NoSuchMethodException, JclException {
        JarClassLoader jc = new JarClassLoader();
        jc.getSystemLoader().setOrder( 1 );
        jc.getParentLoader().setOrder( 3 );
        jc.getLocalLoader().setOrder( 2 );

        jc.add( "./target/test-classes" );

        // Should be loaded from system
        Object testObj = jc.loadClass( "org.xeustechnologies.jcl.test.Test" ).newInstance();
        assertNotNull( testObj );
    }

    @Test
    public void testInterfaceCast() throws IOException, InstantiationException, IllegalAccessException,
            ClassNotFoundException, IllegalArgumentException, SecurityException, InvocationTargetException,
            NoSuchMethodException, JclException {
        JarClassLoader jc = new JarClassLoader();
        jc.add( "./target/test-jcl.jar" );

        JclObjectFactory factory = JclObjectFactory.getInstance();
        Object serializable = factory.create( jc, "org.xeustechnologies.jcl.test.Test" );

        Serializable s = JclUtils.cast( serializable, Serializable.class );

        assertNotNull( s );

        s = (Serializable) JclUtils.toCastable( serializable, Serializable.class );

        assertNotNull( s );

        s = (Serializable) JclUtils.clone( serializable );

        assertNotNull( s );

        s = (Serializable) JclUtils.deepClone( serializable );

        assertNotNull( s );
    }

    @Test
    public void testUnloading() throws IOException, InstantiationException, IllegalAccessException,
            ClassNotFoundException, IllegalArgumentException, SecurityException, InvocationTargetException,
            NoSuchMethodException, JclException {
        JarClassLoader jc = new JarClassLoader( new String[] { "./target/test-classes" } );

        Object testObj = null;
        jc.loadClass( "org.xeustechnologies.jcl.test.Test" );
        jc.unloadClass( "org.xeustechnologies.jcl.test.Test" );

        try {
            testObj = jc.loadClass( "org.xeustechnologies.jcl.test.Test" ).newInstance();

            // Must have been loaded by a CL other than JCL-Local
            Assert
                    .assertFalse( testObj.getClass().getClassLoader()
                            .equals( "org.xeustechnologies.jcl.JarClassLoader" ) );
            return;
        } catch (ClassNotFoundException cnfe) {
            // expected if not found
        }

        assertNull( testObj );
    }

    @Test
    public void testEnabledFlag() throws IOException, InstantiationException, IllegalAccessException,
            ClassNotFoundException, IllegalArgumentException, SecurityException, InvocationTargetException,
            NoSuchMethodException, JclException {
        JarClassLoader jc = new JarClassLoader( new String[] { "./target/test-jcl.jar" } );
        jc.getLocalLoader().setEnabled( false );
        jc.getCurrentLoader().setEnabled( false );
        jc.getParentLoader().setEnabled( false );
        jc.getSystemLoader().setEnabled( false );
        jc.getThreadLoader().setEnabled( false );

        String cls = "org.xeustechnologies.jcl.test.Test";
        try {
            jc.loadClass( cls );
        } catch (ClassNotFoundException e) {
            // expected
            return;
        }

        throw new AssertionError( "Expected: ClassNotFoundException " + cls );
    }

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

    @Test
    public void testOsgiBootLoading() throws IOException, ClassNotFoundException {
        JarClassLoader jc = new JarClassLoader( new String[] { "./target/test-jcl.jar" } );

        AbstractClassLoader.OsgiBootLoader obl = (AbstractClassLoader.OsgiBootLoader) jc.getOsgiBootLoader();
        obl.setEnabled( true );
        obl.setStrictLoading( true );

        // Load with parent among all java core classes
        obl.setBootDelagation( new String[] { "org.xeustechnologies.jcl.test.*" } );

        assertEquals( "sun.misc.Launcher$AppClassLoader", jc.loadClass( "org.xeustechnologies.jcl.test.Test" )
                .getClassLoader().getClass().getName() );
    }
}
