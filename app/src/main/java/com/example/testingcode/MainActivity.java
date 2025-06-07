package com.example.testingcode;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    // Chaves pra salvar no celular (igual um armário)
    public static final String PREFS_NAME = "TarefasPrefs"; // Nome do armário
    public static final String PROGRESS_KEY = "progresso"; // Gaveta da %

    private ProgressBar progressBar; // Barra de progresso
    private TextView txtProgress; // Texto que mostra a %

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Pega o layout

        // Liga os componentes da tela
        progressBar = findViewById(R.id.progressBar);
        txtProgress = findViewById(R.id.txtProgress);
        Button btnIrParaTarefas = findViewById(R.id.btnIrParaTarefas);

        // Botão que leva pra tela de tarefas
        btnIrParaTarefas.setOnClickListener(v -> {
            startActivity(new Intent(this, ListaTarefas.class));
        });

        atualizarBarraProgresso(); // Atualiza a barra quando abre o app
    }

    @Override
    protected void onResume() {
        super.onResume();
        atualizarBarraProgresso(); // Atualiza quando volta pra essa tela
    }

    // Atualiza a barra de progresso e o texto
    private void atualizarBarraProgresso() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int progresso = prefs.getInt(PROGRESS_KEY, 0); // Pega a % salva

        progressBar.setProgress(progresso); // Atualiza a barra
        txtProgress.setText(progresso + "% concluído"); // Atualiza o texto
    }
}