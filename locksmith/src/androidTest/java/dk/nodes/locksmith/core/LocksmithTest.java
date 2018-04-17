package dk.nodes.locksmith.core;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class LocksmithTest {
    public String testString = "This is a test string";
    public boolean testBoolean = true;
    public long testLong = Long.MAX_VALUE;
    public int testInteger = Integer.MAX_VALUE;
    public float testFloat = Float.MAX_VALUE;

    @Before
    public void setUp() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();

        if (Locksmith.getInstance() == null) {
            new Locksmith.Builder(appContext)
                    .setUseFingerprint(false)
                    .build()
                    .init();
        }
    }

    @Test
    public void testEncryptDecryptString() throws Exception {
        String encryptedValue = Locksmith.getInstance().encryptString(testString);

        Assert.assertNotEquals(encryptedValue, testString);

        String decryptedValue = Locksmith.getInstance().decryptString(encryptedValue);

        Assert.assertEquals(decryptedValue, testString);
    }

    @Test
    public void testEncryptDecryptInt() throws Exception {
        String encryptedValue = Locksmith.getInstance().encryptInt(testInteger);

        int decryptedValue = Locksmith.getInstance().decryptInt(encryptedValue);

        Assert.assertEquals(decryptedValue, testInteger);
    }

    @Test
    public void testEncryptDecryptBoolean() throws Exception {
        String encryptedValue = Locksmith.getInstance().encryptBoolean(testBoolean);

        Boolean decryptedValue = Locksmith.getInstance().decryptBoolean(encryptedValue);

        Assert.assertEquals(decryptedValue, testBoolean);
    }

    @Test
    public void testEncryptDecryptLong() throws Exception {
        String encryptedValue = Locksmith.getInstance().encryptLong(testLong);

        long decryptedValue = Locksmith.getInstance().decryptLong(encryptedValue);

        Assert.assertEquals(decryptedValue, testLong);
    }

    @Test
    public void testEncryptDecryptFloat() throws Exception {
        String encryptedValue = Locksmith.getInstance().encryptFloat(testFloat);

        float decryptedValue = Locksmith.getInstance().decryptFloat(encryptedValue);

        Assert.assertEquals(decryptedValue, testFloat, 0);
    }


}