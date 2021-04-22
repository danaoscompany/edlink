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

public class SignupActivity extends BaseActivity {
    EditText emailField, fullNameField, passwordField, repeatedPasswordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        setTitle("");
        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        emailField = findViewById(R.id.email);
        fullNameField = findViewById(R.id.full_name);
        passwordField = findViewById(R.id.password);
        repeatedPasswordField = findViewById(R.id.repeated_password);
        passwordField.setTransformationMethod(new PasswordTransformationMethod());
        repeatedPasswordField.setTransformationMethod(new PasswordTransformationMethod());
    }

    public void hideEmail(View view) {
        if (emailField.getTransformationMethod() == null) {
            emailField.setTransformationMethod(new PasswordTransformationMethod());
        } else {
            emailField.setTransformationMethod(null);
        }
    }

    public void hideName(View view) {
        if (fullNameField.getTransformationMethod() == null) {
            fullNameField.setTransformationMethod(new PasswordTransformationMethod());
        } else {
            fullNameField.setTransformationMethod(null);
        }
    }

    public void showPassword(View view) {
        if (passwordField.getTransformationMethod() == null) {
            passwordField.setTransformationMethod(new PasswordTransformationMethod());
        } else {
            passwordField.setTransformationMethod(null);
        }
    }

    public void showRepeatedPassword(View view) {
        if (repeatedPasswordField.getTransformationMethod() == null) {
            repeatedPasswordField.setTransformationMethod(new PasswordTransformationMethod());
        } else {
            repeatedPasswordField.setTransformationMethod(null);
        }
    }

    public void signup(View view) {
        final String email = emailField.getText().toString().trim();
        final String fullName = fullNameField.getText().toString().trim();
        final String password = passwordField.getText().toString();
        final String repeatedPassword = repeatedPasswordField.getText().toString();
        if (email.equals("") || fullName.equals("") || password.trim().equals("") || repeatedPassword.trim().equals("")) {
            show(R.string.please_complete_data);
            return;
        }
        final ProgressDialog dialog = createDialog(R.string.loading);
        dialog.show();
        post(new Listener() {

            @Override
            public void onResponse(String response) {
                try {
                    dialog.dismiss();
                    final JSONObject obj = new JSONObject(response);
                    int responseCode = getInt(obj, "response_code", 0);
                    if (responseCode == 1) {
                        Constants.USER_ID = getInt(obj, "id", 0);
                        writeEncrypted("email", email);
                        writeEncrypted("password", password);
                        AlertDialog dialog = new AlertDialog.Builder(SignupActivity.this)
                                .setMessage(getResources().getString(R.string.text16)+email+getResources().getString(R.string.text17))
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        startActivity(new Intent(SignupActivity.this, VerifyActivity.class)
                                                .putExtra("email", Util.getString(obj, "email", ""))
                                                .putExtra("verification_code", Util.getString(obj, "verification_code", "")));
                                    }
                                })
                                .setNegativeButton(R.string.back, null)
                                .create();
                        dialog.show();
                    } else if (responseCode == -1) {
                        showError(getResources().getString(R.string.text18));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, Constants.API_URL+"/user/signup", "email", email, "password", password, "name", fullName);
    }

    public void login(View view) {
        startActivity(new Intent(this, LoginActivity.class));
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