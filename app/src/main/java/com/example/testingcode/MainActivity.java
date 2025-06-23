package com.example.testingcode;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // --------------------- Constantes --------------------- //
    public static final String PREFS_NAME = "TarefasPrefs"; // SharedPreferences para as tarefas
    public static final String PROGRESS_KEY = "progresso";   // Chave da % de tarefas concluídas

    // --------------------- UI – Lista de Tarefas --------------------- //
    private ProgressBar progressBarTarefas;  // Barra que mostra % das tarefas concluídas
    private TextView txtProgressTarefas;     // Texto que mostra a %

    // --------------------- UI – Pedômetro --------------------- //
    private TextView contadorPassosTxt;      // Exibe a quantidade de passos
    private TextView distanciaTxt;           // Exibe a distância percorrida
    private ProgressBar barraDeProgressoPassos; // Mostra o total de passos em relação à meta

    // --------------------- Sensor --------------------- //
    private SensorManager sensorManager;     // Gerenciador de sensores
    private Sensor accelerometer;            // Acelerômetro do dispositivo
    private SensorEventListener sensorListener; // Ouvinte que detecta alterações no acelerômetro

    // --------------------- Variáveis de Negócio --------------------- //
    private int contadorPassos = 0;          // Total de passos
    private float previousMagnitude = 0f;    // Última magnitude registrada
    private double distanciaPercorrida = 0;  // Distância estimada (m)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ---------- Liga componentes da UI ---------- //
        Button btnIrParaTarefas = findViewById(R.id.btnIrParaTarefas);

        progressBarTarefas = findViewById(R.id.progressBar); // Já existe no layout
        txtProgressTarefas = findViewById(R.id.txtProgress);

        // Esses elementos precisam ser adicionados no layout XML (veja instruções no chat)
        contadorPassosTxt = findViewById(R.id.textViewContador);
        distanciaTxt = findViewById(R.id.textViewDistancia);
        if (barraDeProgressoPassos != null) barraDeProgressoPassos.setMax(10_000); // meta 10k passos

        // ---------- Navegação ---------- //
        btnIrParaTarefas.setOnClickListener(v -> startActivity(new Intent(this, ListaTarefas.class)));

        // ---------- Inicializa progressão de tarefas ---------- //
        atualizarBarraProgressoTarefas();

        // ---------- Configura Sensor ---------- //
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                float magnitude = (float) Math.sqrt(x * x + y * y + z * z);
                float delta = magnitude - previousMagnitude;
                previousMagnitude = magnitude;

                // Se o "pico" for maior que 6 consideramos um passo
                if (delta > 6) {
                    contadorPassos++;
                    distanciaPercorrida = contadorPassos * 0.76; // 0,76 m ≈ passo médio
                    atualizarPedometerUI();
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // Não precisamos tratar mudanças de acurácia aqui
            }
        };
    }

    // --------------------- Métodos Auxiliares --------------------- //
    private void atualizarPedometerUI() {
        if (contadorPassosTxt != null) {
            contadorPassosTxt.setText("Passos: " + contadorPassos);
        }
        if (distanciaTxt != null) {
            distanciaTxt.setText(String.format("Distância: %.2f m", distanciaPercorrida));
        }
        if (barraDeProgressoPassos != null) {
            barraDeProgressoPassos.setProgress(contadorPassos);
        }
    }

    private void atualizarBarraProgressoTarefas() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int progresso = prefs.getInt(PROGRESS_KEY, 0);

        progressBarTarefas.setProgress(progresso);
        txtProgressTarefas.setText(progresso + "% concluído");
    }

    // --------------------- Ciclo de Vida --------------------- //
    @Override
    protected void onResume() {
        super.onResume();
        atualizarBarraProgressoTarefas(); // Garante que a % das tarefas está atualizada
        if (accelerometer != null) {
            sensorManager.registerListener(sensorListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorListener); // Economiza bateria quando a activity não está visível
    }
}
