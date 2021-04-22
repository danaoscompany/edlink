package id.co.sevima.edlinkduplicate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import org.json.JSONObject;

public class LoginActivity extends BaseActivity {
    EditText emailField, passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("");
        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password);
        passwordField.setTransformationMethod(new PasswordTransformationMethod());
    }

    public void showPassword(View view) {
        if (passwordField.getTransformationMethod() == null) {
            passwordField.setTransformationMethod(new PasswordTransformationMethod());
        } else {
            passwordField.setTransformationMethod(null);
        }
    }

    public void login(View view) {
        final String email = emailField.getText().toString().trim();
        final String password = passwordField.getText().toString();
        if (email.equals("") || password.trim().equals("")) {
            show(R.string.please_complete_data);
            return;
        }
        final ProgressDialog dialog = createDialog(R.string.loading);
        dialog.show();
        post(new Listener() {

            @Override
            public void onResponse(String response) {
                try {
                    final JSONObject obj = new JSONObject(response);
                    int responseCode = getInt(obj, "response_code", 0);
                    if (responseCode == 1) {
                        Constants.USER_ID = getInt(obj, "id", 0);
                        writeEncrypted("email", email);
                        writeEncrypted("password", password);
                        int emailVerified = getInt(obj, "email_verified", 0);
                        if (emailVerified == 0) {
                            post(new Listener() {

                                @Override
                                public void onResponse(String response) {
                                    dialog.dismiss();
                                    try {
                                        JSONObject obj = new JSONObject(response);
                                        String verificationCode = getString(obj, "verification_code", "");
                                        startActivity(new Intent(LoginActivity.this, VerifyActivity.class)
                                                .putExtra("email", email)
                                                .putExtra("verification_code", verificationCode));
                                        finish();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, Constants.API_URL+"/user/send_verification_code", "email", email);
                        } else {
                            dialog.dismiss();
                            try {
                                IntroActivity.instance.finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            finish();
                        }
                    } else if (responseCode == -1) {
                        showError(getResources().getString(R.string.text18));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, Constants.API_URL+"/user/login", "email", email, "password", password);
    }

    public void signup(View view) {
        startActivity(new Intent(this, SignupActivity.class));
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return false;
    }
}