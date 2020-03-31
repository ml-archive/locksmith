package dk.nodes.locksmith.example.dialogs

import android.content.Context
import android.os.Build
import android.os.Handler
import androidx.annotation.RequiresApi
import androidx.annotation.StyleRes
import android.view.View
import android.widget.Button
import android.widget.TextView
import dk.nodes.locksmith.core.fingerprint.FingerprintAlertDialogBase
import dk.nodes.locksmith.core.models.OnFingerprintDialogEventListener
import dk.nodes.locksmith.example.R

@RequiresApi(Build.VERSION_CODES.M)
class CustomFingerprintDialog(context: Context) : FingerprintAlertDialogBase(context) {
    // Handler
    private val handler = Handler()
    lateinit var tvTitle: TextView
    lateinit var tvSubtitle: TextView
    lateinit var tvDescription: TextView
    lateinit var tvMessage: TextView
    //    lateinit var ivFingerprint: ImageView
    lateinit var btnUsePassword: Button
    lateinit var btnCancel: Button

    var onUsePasswordBtnListener: (() -> Unit)? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setupViews()
    }


    private fun setupViews() {
        val window = window ?: return
        tvTitle = window.findViewById(R.id.dialogCustomFingerprintTvTitle)
        tvDescription = window.findViewById(R.id.dialogCustomFingerprintTvDescription)
        tvSubtitle = window.findViewById(R.id.dialogCustomFingerprintTvSubtitle)
        tvMessage = window.findViewById(R.id.dialogCustomFingerprintTvMessage)
        btnUsePassword = window.findViewById(R.id.dialogCustomFingerprintBtnUsePassword)
        btnCancel = window.findViewById(R.id.dialogCustomFingerprintBtnCancel)

        tvTitle.text = context.getString(R.string.fingerprintCustomDialogTitle)
        tvDescription.text = context.getString(R.string.fingerprintCustomDialogDescription)
        tvSubtitle.text = context.getString(R.string.fingerprintCustomDialogSubtitle)
        tvMessage.text = context.getString(R.string.fingerprintCustomDialogDefaultMessage)
        btnUsePassword.text = context.getString(R.string.usePassword)
        btnCancel.text = context.getString(R.string.cancel)

        btnCancel.setOnClickListener {
            onCancelClicked()
        }

        btnUsePassword.setOnClickListener {
            onUsePasswordBtnListener?.invoke()
        }

    }

    override fun getDialogLayout(): Int {
        return R.layout.dialog_custom_fingerprint
    }

    override fun onFingerprintHelp(help: String) {
        setTvMessageWithStyle(help, dk.nodes.locksmith.R.style.FingerprintDialogWarn)
    }

    override fun onFingerprintError() {
        setTvMessageWithStyle(
                context.getString(R.string.fingerprintErrorMessage),
                dk.nodes.locksmith.R.style.FingerprintDialogError
        )

        btnCancel.isEnabled = false
        btnUsePassword.isEnabled = false

        handler.postDelayed({ closeDialog() }, 1000)
    }

    override fun onFingerprintSuccess() {
        setTvMessageWithStyle(
                context.getString(R.string.fingerprintSuccessMessage),
                dk.nodes.locksmith.R.style.FingerprintDialogSuccess
        )

        btnCancel.isEnabled = false
        btnUsePassword.isEnabled = false

        handler.postDelayed({ closeDialog() }, 1000)
    }

    private fun setTvMessageWithStyle(message: String, @StyleRes styleRes: Int) {
        tvMessage.visibility = View.VISIBLE
        tvMessage.text = message
        tvMessage.setTextAppearance(styleRes)
    }

    fun setOnFingerprintDialogEventListener(listener: OnFingerprintDialogEventListener) {
        this.onFingerprintDialogEventListener = listener
    }
}