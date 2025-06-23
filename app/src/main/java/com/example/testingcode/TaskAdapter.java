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
    private final List<String> tasks;
    private final List<Boolean> taskStatus;
    private final ListaTarefas activity;

    public TaskAdapter(Context context, List<String> tasks, List<Boolean> taskStatus, ListaTarefas activity) {
        super(context, R.layout.task_item, tasks);
        this.tasks = tasks;
        this.taskStatus = taskStatus;
        this.activity = activity;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.task_item, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.textViewTask);
        CheckBox checkBox = convertView.findViewById(R.id.checkBoxTask);
        Button btnExcluir = convertView.findViewById(R.id.btnExcluir);

        textView.setText(tasks.get(position));
        checkBox.setChecked(taskStatus.get(position));

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            activity.atualizarStatusTarefa(position, isChecked);
        });

        btnExcluir.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.holo_red_dark));
        btnExcluir.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
        btnExcluir.setText("X");
        btnExcluir.setOnClickListener(v -> activity.removerTarefa(position));

        return convertView;
    }
}