package natalia.doskach.readingbuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {
    EditText emailET, passwordET;
    String email, password;
    FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        Data.mAuth = FirebaseAuth.getInstance();
        currentUser = Data.mAuth.getCurrentUser();
        if (currentUser != null)
            startActivity(new Intent(this, HomeActivity.class));
        else {
            emailET = findViewById(R.id.email);
            passwordET = findViewById(R.id.password);
        }

    }


    public void forgotPassword(View view) {
        startActivity(new Intent(this, ForgotPasswordActivity.class));
    }

    public void signInOrRegister(View view) {
        email = emailET.getText().toString();
        password = passwordET.getText().toString();
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(MainActivity.this, "wrong value!", Toast.LENGTH_LONG).show();
            return;
        }
        Data.mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    startHome();
                } else {
                    try {
                        String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                        switch (errorCode) {
                            case "ERROR_WRONG_PASSWORD":
                                Toast.makeText(MainActivity.this, "wrong password!", Toast.LENGTH_LONG).show();
                                passwordET.setText("");
                                break;
                            case "ERROR_INVALID_EMAIL":
                                Toast.makeText(MainActivity.this, "wrong email!", Toast.LENGTH_LONG).show();
                                emailET.setText("");
                                break;
                            case "ERROR_USER_NOT_FOUND":
                                Toast.makeText(MainActivity.this, "email not found! register instead", Toast.LENGTH_LONG).show();
                                startRegistration();
                        }
                    } catch (Exception e){
                        Toast.makeText(MainActivity.this, "no connection!", Toast.LENGTH_LONG).show();
                    }

                }
            }
        });

    }

    private void startHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    public void startRegistration() {
        Intent intent = new Intent(this, RegistrationActivity.class);
        intent.putExtra("password", password);
        intent.putExtra("email", email);
        startActivity(intent);
    }

    public void register(View view) {
        startRegistration();
    }
}