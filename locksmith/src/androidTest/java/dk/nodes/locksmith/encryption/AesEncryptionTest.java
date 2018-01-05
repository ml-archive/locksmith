package dk.nodes.locksmith.encryption;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.crypto.spec.SecretKeySpec;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class AesEncryptionTest {
    private static SecretKeySpec key;
    private AesEncryption encryption;
    private String testString = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas pharetra tristique lacus, vel aliquam nunc eleifend vitae. Mauris vitae faucibus neque, sed ornare augue. Donec laoreet eleifend nisl. Sed finibus odio eu leo imperdiet, at lobortis tortor dignissim. Aenean nec pretium mi. Sed consectetur eu neque ac bibendum. Suspendisse sit amet diam dictum, viverra diam vel, luctus dolor. Donec efficitur sem eget convallis interdum. Nam metus ligula, tincidunt a urna vel, euismod euismod risus. Mauris eget sem tempor, rutrum justo ac, pellentesque nibh. Nunc elit libero, ornare id dignissim nec, accumsan sed libero. Vestibulum laoreet ante sit amet libero feugiat egestas. Nam rhoncus ut lorem vel fringilla. Nunc facilisis enim risus, sit amet rutrum leo mattis a. Pellentesque commodo sodales rhoncus.";

    @Before
    public void setup() throws Exception {

        if (key == null) {
            key = AesEncryption.generateKey();
        }

        encryption = new AesEncryption(key);
    }

    @Test
    public void encryptionTest() throws Exception {
        String encryptedString = encryption.encryptString(testString);

        Assert.assertNotEquals(testString, encryptedString);
    }

    @Test
    public void decryptionTest_shouldPass() throws Exception {
        String encryptedString = encryption.encryptString(testString);

        Assert.assertNotEquals(testString, encryptedString);

        System.out.println(String.format("EncryptedString: %s", encryptedString));

        String decryptedString = encryption.decryptString(encryptedString);

        Assert.assertEquals(testString, decryptedString);
    }
}
