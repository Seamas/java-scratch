package wang.seamas.scratch.web.sm.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SM4CryptoUtilTest {

    @Test
    public void testEncrypt() {
        SM4KeyUtil.SM4KeyWithIV keyWithIV = SM4KeyUtil.generateKeyWithIV();
        String key = keyWithIV.key();
        String iv = keyWithIV.iv();

        String plainText = "Hello SM4";
        String encryptedText = SM4CryptoUtil.encrypt(plainText, key, iv);
        System.out.println("Encrypted Text: " + encryptedText);
        String decryptedText = SM4CryptoUtil.decrypt(encryptedText, key, iv);
        System.out.println("Decrypted Text: " + decryptedText);

        assertEquals(plainText, decryptedText, "Decrypted text does not match plain text");
    }
}
