package xeus.jcl;

import java.util.ResourceBundle;

public class Configuration {
    private static ResourceBundle bundle = ResourceBundle.getBundle("jcl");

    public static boolean supressCollisionException() {
        return bundle.getString("jcl.suppressCollisionException")
                .equals("true") ? true : false;
    }
}
