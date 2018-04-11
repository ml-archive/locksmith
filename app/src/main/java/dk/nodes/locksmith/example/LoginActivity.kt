package dk.nodes.locksmith.example

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import dk.nodes.locksmith.fingerprint.FingerprintDialog
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), FingerprintDialog.OnFingerprintDialogEventListener {
    private val TAG = LoginActivity::class.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setSupportActionBar(loginToolbar)

        setupListeners()
    }

    private fun setupListeners() {
        loginBtnFingerprint.setOnClickListener {
            Log.d(TAG, "loginBtnFingerprint Clicked")
            doFingerprintAuth()
        }
    }

    private fun doFingerprintAuth() {
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
                    .setEventListener(this)
                    .build()
                    .show(fragmentManager, "FingerprintDialog")
        }
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    override fun onFingerprintEvent(event: FingerprintDialog.FingerprintDialogEvent) {
        when (event) {
            FingerprintDialog.FingerprintDialogEvent.CANCEL           -> {
                Log.w(TAG, "CANCEL")
            }
            FingerprintDialog.FingerprintDialogEvent.SUCCESS          -> {
                Log.w(TAG, "SUCCESS")
                startMainActivity()
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
        }
    }
}
