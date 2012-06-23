<%@ page import="org.xeustechnologies.jcl.*" %>
<%@ page import="org.xeustechnologies.jcl.context.*" %>
<%
	JarClassLoader jcl=JclContext.get("jcl1");
	JclObjectFactory factory = JclObjectFactory.getInstance();

	Object obj = factory.create(jcl, "org.xeustechnologies.jcl.test.Test");
	String msg = (String) obj.getClass().getMethod("sayHello").invoke(obj, null);

	out.write("<h1>"+msg+"</h2><br>");
	
	out.write("<p><b>Object Details:</b></p>");
	out.write("<p>Class: "+obj.getClass().getName()+"<p>");
	out.write("<p>Classloader: "+obj.getClass().getClassLoader().getClass().getName()+"<p>");

	out.write("<br><p><b>Jcl Context:</b></p>");
	out.write("<p>JCL instances: "+JclContext.getAll()+"<p>");
	out.write("<p>Context classloader: "+jcl.getClass().getClassLoader()+"<p>");
%>