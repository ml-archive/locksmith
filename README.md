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
        
        // Example using Fingerprint Encryption
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        // If were using a version above 23 we can use Fingerprint Encryption
            Locksmith.Builder(this)
                    .setKeyValidityDuration(120) // How many seconds should the key be valid for after fingerprint auth
                    .setUseFingerprint(true)
                    .build()
        } 
        
        // Example normal encryption (Api 23 Above)
     
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Locksmith.Builder(this)
                    .setUseFingerprint(false)
                    .build()
        } 
        
        // If using a version below 23 you should initiate the following method
         Locksmith.Builder(this).build()
    }
```

### Using Fingerprint Auth

If you enabled the `setUseFingerprint` flag during the configuration then the following section will be useful to you otherwise skip to the next section

##### Step 1) Show Dialog

```Kotlin
    // This only needs to be done if you enabled Fingerprint auth on the config section
    private fun showFingerprintDialog(){
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Locksmith.getInstance()
                    .getFingerprintDialogBuilder(this) // Provide context for our dialog
                    .setTitle(titleText) // Title Text
                    .setSubtitle(subtitleText) // Subtitle Text
                    .setDescription(descriptionText) // Description Text
                    .setSuccessMessage(successMessage) // Message shown when you have successfully auth
                    .setErrorMessage(errorMessage) // Error message shown when auth failed
                    .setCancelText(cancelText) // Cancel button text
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

### Encrypting Data

The following methods are available for encrypting/decrypting data

```
    Locksmith.getInstance().encryptString(data: String): String
    Locksmith.getInstance().decryptString(data: String): String 
    // Int Encrypt/Decrypt
    Locksmith.getInstance().encryptInt(data: Int): String
    Locksmith.getInstance().decryptInt(data: String): Int
    // Boolean Encrypt/Decrypt
    Locksmith.getInstance().encryptBoolean(data: Boolean): String 
    Locksmith.getInstance().decryptBoolean(data: String): Boolean
    // Float Encrypt/Decrypt
    Locksmith.getInstance().encryptFloat(data: Float): String
    Locksmith.getInstance().decryptFloat(data: String): Float
    // Long Encrypt/Decrypt
    Locksmith.getInstance().encryptLong(data: Long): String
    Locksmith.getInstance().decryptLong(data: String): Long
```.

***Note: if you're using kotlin you need to be sure to catch `LocksmithEncryptionException` and handle the errors appropriately (see the section below for how to do that)***


####Step 3) Handling Encryption Errors

```Koltin
private fun handleException(e: LocksmithEncryptionException) {
    Log.e(TAG, "handleException")
    
    when (e.type) {
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
