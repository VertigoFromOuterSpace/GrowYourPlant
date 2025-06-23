//Trabalho feito por: Henrique Lopes, Thiago Vieira, Luiz Raeder

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
    public static final String PREFS_NAME = "TarefasPrefs";
    public static final String PROGRESS_KEY = "progresso";
    private ProgressBar progressBarTarefas;
    private TextView txtProgressTarefas;
    private TextView contadorPassosTxt;
    private TextView distanciaTxt;
    private ProgressBar barraDeProgressoPassos;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private SensorEventListener sensorListener;
    private int contadorPassos = 0;
    private float previousMagnitude = 0f;
    private double distanciaPercorrida = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnIrParaTarefas = findViewById(R.id.btnIrParaTarefas);

        progressBarTarefas = findViewById(R.id.progressBar);
        txtProgressTarefas = findViewById(R.id.txtProgress);
        contadorPassosTxt = findViewById(R.id.textViewContador);
        distanciaTxt = findViewById(R.id.textViewDistancia);

        if (barraDeProgressoPassos != null) barraDeProgressoPassos.setMax(10_000);
        btnIrParaTarefas.setOnClickListener(v -> startActivity(new Intent(this, ListaTarefas.class)));

        atualizarBarraProgressoTarefas();
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

                if (delta > 6) {
                    contadorPassos++;
                    distanciaPercorrida = contadorPassos * 0.76;
                    atualizarPedometerUI();
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };
    }

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


    @Override
    protected void onResume() {
        super.onResume();
        atualizarBarraProgressoTarefas();
        if (accelerometer != null) {
            sensorManager.registerListener(sensorListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorListener);
    }
}
