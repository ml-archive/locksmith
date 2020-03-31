package dk.nodes.locksmith.example.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import dk.nodes.locksmith.BiometricCryptManager
import dk.nodes.locksmith.BiometricEncryptionManager
import dk.nodes.locksmith.core.encryption.handlers.EncryptionHandlerImpl
import dk.nodes.locksmith.core.encryption.providers.AndroidKeyProviderImpl
import dk.nodes.locksmith.core.encryption.providers.KeyProvider
import dk.nodes.locksmith.core.exceptions.LocksmithException
import dk.nodes.locksmith.example.R
import kotlinx.android.synthetic.main.fragment_encryption.*
import java.util.concurrent.Executor
import javax.crypto.Cipher

class BiometricEncryptionFragment : Fragment(R.layout.fragment_encryption) {

    private val defaultData = "Mary had a little lamb\n" +
        "It's fleece was white as snow, yeah\n" +
        "Everywhere the child went\n" +
        "The lamb, the lamb was sure to go, yeah"

    private var encryptedData: String = ""

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private var keyProvider: KeyProvider? = null
    private var biometricManager: BiometricEncryptionManager? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        encryptedData = defaultData
        fragmentEtData.setText(defaultData)
        setupBiometric()
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

    private fun setupBiometric() {
        executor = ContextCompat.getMainExecutor(requireContext())
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    biometricManager = BiometricEncryptionManager(
                        EncryptionHandlerImpl(
                            object : AndroidKeyProviderImpl(true) {
                                override fun getCipher(): Cipher {
                                    return result.cryptoObject?.cipher!!
                                }
                            })
                    )
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Use account password")
            .build()
    }

    private fun decryptData() {
        if (biometricManager == null) {
            biometricPrompt.authenticate(promptInfo)
        } else {
            try {
                encryptedData = biometricManager!!.decryptString(encryptedData)
                updateTextView()
            } catch (e: LocksmithException) {
                handleException(e)
            }
        }
    }

    private fun updateTextView() {
        fragmentTvData.text = encryptedData
    }

    private fun encryptData() {
        if (biometricManager == null) {
            val b = BiometricCryptManager()
            biometricPrompt.authenticate(
                promptInfo,
                b.getCryptoObject()
            )
        } else {
            try {
                encryptedData = biometricManager!!.encryptString(encryptedData)
                updateTextView()
            } catch (e: LocksmithException) {
                handleException(e)
            }
        }
    }

    private fun handleException(e: LocksmithException) {
        Log.e(tag, "handleException")
        e.printStackTrace()

        when (e.type) {
            LocksmithException.Type.Initiation,
            LocksmithException.Type.Uninitiated -> {
                Log.e(tag, "Uninitiated")
            }
            LocksmithException.Type.Unauthenticated -> {
                Log.e(tag, "Unauthenticated")
            }
            LocksmithException.Type.InvalidData -> {
            }
            LocksmithException.Type.EncryptionError -> {
                Log.e(tag, "Encryption error")
            }
            LocksmithException.Type.Generic -> {
                Log.e(tag, "Generic", e)
            }
        }
    }

    private val KEY_NAME = "LockSmithBiometricEncryptionKey"
}