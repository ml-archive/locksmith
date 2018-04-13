![Locksmith Badge](https://img.shields.io/maven-central/v/dk.nodes.locksmith/core.svg)

# Locksmith

Nodes Encryption library using the Android KeyStore

## 🔧 Installation
Installation is pretty simple just add the following line to your gradle dependecies (See badge for current version)
```
  implementation 'dk.nodes.locksmith.core:x.x.+'
```

## Description

The library itself is written in Java but most of the examples you'll find here (including the sample app itself) is written in kotlin. The aim of the library is to simplify and have the same fingerprint authentication and encryption process across all of our apps

## Getting Started

##### Step 1) Fingerprint Authentication
```Kotlin
    private fun showFingerprintDialog(){
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            FingerprintDialog.Builder(this) // Provide context for our dialog
                    .setTitle(titleText) // Title Text
                    .setSubtitle(subtitleText) // Subtitle Text
                    .setDescription(descriptionText) // Description Text
                    .setSuccessMessage(successMessage) // Message shown when you have successfully auth
                    .setErrorMessage(errorMessage) // Error message shown when auth failed
                    .setCancelText(cancelText) // Cancel button text
                    .setKeyValidityDuration(10) // How long should the key be valid for once authenthicated (Can only be set once)
                    .setEventListener(this) // Set an event listener (See next step for a better explination)
                    .build() // Build our dialog
                    .show() // Show our dialog
        }
    }
```
Step is pretty self explanatory we just use a builder to create our dialog that will allow our user to auth.

##### Step 2) Add Event Listener

```
FingerprintDialog.OnFingerprintDialogEventListener
```
In order to listen to events from the dialog you must implement the `OnFingerprintDialogEventListener` as shown above.

```Kotlin
override fun onFingerprintEvent(event: FingerprintDialog.FingerprintDialogEvent) {
        when (event) {
            FingerprintDialog.FingerprintDialogEvent.CANCEL           -> {
                Log.w(TAG, "CANCEL")
                // Returns the following event when the user cancels the dialog
            }
            FingerprintDialog.FingerprintDialogEvent.SUCCESS          -> {
                Log.w(TAG, "SUCCESS")
                // Returns the following event when the correct fingerprint has been read
            }
            FingerprintDialog.FingerprintDialogEvent.ERROR            -> {
                Log.w(TAG, "ERROR")
                 // Returns the following event when a fingerprint was correctly read but not accepted
            }
            FingerprintDialog.FingerprintDialogEvent.ERROR_SECURE     -> {
                Log.w(TAG, "ERROR_SECURE")
                // Returns the following event when the lock screen isn't enabled
            }
            FingerprintDialog.FingerprintDialogEvent.ERROR_HARDWARE   -> {
                Log.w(TAG, "ERROR_HARDWARE")
                // Returns the following event when there is no fingerprint hardware
            }
            FingerprintDialog.FingerprintDialogEvent.ERROR_ENROLLMENT -> {
                Log.w(TAG, "ERROR_ENROLLMENT")
                // Returns the following event when no fingerprints are enrolled
            }
            FingerprintDialog.FingerprintDialogEvent.ERROR_CIPHER     -> {
                Log.w(TAG, "ERROR_ENROLLMENT")
                // Returns the following event when the cipher failed to initate
            }
        }
    }
```

The dialog will return the following events

### Encrypting Data (Optional)

If the only thing you're looking to do is get verification for a login then the following steps aren't required these are just purely for encrypting and decrypting data

****Note: You should be authenthicated first via the above method before trying to encrypt/decrypt data****

##### Step 1) Encrypting Data

```Kotlin
    private fun encryptData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                val testString = "Test"
                val encryptedData = Locksmith.encrypt(testString)
            } catch (e: LocksmithEncryptionException) {
                handleException(e) // See step 3 for how to handle errors
            }
        }
    }
```

##### Step 2) Decrypting Data

```Kotlin
    private fun decryptData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                val decryptedData = Locksmith.decrypt(encryptedData)
            } catch (e: LocksmithEncryptionException) {
                handleException(e) // See step 3 for how to handle errors
            }
        }
    }
```

##### Step 3) Handling Errors

```Kotlin
    private fun handleException(e: LocksmithEncryptionException) {
        when (e.type) {
            UninitiatedCipher-> {
                // Will return this if the Fingerprint Dialog was not shown first before trying to encrypt/decrypt                          
            }
            Unauthenticated -> {
                // Returns this error if the key used to encrypt/decrypt has been invalidated, if you get this error you
                // Should show the fingerprint dialog again
            }
            InvalidData     -> {
               // Will return this if the data provided is not valid encrypted data
            }
            InvalidKey,
            InvalidAlgorithm,
            IllegalBlockSize,
            BadPadding      -> {
               // Will return this if for whatever reason we fail to encrypt/decrypt data
            }
            Generic         -> {
                // A generic catch for our error
            }
        }
    }
```

Because of the way Kotlin handles checked exceptions we opted to wrap all exceptions and return a ENUM with the exception as well (makes it easier)

## 💻 Developers
- @brianestrada
