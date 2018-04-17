package dk.nodes.locksmith.core.util;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class HashingUtilsTest {
    private String testString = "This is a test hash";
    private String sha1TestResult = "bd76dc781a1576047dd5f82e5206a012714fe022";
    private String sha256TestResult = "874560b0db4360448acaa8edabe16524c822a000a08358100607215396b76057";
    private String sha512TestResult = "5a021be64aad901d553eda5d175fae6ee2c158a7b3dfce5b3b0677dfffef42a793177936b5960f1bdc70cb6ef6cf02d4c8ea7587293ab8c4625c39e9ac9d7af4";

    @Test
    public void sha1_shouldPass() throws Exception {
        String result = HashingUtils.sha1(testString);
        assertEquals(result, sha1TestResult);
    }

    @Test
    public void sha256_shouldPass() throws Exception {
        String result = HashingUtils.sha256(testString);
        assertEquals(result, sha256TestResult);
    }

    @Test
    public void sha512_shouldPass() throws Exception {
        String result = HashingUtils.sha512(testString);
        assertEquals(result, sha512TestResult);
    }

}