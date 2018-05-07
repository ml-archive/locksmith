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


##### Step 1) Initiate Library
```Kotlin
    override fun onCreate() {
        super.onCreate()
        // Configure our Locksmith instance
        val locksmithConfiguration = LocksmithConfiguration()
        locksmithConfiguration.keyValidityDuration = 120
        // Start our locksmith instance
        Locksmith.init(this, locksmithConfiguration)
    }
```

### Using Fingerprint Auth

Next section will cover using the fingerprint encryption portion of the Library if you're not going to use this just skip to the next section

##### Step 1) Show Dialog

```Kotlin
    // This only needs to be done if you enabled Fingerprint auth on the config section
    private fun showFingerprintDialog(){
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Locksmith.getInstance()
                    .getFingerprintDialogBuilder(context)
                    .setTitle(titleText)
                    .setSubtitle(subtitleText)
                    .setDescription(descriptionText)
                    .setSuccessMessage(successMessage)
                    .setErrorMessage(errorMessage)
                    .setCancelText(cancelText)
                    .setEventListener(this) // Sets the event listener for our dialog
                    .build()
                    .show()
        }
    }
```

After setting up the dialog and showing it, the authentication process will then be handled by the dialog, and should return a successful event or an error even see the following section for events and types.

***Note: You are also able to extend the `FingerprintDialogBase` or `FingerprintAlertDialogBase` to write your own custom dialog***

##### Step 2) Add Event Listener

```
OnFingerprintDialogEventListener
```
In order to listen to events from the dialog you must implement the `OnFingerprintDialogEventListener` as shown above.

```Kotlin
    override fun onFingerprintEvent(event: FingerprintDialogEvent) {
        when (event) {
            FingerprintDialogEvent.CANCEL           -> {
                Log.w(tag, "CANCEL") // Event fired when user cancels the dialog
            }
            FingerprintDialogEvent.SUCCESS          -> {
                Log.w(tag, "SUCCESS") // Event fired when user successfully authenticated
            }
            FingerprintDialogEvent.ERROR            -> {
                Log.w(tag, "ERROR") // A fingerprint was read but it was an incorrect fingerprint
            }
            FingerprintDialogEvent.ERROR_SECURE     -> {
                Log.w(tag, "ERROR_SECURE") // Device does not have lock screen enabled
            }
            FingerprintDialogEvent.ERROR_HARDWARE   -> {
                Log.w(tag, "ERROR_HARDWARE") // Device does not have fingerprint hardware
            }
            FingerprintDialogEvent.ERROR_ENROLLMENT -> {
                Log.w(tag, "ERROR_ENROLLMENT") // Device has no fingerprints enrolled
            }
            FingerprintDialogEvent.ERROR_CIPHER     -> {
                Log.w(tag, "ERROR_ENROLLMENT") // Error initialization a cipher
            }
        }
    }
```

The dialog will return the following events

### Encrypting Data

For encrypting data you have two main forms of doing so you can either use the `fingerprintEncryptionManager` or `encryptionManager`

If you're going to use the `fingerprintEncryptionManager` you must authenticate first using the method in the first step otherwise the standard `encryptionManager`

```
    Locksmith.instance.encryptionManager.encryptString(data)
    Locksmith.instance.encryptionManager.decryptString(data) 
    // Int Encrypt/Decrypt
    Locksmith.instance.encryptionManager.encryptInt(data)
    Locksmith.instance.encryptionManager.decryptInt(data)
    // Boolean Encrypt/Decrypt
    Locksmith.instance.encryptionManager.encryptBoolean(data) 
    Locksmith.instance.encryptionManager.decryptBoolean(data)
    // Float Encrypt/Decrypt
    Locksmith.instance.encryptionManager.encryptFloat(data)
    Locksmith.instance.encryptionManager.decryptFloat(data)
    // Long Encrypt/Decrypt
    Locksmith.instance.encryptionManager.encryptLong(data)
    Locksmith.instance.encryptionManager.decryptLong(data)
```

***Note: if you're using kotlin you need to be sure to catch `LocksmithEncryptionException` and handle the errors appropriately (see the section below for how to do that)***


####Step 3) Handling Encryption Errors

```Koltin
private fun handleException(e: LocksmithEncryptionException) {
    Log.e(TAG, "handleException")
    when (e.type) {
       /**
         * Will return this if for some reason the encryption cipher failed to be initiated
         */
         
        Initiation -> {}
        
       /**
         * Will return this type if the cipher/algorithm was not properly initiated
         */
         
        Uninitiated -> {}
        
        /**
         * Will return this type if key has expired 
         * (will usually require you to go through the fingerprint validation sequence again)
         */
         
        Unauthenticated -> {}
        
        /**
         * Will return this type if the data fed to the encrypt method isn't a valid encrypted message
         */
         
        InvalidData -> {}
        
        /**
         * Will return this type if the data is too long or the wrong size
         */
         
        EncryptionError -> {}
        
        /**
         * Thrown when an unknown error is caught
         */
         
        Generic -> {}
    }
}
```

Because of the way Kotlin handles checked exceptions we opted to wrap all exceptions and return a ENUM with the exception as well (makes it easier)

## 💻 Developers
- @brianestrada
