package manager.data;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.regex.Pattern;

/**
 * @author unknown
 */
public class PBKDF2Hash
{
    private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA1";

    // The following constants may be changed without breaking existing hashes.
    private static final int SALT_BYTE_SIZE = 24;
    private static final int HASH_BYTE_SIZE = 24;
    private static final int PBKDF2_ITERATIONS = 1000;

    private static final int ITERATION_INDEX = 1;
    private static final int SALT_INDEX = 2;
    private static final int PBKDF2_INDEX = 3;

    private static final Pattern COLON_PATTERN = Pattern.compile(":");

    /**
     * Returns a salted PBKDF2 hash of the password.
     *
     * @param   password    the password to hash
     * @return a salted PBKDF2 hash of the password
     */
    public static String createHash(final String password)
    {
        try {
            return createHash(password.toCharArray());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns a salted PBKDF2 hash of the password.
     *
     * @param   password    the password to hash
     * @return a salted PBKDF2 hash of the password
     */
    private static String createHash(final char[] password)
            throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        // Generate a random salt
        final SecureRandom random = new SecureRandom();
        final byte[] salt = new byte[SALT_BYTE_SIZE];
        random.nextBytes(salt);

        // Hash the password
        final byte[] hash = pbkdf2(password, salt, PBKDF2_ITERATIONS, HASH_BYTE_SIZE);
        // format iterations:salt:hash
        return "sha1:" + PBKDF2_ITERATIONS + ':' + toBase64(salt) + ':' + toBase64(hash);
    }

    /**
     * Validates a password using a hash.
     *
     * @param   password        the password to check
     * @param   correctHash     the hash of the valid password
     * @return true if the password is correct, false if not
     */
    public static boolean validatePassword(final String password, final String correctHash)
    {
        try {
            return validatePassword(password.toCharArray(), correctHash);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Validates a password using a hash.
     *
     * @param   password        the password to check
     * @param   correctHash     the hash of the valid password
     * @return true if the password is correct, false if not
     */
    private static boolean validatePassword(final char[] password, final CharSequence correctHash)
            throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        // Decode the hash into its parameters
        final String[] params = COLON_PATTERN.split(correctHash);
        final int iterations = Integer.parseInt(params[ITERATION_INDEX]);
        final byte[] salt = fromBase64(params[SALT_INDEX]);
        final byte[] hash = fromBase64(params[PBKDF2_INDEX]);
        // Compute the hash of the provided password, using the same salt,
        // iteration count, and hash length
        final byte[] testHash = pbkdf2(password, salt, iterations, hash.length);
        // Compare the hashes in constant time. The password is correct if
        // both hashes match.
        return slowEquals(hash, testHash);
    }

    /**
     * Compares two byte arrays in length-constant time. This comparison method
     * is used so that password hashes cannot be extracted from an on-line
     * system using a timing attack and then attacked off-line.
     *
     * @param   a       the first byte array
     * @param   b       the second byte array
     * @return true if both byte arrays are the same, false if not
     */
    private static boolean slowEquals(final byte[] a, final byte[] b)
    {
        int diff = a.length ^ b.length;
        for(int i = 0; i < a.length && i < b.length; i++)
            diff |= a[i] ^ b[i];
        return diff == 0;
    }

    /**
     *  Computes the PBKDF2 hash of a password.
     *
     * @param   password    the password to hash.
     * @param   salt        the salt
     * @param   iterations  the iteration count (slowness factor)
     * @param   bytes       the length of the hash to compute in bytes
     * @return the PBDKF2 hash of the password
     */
    private static byte[] pbkdf2(final char[] password, final byte[] salt, final int iterations, final int bytes)
            throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        final KeySpec spec = new PBEKeySpec(password, salt, iterations, bytes * 8);
        final SecretKeyFactory skf = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
        return skf.generateSecret(spec).getEncoded();
    }

    /**
     * Converts a string of hexadecimal characters into a byte array.
     *
     * @param   hex         the hex string
     * @return the hex string decoded into a byte array
     */
    private static byte[] fromBase64(final String hex)
    {
        return DatatypeConverter.parseBase64Binary(hex);
    }

    /**
     * Converts a byte array into a hexadecimal string.
     *
     * @param   array       the byte array to convert
     * @return a length*2 character string encoding the byte array
     */
    private static String toBase64(final byte[] array)
    {
        return DatatypeConverter.printBase64Binary(array);
    }
}