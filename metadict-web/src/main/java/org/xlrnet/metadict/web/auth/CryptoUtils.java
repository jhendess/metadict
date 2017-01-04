/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Jakob Hendeß
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.xlrnet.metadict.web.auth;

import org.xlrnet.metadict.api.exception.MetadictRuntimeException;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

/**
 * Utility class with crypto related methods.
 */
public class CryptoUtils {

    /**
     * The default keylength for hashing passwords.
     */
    public static final int DEFAULT_KEYLENGTH = 256;

    /**
     * The default amount of iterations for hashing passwords.
     */
    public static final int DEFAULT_ITERATIONS = 50;

    /**
     * The default length for a salt.
     */
    public static final int DEFAULT_SALT_LENGTH = 32;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * Generates a new random salt of a given length.
     *
     * @param length
     *         Length of the salt to generate
     * @return The generated salt.
     */
    public static byte[] generateSalt(int length) {
        byte[] salt = new byte[length];
        SECURE_RANDOM.nextBytes(salt);
        return salt;
    }

    /**
     * Hashes the given password using a custom salt.
     *
     * @param password
     *         The password to hash.
     * @param salt
     *         The salt to use.
     * @param iterations
     *         Amount of iterations to use.
     * @param keyLength
     *         Key length to use.
     * @return
     */
    public static byte[] hashPassword(final char[] password, final byte[] salt, final int iterations, final int keyLength) {
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
            SecretKey key = skf.generateSecret(spec);
            return key.getEncoded();

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new MetadictRuntimeException(e);
        }
    }
}
