package com.example.application1.view.login;

import android.content.Intent;
import android.os.Bundle;

import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.application1.util.factory.ClientFactory;
import com.example.application1.view.home.HomeActivity;
import com.example.application1.R;
import com.example.application1.viewModel.login.LoginViewModel;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.application1.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        String checkDevice = ClientFactory.checkAllCreator();
        showMessage(checkDevice);

        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this); // QUAN TRỌNG để data binding hoạt động với LiveData

        // Observe message
        viewModel.getMessage().observe(this, msg -> {
            if (msg != null && !msg.isEmpty()) {
                showMessage(msg);
            }
        });

        // Observe login success
        viewModel.getLoginSuccess().observe(this, username -> {
            if (username != null && !username.isEmpty()) {
                loginSuccessful(username);
            }
        });
    }

    public void loginSuccessful(String username) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}