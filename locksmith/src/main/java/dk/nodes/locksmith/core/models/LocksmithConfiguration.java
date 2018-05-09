package dk.nodes.locksmith.core.models;

public class LocksmithConfiguration {
    public int keyValidityDuration = 120;

    public LocksmithConfiguration() {
        // Do nothing
    }

    public LocksmithConfiguration(int keyValidityDuration) {
        this.keyValidityDuration = keyValidityDuration;
    }

    public int getKeyValidityDuration() {
        return keyValidityDuration;
    }

    public void setKeyValidityDuration(int keyValidityDuration) {
        this.keyValidityDuration = keyValidityDuration;
    }
}
