package org.xeustechnologies.jcl;

public class JclJarEntry {
  
  private String baseUrl;
  private byte[] resourceBytes;
  
  public String getBaseUrl() {
    return baseUrl;
  }
  
  public void setBaseUrl(String argBaseUrl) {
    baseUrl = argBaseUrl;
  }
  
  public byte[] getResourceBytes() {
    return resourceBytes;
  }
  
  public void setResourceBytes(byte[] argResourceBytes) {
    resourceBytes = argResourceBytes;
  }

}
