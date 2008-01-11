package xeus.jcl.exception;

public class JclException extends Exception{
    /**
     * Default serial id
     */
    private static final long serialVersionUID = 1L;

    public JclException(){
        super();
    }
    
    public JclException(String message){
        super(message);
    }
}
