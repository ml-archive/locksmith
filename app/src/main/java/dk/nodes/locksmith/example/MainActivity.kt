package dk.nodes.locksmith.example

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import dk.nodes.locksmith.core.Locksmith
import dk.nodes.locksmith.core.encryption.EncryptionManager
import dk.nodes.locksmith.core.exceptions.LocksmithEncryptionException
import dk.nodes.locksmith.core.exceptions.LocksmithEncryptionException.Type.*
import dk.nodes.locksmith.core.fingerprint.FingerprintDialog
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), FingerprintDialog.OnFingerprintDialogEventListener {
    private val TAG = MainActivity::class.simpleName

    private val originalData = "Mary had a little lamb\n" +
            "It's fleece was white as snow, yeah\n" +
            "Everywhere the child went\n" +
            "The lamb, the lamb was sure to go, yeah"

    private var currentData = originalData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(mainToolbar)

        setupListeners()

        mainTvOriginalData.text = originalData
    }

    private fun setupListeners() {
        mainBtnEncrypt.setOnClickListener {
            encryptData()
        }

        mainBtnDecrypt.setOnClickListener {
            decryptData()
        }
    }


    @SuppressLint("NewApi")
    private fun encryptData() {
        Log.d(TAG, "encryptData")

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Log.d(TAG, "Unsupported Android Version: " + Build.VERSION.SDK_INT)
            Snackbar.make(
                    mainRootContainer,
                    R.string.errorUnsupportedAndroidVersion,
                    Snackbar.LENGTH_SHORT
            )
            return
        }

        try {
            currentData = Locksmith.encrypt(currentData)
        } catch (e: LocksmithEncryptionException) {
            handleException(e)
        }

        updateTextView()
    }

    @SuppressLint("NewApi")
    private fun decryptData() {
        Log.d(TAG, "decryptData")

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Log.d(TAG, "Unsupported Android Version: " + Build.VERSION.SDK_INT)
            Snackbar.make(
                    mainRootContainer,
                    R.string.errorUnsupportedAndroidVersion,
                    Snackbar.LENGTH_SHORT
            )
        }

        try {
            currentData = Locksmith.decrypt(currentData)
        } catch (e: LocksmithEncryptionException) {
            handleException(e)
        }

        updateTextView()
    }

    private fun handleException(e: LocksmithEncryptionException) {
        Log.e(TAG, "handleException")
        e.printStackTrace()

        when (e.type) {
            UninitiatedCipher,
            Unauthenticated -> {
                Log.e(TAG, "UninitiatedCipher,Unauthenticated")
                showFingerprintDialog()
            }
            InvalidData     -> {
                Snackbar.make(
                        mainRootContainer,
                        R.string.errorInvalidData,
                        Snackbar.LENGTH_SHORT
                ).show()
            }
            InvalidKey,
            InvalidAlgorithm,
            IllegalBlockSize,
            BadPadding      -> {
                Snackbar.make(
                        mainRootContainer,
                        R.string.errorGeneric,
                        Snackbar.LENGTH_SHORT
                ).show()
            }
            Generic         -> {
                Log.e(TAG, "Generic")
            }
        }
    }

    private fun showFingerprintDialog() {
        Log.d(TAG, "showFingerprintDialog")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            val cancelText = getString(R.string.cancel)
            val titleText = getString(R.string.fingerprintDialogTitle)
            val subtitleText = getString(R.string.fingerprintDialogSubtitle)
            val descriptionText = getString(R.string.fingerprintDialogDescription)

            val successMessage = getString(R.string.fingerprintDialogSuccessMessage)
            val errorMessage = getString(R.string.fingerprintDialogErrorMessage)

            FingerprintDialog.Builder(this)
                    .setTitle(titleText)
                    .setSubtitle(subtitleText)
                    .setDescription(descriptionText)
                    .setSuccessMessage(successMessage)
                    .setErrorMessage(errorMessage)
                    .setCancelText(cancelText)
                    .setKeyValidityDuration(10)
                    .setEventListener(this)
                    .build()
                    .show()
        }
    }

    private fun updateTextView() {
        mainTvDecryptedData.text = currentData
    }

    override fun onFingerprintEvent(event: FingerprintDialog.FingerprintDialogEvent) {
        when (event) {
            FingerprintDialog.FingerprintDialogEvent.CANCEL           -> {
                Log.w(TAG, "CANCEL")
            }
            FingerprintDialog.FingerprintDialogEvent.SUCCESS          -> {
                Log.w(TAG, "SUCCESS")
            }
            FingerprintDialog.FingerprintDialogEvent.ERROR            -> {
                Log.w(TAG, "ERROR")
            }
            FingerprintDialog.FingerprintDialogEvent.ERROR_SECURE     -> {
                Log.w(TAG, "ERROR_SECURE")
            }
            FingerprintDialog.FingerprintDialogEvent.ERROR_HARDWARE   -> {
                Log.w(TAG, "ERROR_HARDWARE")
            }
            FingerprintDialog.FingerprintDialogEvent.ERROR_ENROLLMENT -> {
                Log.w(TAG, "ERROR_ENROLLMENT")
            }
            FingerprintDialog.FingerprintDialogEvent.ERROR_CIPHER     -> {
                Log.w(TAG, "ERROR_ENROLLMENT")
            }
        }
    }
}
