package dk.nodes.locksmith.example.fragments

import android.os.Bundle
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
import dk.nodes.locksmith.example.R
import kotlinx.android.synthetic.main.fragment_encryption.*

class EncryptionFragment : Fragment() {
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
        fragmentSwtDialog.visibility = View.GONE

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
            encryptedData = Locksmith.instance.encryptionManager.encryptString(
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
            encryptedData = Locksmith.instance.encryptionManager.decryptString(
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

            LocksmithException.Type.InvalidData     -> {
                showSnackbar(R.string.errorInvalidData)
            }
            LocksmithException.Type.EncryptionError -> {
                showSnackbar(R.string.errorGeneric)
            }
            LocksmithException.Type.Initiation,
            LocksmithException.Type.Uninitiated,
            LocksmithException.Type.Unauthenticated,
            LocksmithException.Type.Generic         -> {
                Log.e(tag, "Generic", e)
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
