package sk.upjs.ics.users;

import org.springframework.security.crypto.bcrypt.BCrypt;

public class PasswordHasher {

    public String hashPassword(String password) {
        var salt = BCrypt.gensalt();
        return BCrypt.hashpw(password, salt);
    }
}
