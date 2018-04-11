package dk.nodes.locksmith.example

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import dk.nodes.locksmith.Locksmith
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
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
        mainBtnExit.setOnClickListener {
            Log.d(TAG, "mainBtnExit Clicked")
        }

        mainBtnEncrypt.setOnClickListener {
            encryptData()
        }

        mainBtnDecrypt.setOnClickListener {
            decryptData()
        }
    }


    private fun encryptData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                currentData = Locksmith.getInstance().fingerprintCryptManager.encryptString(
                        currentData
                )
                updateTextView()
            } catch (e: Exception) {
                Log.e(TAG, "encryptData: ERROR")
                e.printStackTrace()
            }
        }
    }

    private fun decryptData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            try {
                currentData = Locksmith.getInstance().fingerprintCryptManager.decryptString(
                        currentData
                )
                updateTextView()
            } catch (e: Exception) {
                Log.e(TAG, "decryptData: ERROR")
                e.printStackTrace()
            }
        }
    }

    private fun updateTextView() {
        mainTvDecryptedData.text = currentData
    }

    private fun startLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}
