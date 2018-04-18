package dk.nodes.locksmith.example

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import dk.nodes.locksmith.core.Locksmith
import dk.nodes.locksmith.core.exceptions.LocksmithEncryptionException
import dk.nodes.locksmith.core.exceptions.LocksmithEncryptionException.Type.*
import dk.nodes.locksmith.core.models.FingerprintDialogEvent
import dk.nodes.locksmith.core.models.OnFingerprintDialogEventListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), OnFingerprintDialogEventListener {
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

        try {
            currentData = Locksmith.getInstance().encryptString(currentData)
        } catch (e: LocksmithEncryptionException) {
            handleException(e)
        }

        updateTextView()
    }

    @SuppressLint("NewApi")
    private fun decryptData() {
        Log.d(TAG, "decryptData")

        try {
            currentData = Locksmith.getInstance().encryptString(currentData)
        } catch (e: LocksmithEncryptionException) {
            handleException(e)
        }

        updateTextView()
    }

    private fun handleException(e: LocksmithEncryptionException) {
        Log.e(TAG, "handleException")

        when (e.type) {
            Uninitiated     -> {
                Log.e(TAG, "Uninitiated")
                showFingerprintDialogCustom()
            }
            Unauthenticated -> {
                Log.e(TAG, "Unauthenticated")
                showFingerprintDialogCustom()
            }
            InvalidData     -> {
                Snackbar.make(
                        mainRootContainer,
                        R.string.errorInvalidData,
                        Snackbar.LENGTH_SHORT
                ).show()
            }
            EncryptionError -> {
                Snackbar.make(
                        mainRootContainer,
                        R.string.errorGeneric,
                        Snackbar.LENGTH_SHORT
                ).show()
            }
            Generic         -> {
                Log.e(TAG, "Generic", e)
            }
        }
    }

    private fun showFingerprintDialogCustom() {
        Log.d(TAG, "showFingerprintDialog")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            val dialog = CustomFingerprintDialog(this)

            dialog.setOnFingerprintDialogEventListener {
                when (it) {
                    FingerprintDialogEvent.CANCEL           -> {
                        Log.w(TAG, "CANCEL")
                    }
                    FingerprintDialogEvent.SUCCESS          -> {
                        Log.w(TAG, "SUCCESS")
                    }
                    FingerprintDialogEvent.ERROR            -> {
                        Log.w(TAG, "ERROR")
                    }
                    FingerprintDialogEvent.ERROR_SECURE     -> {
                        Log.w(TAG, "ERROR_SECURE")
                    }
                    FingerprintDialogEvent.ERROR_HARDWARE   -> {
                        Log.w(TAG, "ERROR_HARDWARE")
                    }
                    FingerprintDialogEvent.ERROR_ENROLLMENT -> {
                        Log.w(TAG, "ERROR_ENROLLMENT")
                    }
                    FingerprintDialogEvent.ERROR_CIPHER     -> {
                        Log.w(TAG, "ERROR_ENROLLMENT")
                    }
                }
            }
            dialog.onUsePasswordBtnListener = {
                Log.d(TAG, "onUsePasswordBtnListener")
                dialog.dismiss()
            }

            dialog.show()


        }
    }

    private fun showFingerprintDialog() {
        val cancelText = getString(R.string.cancel)
        val titleText = getString(R.string.fingerprintDialogTitle)
        val subtitleText = getString(R.string.fingerprintDialogSubtitle)
        val descriptionText = getString(R.string.fingerprintDialogDescription)

        val successMessage = getString(R.string.fingerprintSuccessMessage)
        val errorMessage = getString(R.string.fingerprintErrorMessage)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Locksmith.getInstance()
                    .getFingerprintDialogBuilder(this)
                    .setTitle(titleText)
                    .setSubtitle(subtitleText)
                    .setDescription(descriptionText)
                    .setSuccessMessage(successMessage)
                    .setErrorMessage(errorMessage)
                    .setCancelText(cancelText)
                    .setEventListener(this)
                    .build()
                    .show()
        }
    }

    private fun updateTextView() {
        mainTvDecryptedData.text = currentData
    }

    override fun onFingerprintEvent(event: FingerprintDialogEvent) {
        when (event) {
            FingerprintDialogEvent.CANCEL           -> {
                Log.w(TAG, "CANCEL")
            }
            FingerprintDialogEvent.SUCCESS          -> {
                Log.w(TAG, "SUCCESS")
            }
            FingerprintDialogEvent.ERROR            -> {
                Log.w(TAG, "ERROR")
            }
            FingerprintDialogEvent.ERROR_SECURE     -> {
                Log.w(TAG, "ERROR_SECURE")
            }
            FingerprintDialogEvent.ERROR_HARDWARE   -> {
                Log.w(TAG, "ERROR_HARDWARE")
            }
            FingerprintDialogEvent.ERROR_ENROLLMENT -> {
                Log.w(TAG, "ERROR_ENROLLMENT")
            }
            FingerprintDialogEvent.ERROR_CIPHER     -> {
                Log.w(TAG, "ERROR_ENROLLMENT")
            }
        }
    }
}
