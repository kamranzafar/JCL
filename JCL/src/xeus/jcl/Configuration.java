package xeus.jcl;

import java.util.ResourceBundle;

public class Configuration {
    private static ResourceBundle bundle;

    static{
        bundle = ResourceBundle.getBundle("jcl.properties");
    }

    public static boolean supressCollisionException() {
        return bundle.getString("jcl.suppressCollisionException")
                .equals("true") ? true : false;
    }
}
