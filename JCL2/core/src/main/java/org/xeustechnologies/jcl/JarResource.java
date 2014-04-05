package org.xeustechnologies.jcl;

import java.security.ProtectionDomain;

/**
 * Represents class/resource loaded by JarClassLoader
 */
public class JarResource {

    private byte[] content;

    //can be null
    private ProtectionDomain protectionDomain;

    public JarResource(byte[] content, ProtectionDomain protectionDomain) {
        this.content = content;
        this.protectionDomain = protectionDomain;
    }

    public JarResource(byte[] content) {
        this.content = content;
    }

    public byte[] getContent() {
        return content;
    }

    public ProtectionDomain getProtectionDomain() {
        return protectionDomain;
    }
}
