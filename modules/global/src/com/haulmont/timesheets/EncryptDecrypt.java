/*
 * Copyright (c) 2016 Haulmont
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.haulmont.timesheets;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.spec.AlgorithmParameterSpec;

public class EncryptDecrypt {
    private static final String SALT = "string Private org.apache.commons.codec.digest.DigestUtils";

    private static final byte[] INIT_VECTOR = new byte[]{
            0x00, 0x01, 0x02, 0x03,
            0x04, 0x05, 0x06, 0x07,
            0x08, 0x09, 0x0a, 0x0b,
            0x0c, 0x0d, 0x0e, 0x0f
    };


    private Cipher eCipher;
    private Cipher dCipher;


    public EncryptDecrypt(String key) {
        try {
            String data = new StringBuilder(SALT + key).reverse().toString();
            SecretKeySpec secretKey = new SecretKeySpec(DigestUtils.md5(data), "AES");
            AlgorithmParameterSpec paramSpec = new IvParameterSpec(INIT_VECTOR);
            eCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            dCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            eCipher.init(Cipher.ENCRYPT_MODE, secretKey, paramSpec);
            dCipher.init(Cipher.DECRYPT_MODE, secretKey, paramSpec);
        } catch (Exception e) {
            throw new RuntimeException("Exception while init cipher:", e);
        }
    }

    public String encrypt(String plaintext) {
        try {
            byte[] buf = eCipher.doFinal(getBytesUtf8(plaintext));
            return encodeString(buf);
        } catch (IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Encrypt exception:", e);
        }
    }

    public String decrypt(String hexCipherText) {
        try {
            return newStringUtf8(dCipher.doFinal(decodeString(hexCipherText)));
        } catch (IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException | DecoderException e) {
            throw new RuntimeException("Decrypt exception:", e);
        }
    }

    private static byte[] getBytesUtf8(String string) throws UnsupportedEncodingException {
        if (string == null)
            return null;
        return string.getBytes("UTF-8");
    }

    public static String newStringUtf8(byte[] bytes) throws UnsupportedEncodingException {
        if (bytes == null)
            return null;
        return new String(bytes, "UTF-8");
    }

    private static String encodeString(byte[] raw) {
        if (raw == null)
            return null;
        return new String(Hex.encodeHex(raw));
    }

    private static byte[] decodeString(String string) throws DecoderException {
        if (string == null)
            return null;
        return Hex.decodeHex(string.toCharArray());
    }
}