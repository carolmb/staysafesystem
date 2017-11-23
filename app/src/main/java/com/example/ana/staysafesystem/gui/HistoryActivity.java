package com.example.ana.staysafesystem.gui;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.ana.staysafesystem.R;
import com.example.ana.staysafesystem.data.Call;
import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {
    ListView listView;
    ArrayAdapter<Call> arrayAdapter;
    static ArrayList<Call> history;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Chamados antigos");

        initFakeCalls();

        setContentView(R.layout.activity_history);
        listView = findViewById(R.id.history);
        arrayAdapter = new ArrayAdapter<>(
                this,
                R.layout.contact_list_item,
                history);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent,
                                    final View view, final int position, long id) {
                String content = history.get(position).longToString();
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage(content);
                builder.setCancelable(true);
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                history.get(position).setFinished();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                history.remove(position);
                                arrayAdapter.notifyDataSetChanged();
                                break;
                        }
                    }
                };

                builder.setPositiveButton("Marcar como resolvido", dialogClickListener);
                builder.setNegativeButton("Apagar", dialogClickListener);
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        listView.setAdapter(arrayAdapter);
    }

    private void initFakeCalls() {
        history = new ArrayList<>();
        for(int i = 0; i < 5; i++) {
            Call fakeCall = new Call("12/12/12", "12h30", "Júlia pediu ajuda", "Não foi resolvido");
            history.add(fakeCall);
        }
    }
}
