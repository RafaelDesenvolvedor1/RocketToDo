package com.example.rockettodo;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;

import androidx.appcompat.app.AlertDialog;

import com.example.rockettodo.model.Task;

import java.util.List;

public class TaskAdapter extends ArrayAdapter<Task> {
    private Context context;
    private List<Task> tasks;


    public TaskAdapter(Context context, List<Task> tasks) {
        super(context, R.layout.task_layout, tasks);
        this.context = context;
        this.tasks = tasks;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.task_layout, parent, false);
        }

        Task taskAtual = getItem(position);
        CheckBox checkText = convertView.findViewById(R.id.checkText);
        ImageButton btnDelete = convertView.findViewById(R.id.btnDelete);

        btnDelete.setOnClickListener(v -> {
            // Criar um alerta de confirmação é uma boa prática!
            new AlertDialog.Builder(context)
                    .setTitle("Excluir Tarefa")
                    .setMessage("Tem certeza que deseja remover esta tarefa?")
                    .setPositiveButton("Sim", (dialog, which) -> {
                        if (context instanceof MainActivity) {
                            ((MainActivity) context).deletarTask(taskAtual.getIdTask());
                        }
                    })
                    .setNegativeButton("Não", null)
                    .show();
        });

        // IMPORTANTE: Remove o listener antes de mudar o estado para evitar bugs de reciclagem
        checkText.setOnCheckedChangeListener(null);

        // 1. Define o texto e o estado baseado no objeto Task
        checkText.setText(taskAtual.getText());
        checkText.setChecked(taskAtual.isStatus());

        // 2. Aplica o visual inicial (riscado ou não)
        aplicarRisco(checkText, taskAtual.isStatus());

        // 3. Define o novo listener para quando o usuário clicar
        checkText.setOnCheckedChangeListener((buttonView, isChecked) -> {
            taskAtual.setStatus(isChecked); // Atualiza o objeto na memória
            aplicarRisco(checkText, isChecked); // Atualiza o visual

            // CHAMA O MÉTODO DA MAIN ACTIVITY AQUI:
            if (context instanceof MainActivity) {
                ((MainActivity) context).atualizarStatusNoBanco(taskAtual.getIdTask(), isChecked);
                ((MainActivity) context).atualizarContadores();
            }
        });

        return convertView;
    }

    private void aplicarRisco(CheckBox checkBox, boolean concluida) {
        if (concluida) {
            // Aplica o line-through e muda a cor para cinza (estilo RocketToDo)
            checkBox.setPaintFlags(checkBox.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            checkBox.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
        } else {
            // Remove o risco e volta para a cor branca
            checkBox.setPaintFlags(checkBox.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            checkBox.setTextColor(context.getResources().getColor(android.R.color.white));
        }
    }
}
