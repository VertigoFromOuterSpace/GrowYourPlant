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
    // Chaves pra salvar no celular (tipo um armário com gavetas marcadas)
    private static final String TASKS_KEY = "tasks";  // Gaveta das tarefas
    private static final String STATUS_KEY = "status"; // Gaveta dos status (feito/não feito)

    // Listas que armazenam as tarefas e se tão feitas ou não
    private List<String> tasks; // ["Comprar pão", "Lavar roupa"]
    private List<Boolean> taskStatus; // [true, false] (true = feito)

    private TaskAdapter adapter; // O cara que cuida da listinha bonitinha
    private ListView listViewTasks; // A listinha em si na tela

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_tarefas); // Pega o layout XML

        // Liga os componentes da tela
        listViewTasks = findViewById(R.id.listViewTasks);
        tasks = new ArrayList<>(); // Lista vazia pra começar
        taskStatus = new ArrayList<>();

        carregarTarefasSalvas(); // Pega o que tava salvo no celular

        // Configura o adapter (o "middleman" entre a lista e a tela)
        adapter = new TaskAdapter(this, tasks, taskStatus, this);
        listViewTasks.setAdapter(adapter); // Joga tudo na tela

        // Botão de voltar - só fecha essa tela
        findViewById(R.id.btnVoltar).setOnClickListener(v -> finish());

        // Botão de adicionar tarefa
        findViewById(R.id.buttonAdd).setOnClickListener(v -> {
            String task = ((android.widget.EditText) findViewById(R.id.editTextTask)).getText().toString().trim();
            if(!task.isEmpty()){
                tasks.add(task); // Coloca na lista
                taskStatus.add(false); // Começa como não feita
                adapter.notifyDataSetChanged(); // Atualiza a tela
                ((android.widget.EditText) findViewById(R.id.editTextTask)).setText(""); // Limpa o campo
                salvarTarefas(); // Guarda no celular
                atualizarProgresso(); // Atualiza a % lá na main
            }
        });
    }

    // Pega as tarefas salvas no celular
    private void carregarTarefasSalvas() {
        SharedPreferences prefs = getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
        String tasksJson = prefs.getString(TASKS_KEY, "[]"); // Pega como JSON
        String statusJson = prefs.getString(STATUS_KEY, "[]");

        try {
            // Transforma o JSON em listas normais
            JSONArray tasksArray = new JSONArray(tasksJson);
            JSONArray statusArray = new JSONArray(statusJson);

            tasks.clear(); // Limpa tudo antes
            taskStatus.clear();

            // Pega cada tarefa e joga na lista
            for (int i = 0; i < tasksArray.length(); i++) {
                tasks.add(tasksArray.getString(i));
            }

            // Pega cada status (feito/não feito)
            for (int i = 0; i < statusArray.length(); i++) {
                taskStatus.add(statusArray.getBoolean(i));
            }
        } catch (JSONException e) {
            Log.e("ListaTarefas", "Erro ao carregar tarefas", e); // Se der ruim, avisa
        }
    }

    // Apaga uma tarefa
    public void removerTarefa(int position) {
        if (position >= 0 && position < tasks.size()) {
            tasks.remove(position); // Tira da lista de tarefas
            taskStatus.remove(position); // Tira da lista de status
            adapter.notifyDataSetChanged(); // Atualiza a tela
            salvarTarefas(); // Salva no celular
            atualizarProgresso(); // Atualiza a %
        }
    }

    // Atualiza quando marca/desmarca uma tarefa
    public void atualizarStatusTarefa(int position, boolean isChecked) {
        if (position >= 0 && position < taskStatus.size()) {
            taskStatus.set(position, isChecked); // Atualiza o status
            salvarTarefas(); // Salva no celular
            atualizarProgresso(); // Atualiza a %
        }
    }

    // Calcula quantas % das tarefas tão feitas
    private void atualizarProgresso() {
        int concluidas = 0;
        for (Boolean status : taskStatus) {
            if (status) concluidas++; // Conta quantas tão feitas
        }
        // Calcula a % (evita divisão por zero)
        int progresso = tasks.isEmpty() ? 0 : (concluidas * 100) / tasks.size();

        // Salva a % no celular
        getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE)
                .edit()
                .putInt(MainActivity.PROGRESS_KEY, progresso)
                .apply();
    }

    // Salva as listas no celular (em JSON)
    private void salvarTarefas() {
        try {
            JSONArray tasksArray = new JSONArray(tasks); // Converte pra JSON
            JSONArray statusArray = new JSONArray(taskStatus);

            // Guarda no armário do celular (SharedPreferences)
            getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE)
                    .edit()
                    .putString(TASKS_KEY, tasksArray.toString())
                    .putString(STATUS_KEY, statusArray.toString())
                    .apply();
        } catch (Exception e) {
            Log.e("ListaTarefas", "Erro ao salvar tarefas", e); // Se der pau, avisa
        }
    }
}