package chatserver;

public class User {
    private String id;
    private String password;

    public User () {
        System.out.println("Wrong Input!!");
    }
    public User (String id, String pw) {
        this.id = id;
        
        String salt = BCrypt.gensalt(12);
        String hashedPassword = BCrypt.hashpw(pw, salt);
        this.password = hashedPassword;
    }
    
    public boolean checkPassword(String pw) {
        boolean passwordVerified = false;
        
        if (password == null || !password.startsWith("$2a$")) {
            throw new java.lang.IllegalArgumentException("Invalid hash provided for comparison");
        }
        
        passwordVerified = BCrypt.checkpw(pw, password);
        
        return(passwordVerified);
    }

    public String getId() {
        return id;
    }
}
