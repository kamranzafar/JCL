package org.xeustechnologies.jcl;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test handling resources inside and outside jars
 */
public class ClasspathResourcesTest {

  @Test
  public void testLoadResource() throws Exception {
    final String name = "test";
    ClasspathResources jarResources = getClasspathResources(name);

    URL resourceURL = jarResources.getResourceURL("test.properties");
    Properties props = getProperties(resourceURL);
    assertEquals("testval", props.getProperty("testkey"));
  }

  @Test
  public void testLoadResourcesFromJar() throws Exception {
    final String name = "test.jar";
    ClasspathResources jarResources = getClasspathResources(name);

    URL resourceURL = jarResources.getResourceURL("test.properties");
    Properties props = getProperties(resourceURL);
    assertEquals("testval in jar", props.getProperty("testkey"));

    resourceURL = jarResources.getResourceURL("test/subdir.properties");
    props = getProperties(resourceURL);
    assertEquals("testval in jar in subdirectory", props.getProperty("testkey"));
  }

  private ClasspathResources getClasspathResources(String name) {
    final URL testJar = ClassLoader.getSystemClassLoader().getResource(name);
    assertNotNull("Could not find file or directory named '" + name + "'. It should be in the test resources directory", testJar);
    ClasspathResources jarResources = new ClasspathResources();
    jarResources.loadResource(testJar);
    return jarResources;
  }

  private Properties getProperties(URL resourceURL) throws IOException {
    assertNotNull(resourceURL);
    Properties props = new Properties();
    props.load(resourceURL.openStream());
    return props;
  }
}
