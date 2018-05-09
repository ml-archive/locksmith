package dk.nodes.locksmith.core.fingerprint;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.annotation.StyleRes;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import dk.nodes.locksmith.R;

@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerprintDialog extends FingerprintDialogBase {
    public static final String TAG = FingerprintDialog.class.getSimpleName();

    // Views
    private LinearLayout mainContainer;
    protected TextView tvTitle;
    protected TextView tvSubtitle;
    protected TextView tvDescription;
    protected TextView tvMessage;
    protected ImageView ivFingerprint;
    protected Button btnCancel;
    // Values
    protected String titleText;
    protected String subtitleText;
    protected String descriptionText;
    protected String cancelButtonText;
    protected String successMessageText;
    protected String errorMessageText;
    // Handler
    private Handler handler = new Handler();

    public FingerprintDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        setupViews();
        startShowAnimation();
    }

    @Override
    public int getDialogLayout() {
        return R.layout.dialog_fingerprint;
    }

    public void setupViews() {
        if (getWindow() == null) {
            dismiss();
            return;
        }

        mainContainer = getWindow().findViewById(R.id.dialogFragmentMainContainer);

        tvTitle = getWindow().findViewById(R.id.dialogFragmentTvTitle);
        tvSubtitle = getWindow().findViewById(R.id.dialogFragmentTvSubtitle);
        tvDescription = getWindow().findViewById(R.id.dialogFragmentTvDescription);
        tvMessage = getWindow().findViewById(R.id.dialogFragmentTvMessage);

        ivFingerprint = getWindow().findViewById(R.id.dialogFragmentIvFingerprint);

        btnCancel = getWindow().findViewById(R.id.dialogFragmentBtnCancel);

        if (cancelButtonText != null) {
            btnCancel.setText(cancelButtonText);
        }

        if (titleText != null) {
            tvTitle.setText(titleText);
        }

        if (subtitleText != null) {
            tvSubtitle.setText(subtitleText);
        }

        if (descriptionText != null) {
            tvDescription.setText(descriptionText);
        }

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancelClicked();
            }
        });
    }

    @Override
    protected void closeDialog() {
        startCloseAnimation();
        cancelSignal();
    }

    // Helpers

    private void setTvMessageWithStyle(String message, @StyleRes int styleRes) {
        tvMessage.setVisibility(View.VISIBLE);
        tvMessage.setText(message);
        tvMessage.setTextAppearance(styleRes);
    }

    private void setFingerprintBackgroundTint(@ColorRes int colorRes) {
        Log.d(TAG, "setFingerprintBackgroundTint: " + colorRes);

        Context context = getContext();

        int color = context.getResources().getColor(colorRes, null);
        ColorStateList stateList = ColorStateList.valueOf(color);
        ivFingerprint.setBackgroundTintList(stateList);
    }

    // Animation Stuff

    private void startShowAnimation() {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up);
        mainContainer.startAnimation(animation);
    }

    private void startCloseAnimation() {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_down);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                onCloseAnimationFinished();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mainContainer.startAnimation(animation);
    }

    private void onCloseAnimationFinished() {
        dismiss();
    }

    // Fingerprint Events

    @Override
    public void onFingerprintSuccess() {
        setFingerprintBackgroundTint(R.color.fingerprint_success);

        if (successMessageText != null) {
            setTvMessageWithStyle(successMessageText, R.style.FingerprintDialogSuccess);
            btnCancel.setEnabled(false);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    closeDialog();
                }
            }, 1000);
        } else {
            closeDialog();
        }
    }

    @Override
    public void onFingerprintHelp(String help) {
        Log.d(TAG, "onFingerprintHelp: " + help);

        setFingerprintBackgroundTint(R.color.fingerprint_help);

        setTvMessageWithStyle(help, R.style.FingerprintDialogWarn);
    }

    @Override
    public void onFingerprintError() {
        setFingerprintBackgroundTint(R.color.fingerprint_error);

        if (errorMessageText != null) {
            setTvMessageWithStyle(errorMessageText, R.style.FingerprintDialogError);
            btnCancel.setEnabled(false);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    closeDialog();
                }
            }, 1000);
        } else {
            closeDialog();
        }
    }

}
