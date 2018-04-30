package dk.nodes.locksmith.example.fragments

import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dk.nodes.locksmith.core.Locksmith
import dk.nodes.locksmith.core.exceptions.LocksmithException
import dk.nodes.locksmith.core.exceptions.LocksmithException.Type.*
import dk.nodes.locksmith.core.models.FingerprintDialogEvent
import dk.nodes.locksmith.core.models.OnFingerprintDialogEventListener

import dk.nodes.locksmith.example.R
import dk.nodes.locksmith.example.dialogs.CustomFingerprintDialog
import kotlinx.android.synthetic.main.fragment_encryption.*

@RequiresApi(Build.VERSION_CODES.M)
class FingerprintEncryptionFragment : Fragment(), OnFingerprintDialogEventListener {
    private val defaultData = "Mary had a little lamb\n" +
            "It's fleece was white as snow, yeah\n" +
            "Everywhere the child went\n" +
            "The lamb, the lamb was sure to go, yeah"
    private var encryptedData: String = ""

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_encryption, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        encryptedData = defaultData
        fragmentEtData.setText(defaultData)

        setupListeners()
    }

    private fun setupListeners() {
        fragmentTvData.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                encryptedData = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        fragmentBtnEncrypt.setOnClickListener {
            encryptData()
        }

        fragmentBtnDecrypt.setOnClickListener {
            decryptData()
        }
    }


    private fun encryptData() {
        Log.d(tag, "encryptData")

        try {
            encryptedData = Locksmith.instance.fingerprintEncryptionManager.encryptString(
                    encryptedData
            )
        } catch (e: LocksmithException) {
            handleException(e)
        }

        updateTextView()
    }

    private fun decryptData() {
        Log.d(tag, "decryptData")

        try {
            encryptedData = Locksmith.instance.fingerprintEncryptionManager.decryptString(
                    encryptedData
            )
        } catch (e: LocksmithException) {
            handleException(e)
        }

        updateTextView()
    }

    private fun updateTextView() {
        fragmentTvData.text = encryptedData
    }


    private fun handleException(e: LocksmithException) {
        Log.e(tag, "handleException")
        e.printStackTrace()

        when (e.type) {
            Initiation,
            Uninitiated     -> {
                Log.e(tag, "Uninitiated")
                showDialog()
            }
            Unauthenticated -> {
                Log.e(tag, "Unauthenticated")
                showDialog()
            }
            InvalidData     -> {
                showSnackbar(R.string.errorInvalidData)
            }
            EncryptionError -> {
                showSnackbar(R.string.errorGeneric)
            }
            Generic         -> {
                Log.e(tag, "Generic", e)
            }
        }
    }

    private fun showDialog() {
        if (fragmentSwtDialog.isChecked) {
            showFingerprintDialogCustom()
        } else {
            showFingerprintDialog()
        }
    }


    private fun showFingerprintDialogCustom() {
        Log.d(tag, "showFingerprintDialog")

        val context = this.context ?: return

        val dialog = CustomFingerprintDialog(context)

        dialog.setOnFingerprintDialogEventListener(this)

        dialog.onUsePasswordBtnListener = {
            Log.d(tag, "onUsePasswordBtnListener")
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showFingerprintDialog() {
        val context = this.context ?: return

        val cancelText = getString(R.string.cancel)
        val titleText = getString(R.string.fingerprintDialogTitle)
        val subtitleText = getString(R.string.fingerprintDialogSubtitle)
        val descriptionText = getString(R.string.fingerprintDialogDescription)

        val successMessage = getString(R.string.fingerprintSuccessMessage)
        val errorMessage = getString(R.string.fingerprintErrorMessage)


        Locksmith.getInstance()
                .getFingerprintDialogBuilder(context)
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


    override fun onFingerprintEvent(event: FingerprintDialogEvent) {
        when (event) {
            FingerprintDialogEvent.CANCEL           -> {
                Log.w(tag, "CANCEL")
            }
            FingerprintDialogEvent.SUCCESS          -> {
                Log.w(tag, "SUCCESS")
            }
            FingerprintDialogEvent.ERROR            -> {
                Log.w(tag, "ERROR")
            }
            FingerprintDialogEvent.ERROR_SECURE     -> {
                Log.w(tag, "ERROR_SECURE")
            }
            FingerprintDialogEvent.ERROR_HARDWARE   -> {
                Log.w(tag, "ERROR_HARDWARE")
            }
            FingerprintDialogEvent.ERROR_ENROLLMENT -> {
                Log.w(tag, "ERROR_ENROLLMENT")
            }
            FingerprintDialogEvent.ERROR_CIPHER     -> {
                Log.w(tag, "ERROR_ENROLLMENT")
            }
        }
    }

    private fun showSnackbar(@StringRes message: Int) {
        Snackbar.make(
                fragmentRootContainer,
                message,
                Snackbar.LENGTH_SHORT
        ).show()
    }
}
