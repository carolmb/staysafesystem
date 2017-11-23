package com.example.ana.staysafesystem.gui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ana.staysafesystem.R;
import com.example.ana.staysafesystem.processor.Processor;

import java.util.ArrayList;

public class ProtectedUserActivity extends AppCompatActivity {

    class FuncButton {
        TextView description;
        ImageButton img;
    }
    public  static final int RequestPermissionCode = 1;
    ArrayList<FuncButton> funcButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protected_user);
        setTitle("3S - Protegido");

        enableRuntimePermission();
        setFuncButtons();

        String func1 = Processor.getInstance().getButtonFunc(this, 1);
        configButton(func1, 1);

        String func2 = Processor.getInstance().getButtonFunc(this, 2);
        configButton(func2, 2);

        saveMeButtons();
    }

    private void setFuncButtons() {
        funcButtons = new ArrayList<>();

        FuncButton funcButton1 = new FuncButton();
        funcButton1.description = findViewById(R.id.func1);
        funcButton1.img = findViewById(R.id.imageButton1);
        funcButtons.add(funcButton1);

        FuncButton funcButton2 = new FuncButton();
        funcButton2.description = findViewById(R.id.func2);
        funcButton2.img = findViewById(R.id.imageButton2);
        funcButtons.add(funcButton2);
    }

    void configButton(String func, int id) {
        if(func != null) {
            showFunc(func, id);
        } else {
            nullFunc(id);
        }
    }

    void nullFunc(final int id) {
        TextView description = funcButtons.get(id-1).description;
        description.setText("Não configurado");

        ImageButton imageButton = funcButtons.get(id-1).img;
        imageButton.setImageResource(android.R.drawable.ic_input_add);
        imageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), AddFuncActivity.class);
                intent.putExtra("buttonPressed", id);
                view.getContext().startActivity(intent);
            }
        });
    }

    void showFunc(String func, final int id) {
        TextView description = funcButtons.get(id-1).description;
        description.setText(getPrettyName(func));

        ImageButton imageButton = funcButtons.get(id-1).img;
        imageButton.setImageResource(android.R.drawable.ic_menu_delete);
        imageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Processor.getInstance().setButtonFunc(view.getContext(), id, null);
                nullFunc(id);
            }
        });
    }

    private String getPrettyName(String funcCode) {
        String prettyName = null;
        if(funcCode.contentEquals("call")) {
            prettyName = "Ligar para contato especial";
        } else if(funcCode.contentEquals("track")) {
            prettyName = "Ratrear aparelho";
        } else if(funcCode.contentEquals("msg")) {
            prettyName = "Mensagem para amigos";
        }
        return prettyName;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menuAbout:
                UtilGUI.dialog(this,
                        "Stay Safe System foi pensado com carinho para te ajudar.");
                break;

            case R.id.menuProfile:
                UtilGUI.changeScreen(this, ProfileActivity.class);
                break;

            case R.id.menuMode:
                // clean mode
                Processor.getInstance().clearMode(this);
                UtilGUI.changeScreen(this, UserModeActivity.class);
                break;

            case R.id.menuLogout:
                // logout + clean mode
                Processor.getInstance().logout(this);
                UtilGUI.changeScreen(this, LoginActivity.class);
                break;

        }
        return true;
    }

    void saveMeButtons() {
        ImageButton helpMe1 = findViewById(R.id.helpMe);
        helpMe1.setOnClickListener( new View.OnClickListener() {
            public void onClick(View view) {
                UtilGUI.dialog(view.getContext(), "Deu certo 1.");
                Processor.getInstance().buttonPressed(1);
            }
        });

        ImageButton helpMe2 = findViewById(R.id.helpMe11);
        helpMe2.setOnClickListener( new View.OnClickListener() {
            public void onClick(View view) {
                UtilGUI.dialog(view.getContext(), "Deu certo 2.");
                Processor.getInstance().buttonPressed(2);
            }
        });
    }

    public void enableRuntimePermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.INTERNET)) {
            Toast.makeText(this,"Essa permissão é para usarmos a internet.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.INTERNET}, RequestPermissionCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {
        switch (RC) {
            case RequestPermissionCode:
                if (!(PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED)) {
                    UtilGUI.dialog(this, "Esse aplicativo não tem permissão para " +
                            "acessar seus contatos.");
                }
                break;
        }
    }

}
