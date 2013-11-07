package org.xeustechnologies.jcl.test;

import java.io.InputStream;
import java.net.URL;

public class TestLoader extends org.xeustechnologies.jcl.ProxyClassLoader {

    @Override
    public Class loadClass(String className, boolean resolveIt) {
        return null;
    }

    @Override
    public InputStream loadResource(String name) {
        return null;
    }

    @Override
    public URL findResource(String name) {
        return null;
    }

}
