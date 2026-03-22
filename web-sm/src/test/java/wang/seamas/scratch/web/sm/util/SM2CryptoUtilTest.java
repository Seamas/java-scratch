package wang.seamas.scratch.web.sm.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SM2CryptoUtilTest {

    @Test
    public void testEncrypt() {
        SM2KeyUtil.SM2KeyPair keyPair = SM2KeyUtil.generateKeyPair();
        String privateKey = keyPair.privateKey();
        String publicKey = keyPair.publicKey();

        String plainText = "Hello, SM2";
        String encryptedText = SM2CryptoUtil.encrypt(plainText, publicKey);
        System.out.println("Encrypted Text: " + encryptedText);
        String decryptedText = SM2CryptoUtil.decryptToString(encryptedText, privateKey);
        System.out.println("Decrypted Text: " + decryptedText);

        assertEquals(plainText, decryptedText, "Decrypted text does not match plain text");
    }


    @Test
    public void testSign(){
        SM2KeyUtil.SM2KeyPair keyPair = SM2KeyUtil.generateKeyPair();
        String privateKey = keyPair.privateKey();
        String publicKey = keyPair.publicKey();

        String plainText = "Hello, SM2";
        String sign = SM2CryptoUtil.sign(plainText, privateKey);
        System.out.println("Sign: " + sign);
        boolean result = SM2CryptoUtil.verify(plainText, sign, publicKey);
        System.out.println("Verify Result: " + result);

        assertTrue(result, "Signature verification failed");
    }
}
