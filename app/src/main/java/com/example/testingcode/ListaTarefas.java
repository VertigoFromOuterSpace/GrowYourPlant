package com.example.testingcode;

import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.List;

public class ListaTarefas extends AppCompatActivity {
    private static final String TASKS_KEY = "tasks";
    private static final String STATUS_KEY = "status";
    private List<String> tasks;
    private List<Boolean> taskStatus;
    private TaskAdapter adapter;
    private ListView listViewTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_tarefas);

        listViewTasks = findViewById(R.id.listViewTasks);
        tasks = new ArrayList<>();
        taskStatus = new ArrayList<>();

        carregarTarefasSalvas();

        adapter = new TaskAdapter(this, tasks, taskStatus, this);
        listViewTasks.setAdapter(adapter);

        findViewById(R.id.btnVoltar).setOnClickListener(v -> finish());

        findViewById(R.id.buttonAdd).setOnClickListener(v -> {
            String task = ((android.widget.EditText) findViewById(R.id.editTextTask)).getText().toString().trim();
            if(!task.isEmpty()){
                tasks.add(task);
                taskStatus.add(false);
                adapter.notifyDataSetChanged();
                ((android.widget.EditText) findViewById(R.id.editTextTask)).setText("");
                salvarTarefas();
                atualizarProgresso();
            }
        });
    }

    private void carregarTarefasSalvas() {
        SharedPreferences prefs = getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
        String tasksJson = prefs.getString(TASKS_KEY, "[]");
        String statusJson = prefs.getString(STATUS_KEY, "[]");

        try {
            JSONArray tasksArray = new JSONArray(tasksJson);
            JSONArray statusArray = new JSONArray(statusJson);

            tasks.clear();
            taskStatus.clear();

            for (int i = 0; i < tasksArray.length(); i++) {
                tasks.add(tasksArray.getString(i));
            }

            for (int i = 0; i < statusArray.length(); i++) {
                taskStatus.add(statusArray.getBoolean(i));
            }
        } catch (JSONException e) {
            Log.e("ListaTarefas", "Erro ao carregar tarefas", e);
        }
    }

    public void removerTarefa(int position) {
        if (position >= 0 && position < tasks.size()) {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Confirmar exclusão")
                    .setMessage("Tem certeza que deseja excluir esta tarefa?")
                    .setPositiveButton("Sim", (dialog, which) -> {
                        tasks.remove(position);
                        taskStatus.remove(position);
                        adapter.notifyDataSetChanged();
                        salvarTarefas();
                        atualizarProgresso();
                    })
                    .setNegativeButton("Não", null)
                    .show();
        }
    }

    public void atualizarStatusTarefa(int position, boolean isChecked) {
        if (position >= 0 && position < taskStatus.size()) {
            taskStatus.set(position, isChecked);
            salvarTarefas();
            atualizarProgresso();
        }
    }

    private void atualizarProgresso() {
        int concluidas = 0;
        for (Boolean status : taskStatus) {
            if (status) concluidas++;
        }

        int progresso = tasks.isEmpty() ? 0 : (concluidas * 100) / tasks.size();

        getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE)
                .edit()
                .putInt(MainActivity.PROGRESS_KEY, progresso)
                .apply();
    }

    private void salvarTarefas() {
        try {
            JSONArray tasksArray = new JSONArray(tasks);
            JSONArray statusArray = new JSONArray(taskStatus);

            getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE)
                    .edit()
                    .putString(TASKS_KEY, tasksArray.toString())
                    .putString(STATUS_KEY, statusArray.toString())
                    .apply();
        } catch (Exception e) {
            Log.e("ListaTarefas", "Erro ao salvar tarefas", e);
        }
    }
}