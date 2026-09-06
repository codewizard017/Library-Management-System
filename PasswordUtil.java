import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    private static final int WORK_FACTOR = 12;

    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(WORK_FACTOR));
    }

    public static boolean checkPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}