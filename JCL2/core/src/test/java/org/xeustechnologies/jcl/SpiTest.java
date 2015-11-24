package org.xeustechnologies.jcl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Created by kamran on 23/11/15.
 */
@RunWith(JUnit4.class)
public class SpiTest {
    @Test
    public void spiTest() throws Exception {
        JarClassLoader jcl = new JarClassLoader();
        jcl.add("./target/test-classes/lucene-core-5.3.1.jar");

        Class codecClass = jcl.loadClass("org.apache.lucene.codecs.Codec");

        ServiceLoader serviceLoader = ServiceLoader.load(codecClass, jcl);

        Iterator itr = serviceLoader.iterator();

        Assert.assertTrue(itr.hasNext());

//        while (itr.hasNext()) {
//            System.out.println(itr.next());
//        }
    }
}
