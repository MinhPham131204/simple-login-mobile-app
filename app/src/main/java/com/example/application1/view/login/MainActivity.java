package com.example.application1.view.login;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.application1.checkEmulatorFactory.checkEmulator.CheckEmulator;
import com.example.application1.checkEmulatorFactory.checkEmulatorCreator.CheckCreator;
import com.example.application1.checkEmulatorFactory.checkEmulatorCreator.DeviceCheckCreator;
import com.example.application1.checkEmulatorFactory.checkEmulatorCreator.FingerprintCheckCreator;
import com.example.application1.checkEmulatorFactory.checkEmulatorCreator.HardwareCheckCreator;
import com.example.application1.checkEmulatorFactory.checkEmulatorCreator.ModelCheckCreator;
import com.example.application1.checkEmulatorFactory.checkEmulatorCreator.ProductCheckCreator;
import com.example.application1.util.factory.ClientFactory;
import com.example.application1.view.home.HomeActivity;
import com.example.application1.R;
import com.example.application1.presenter.login.LoginPresenter;
import com.example.application1.presenter.login.LoginPresenterImpl;

public class MainActivity extends AppCompatActivity implements LoginView{

    private EditText emailEditText, passwordEditText;

    private LoginPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // check app is running on emulator (or not)
        ClientFactory.checkAllCreator();

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        presenter = new LoginPresenterImpl(this);

        Button loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString();

            presenter.validateUser(email, password);
        });
    }

    @Override
    public void setLoginSuccess(String username) {
        Intent intent = new Intent(this, HomeActivity.class);

        // ensure only 1 activity in back stack
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.putExtra("username", username);

        startActivity(intent);
    }

    @Override
    public void setLoginFailed(String message) {
        Toast.makeText(MainActivity.this, "Login failed: " + message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setLoginError(String errorMessage) {
        Toast.makeText(MainActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
    }
}