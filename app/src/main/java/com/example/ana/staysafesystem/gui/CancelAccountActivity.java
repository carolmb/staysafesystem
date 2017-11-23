package com.example.ana.staysafesystem.gui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ana.staysafesystem.R;
import com.example.ana.staysafesystem.processor.Processor;

public class CancelAccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_account);
        setTitle("Cancelar conta");
        TextView cancelDescription = findViewById(R.id.cancelDescription);
        cancelDescription.setText("Nós, que desenvolvemos o aplicativo com carinho e " +
                "dedicação lamentamos que você deseje cancelar sua conta." +
                "\nSeus dados serão perdidos. Tem certeza que deseja cancelar sua conta?");
        Button cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Processor.getInstance().logout(view.getContext());
                UtilGUI.changeScreen(view.getContext(), LoginActivity.class);
            }
        });
        Button persist = findViewById(R.id.persist);
        persist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UtilGUI.changeScreen(view.getContext(), UserModeActivity.class);
            }
        });

    }
}
