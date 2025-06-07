package com.example.testingcode;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import java.util.List;

public class TaskAdapter extends ArrayAdapter<String> {
    private final List<String> tasks; // Lista de tarefas
    private final List<Boolean> taskStatus; // Lista de status
    private final ListaTarefas activity; // Referência pra activity

    // Construtor - recebe os dados e a activity
    public TaskAdapter(Context context, List<String> tasks, List<Boolean> taskStatus, ListaTarefas activity) {
        super(context, R.layout.task_item, tasks);
        this.tasks = tasks;
        this.taskStatus = taskStatus;
        this.activity = activity;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Se não tiver view pronta, cria uma
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.task_item, parent, false);
        }

        // Pega os componentes do layout
        TextView textView = convertView.findViewById(R.id.textViewTask);
        CheckBox checkBox = convertView.findViewById(R.id.checkBoxTask);
        Button btnExcluir = convertView.findViewById(R.id.btnExcluir);

        // Configura a tarefa e o status
        textView.setText(tasks.get(position));
        checkBox.setChecked(taskStatus.get(position));

        // Quando marca/desmarca a checkbox
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            activity.atualizarStatusTarefa(position, isChecked); // Atualiza na activity
        });

        // Configura o botão de excluir (vermelhão)
        btnExcluir.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.holo_red_dark));
        btnExcluir.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
        btnExcluir.setText("X");
        btnExcluir.setOnClickListener(v -> activity.removerTarefa(position)); // Remove quando clica

        return convertView; // Retorna a view pronta
    }
}