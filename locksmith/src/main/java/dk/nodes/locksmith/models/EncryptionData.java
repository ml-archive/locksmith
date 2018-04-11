package dk.nodes.locksmith.models;

import android.util.Base64;

import java.util.Arrays;

import dk.nodes.locksmith.exceptions.InvalidDataException;

public class EncryptionData {
    private static final String deliminator = "_";
    public byte[] data;
    public byte[] iv;

    public EncryptionData(byte[] data, byte[] iv) {
        this.data = data;
        this.iv = iv;
    }

    public EncryptionData(String data) throws InvalidDataException {
        String[] splitData = data.split(deliminator);

        System.out.print(Arrays.toString(splitData));

        if (splitData.length == 2) {
            this.data = Base64.decode(splitData[0], Base64.NO_WRAP);
            this.iv = Base64.decode(splitData[1], Base64.NO_WRAP);
        } else {
            throw new InvalidDataException();
        }
    }

    public String encode() {
        return Base64.encodeToString(data, Base64.NO_WRAP) + deliminator + Base64.encodeToString(iv, Base64.NO_WRAP);
    }

    @Override
    public String toString() {
        return "\n Data: " + Arrays.toString(data)
                + "\n IV: " + Arrays.toString(iv);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EncryptionData that = (EncryptionData) o;

        return Arrays.equals(data, that.data) && Arrays.equals(iv, that.iv);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(data);
        result = 31 * result + Arrays.hashCode(iv);
        return result;
    }
}
