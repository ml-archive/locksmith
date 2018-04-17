package dk.nodes.locksmith.core.preferences;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import dk.nodes.locksmith.core.Locksmith;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class EncryptedPreferencesTest {
    private EncryptedPreferences encryptedPreferences;

    @Before
    public void setUp() throws Exception {
        if (encryptedPreferences == null) {

            Context appContext = InstrumentationRegistry.getTargetContext();

            encryptedPreferences = new EncryptedPreferences(appContext, "ExamplePreferences", Context.MODE_PRIVATE);

            new Locksmith.Builder(appContext)
                    .setUseFingerprint(false)
                    .build()
                    .init();
        }

        encryptedPreferences.clear();
    }

    @Test
    public void testWriteStringPreferences() throws Exception {
        String testKey = "TestStringKey";
        String testString = "Test String";
        String testDefaultString = "Test Default String";

        String shouldReturnDefaultString = encryptedPreferences.getString(testKey, testDefaultString);

        Assert.assertEquals(shouldReturnDefaultString, testDefaultString);

        encryptedPreferences.putString(testKey, testString);

        String shouldReturnTestString = encryptedPreferences.getString(testKey, testDefaultString);

        Assert.assertEquals(shouldReturnTestString, testString);
    }

    @Test
    public void testWriteIntPreferences() throws Exception {
        String testKey = "TestIntKey";
        int testString = Integer.MAX_VALUE;
        int testDefaultString = Integer.MIN_VALUE;

        int shouldReturnDefault = encryptedPreferences.getInt(testKey, testDefaultString);

        Assert.assertEquals(shouldReturnDefault, testDefaultString);

        encryptedPreferences.putInt(testKey, testString);

        int shouldReturnTest = encryptedPreferences.getInt(testKey, testDefaultString);

        Assert.assertEquals(shouldReturnTest, testString);
    }


    @Test
    public void testWriteLongPreferences() throws Exception {
        String testKey = "TestLongKey";
        long testString = Long.MAX_VALUE;
        long testDefaultString = Long.MIN_VALUE;

        long shouldReturnDefault = encryptedPreferences.getLong(testKey, testDefaultString);

        Assert.assertEquals(shouldReturnDefault, testDefaultString);

        encryptedPreferences.putLong(testKey, testString);

        long shouldReturnTest = encryptedPreferences.getLong(testKey, testDefaultString);

        Assert.assertEquals(shouldReturnTest, testString);
    }

    @Test
    public void testWriteFloatPreferences() throws Exception {
        String testKey = "TestFloatKey";
        float testString = Float.MAX_VALUE;
        float testDefaultString = Float.MIN_VALUE;

        float shouldReturnDefault = encryptedPreferences.getFloat(testKey, testDefaultString);

        Assert.assertEquals(shouldReturnDefault, testDefaultString);

        encryptedPreferences.putFloat(testKey, testString);

        float shouldReturnTest = encryptedPreferences.getFloat(testKey, testDefaultString);

        Assert.assertEquals(shouldReturnTest, testString);
    }

    @Test
    public void testWriteBooleanPreferences() throws Exception {
        String testKey = "TestBooleanKey";
        boolean testString = Boolean.TRUE;
        boolean testDefaultString = Boolean.FALSE;

        boolean shouldReturnDefault = encryptedPreferences.getBoolean(testKey, testDefaultString);

        Assert.assertEquals(shouldReturnDefault, testDefaultString);

        encryptedPreferences.putBoolean(testKey, testString);

        boolean shouldReturnTest = encryptedPreferences.getBoolean(testKey, testDefaultString);

        Assert.assertEquals(shouldReturnTest, testString);
    }
}