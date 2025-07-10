package com.example.application1;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Button logoutButton = findViewById(R.id.button);

        TextView textView = findViewById(R.id.textView);
        String username = getIntent().getStringExtra("username");
        textView.setText("Welcome, " + username);

        OnBackPressedCallback newCallback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                new AlertDialog.Builder(HomeActivity.this)
                        .setTitle("Exit app")
                        .setMessage("Bạn có muốn rời khỏi ứng dụng này")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Disable this object/callback (newCallback)
                                setEnabled(false);

                                // Invoke Back Action => system find default OnBackPressedCallback instance => Invoke handleOnBackPressed => Back to system
                                HomeActivity.super.getOnBackPressedDispatcher().onBackPressed();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, newCallback);

        logoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);

            // ensure only 1 activity in the back stack
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);

            // reverse navigation
            overridePendingTransition(R.anim.enter, R.anim.exit);
        });
    }
}