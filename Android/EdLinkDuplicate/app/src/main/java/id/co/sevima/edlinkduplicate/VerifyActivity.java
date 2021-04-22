package id.co.sevima.edlinkduplicate;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class VerifyActivity extends BaseActivity {
    TextView text1View;
    EditText verificationCodeField;
    String email, verificationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        text1View = findViewById(R.id.text1);
        verificationCodeField = findViewById(R.id.verification_code);
        email = getIntent().getStringExtra("email");
        verificationCode = getIntent().getStringExtra("verification_code");
        text1View.setText(getResources().getString(R.string.text20)+" "+email+" "+getResources().getString(R.string.text21));
    }

    public void next(View view) {
        String enteredVerificationCode = verificationCodeField.getText().toString().trim();
        if (enteredVerificationCode.equals("")) {
            show(R.string.text22);
            return;
        }
        if (!enteredVerificationCode.toLowerCase().equals(verificationCode.toLowerCase().trim())) {
            showError(getResources().getString(R.string.text23));
            return;
        }
        final ProgressDialog dialog = createDialog(R.string.text24);
        dialog.show();
        post(new Listener() {

            @Override
            public void onResponse(String str) {
                dialog.dismiss();
                try {
                    IntroActivity.instance.finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                startActivity(new Intent(VerifyActivity.this, HomeActivity.class));
                finish();
            }
        }, Constants.API_URL+"/user/verify_email", "email", email);
    }
}