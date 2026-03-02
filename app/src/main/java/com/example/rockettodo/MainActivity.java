package com.example.rockettodo;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.rockettodo.model.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView myList;
    private TextView txtCriadas;
    private TextView txtConcluidas;
    private Button btnCriadas;
    private Button btnConcluidas;


    private SQLiteDatabase bd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        createDB();
        loadTask(false);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void showToast(String text){
        Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    private void createDB(){
        try{
            bd = openOrCreateDatabase("RocketToDO", MODE_PRIVATE, null);
            bd.execSQL("CREATE TABLE IF NOT EXISTS minhasTarefas(id INTEGER PRIMARY KEY AUTOINCREMENT, tarefa VARCHAR, status INTEGER)");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void addTask(String text) {
        try {
            if (text.trim().length() != 0) {
                // O 0 tem que ficar dentro dos parênteses!
                // Corrigido: VALUES ('texto', 0)
                bd.execSQL("INSERT INTO minhasTarefas(tarefa, status) VALUES ('" + text + "', 0)");

                loadTask(false); // Atualiza a tela
                showToast("Tarefa Inserida");
            } else {
                showToast("Insira uma tarefa");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast("Erro ao salvar no banco");
        }
    }

    private void loadTask(boolean tasksConcluidas){
        Cursor cursor;
        if(tasksConcluidas){
            cursor = bd.rawQuery("SELECT * FROM minhasTarefas WHERE status = 1 ORDER BY id DESC", null);

        }else{
            cursor = bd.rawQuery("SELECT * FROM minhasTarefas ORDER BY id DESC", null);
        }


        int idIndice = cursor.getColumnIndex("id");
        int taskIndice = cursor.getColumnIndex("tarefa");
        int statusIndice = cursor.getColumnIndex("status");

        List<Task> taskList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                // Criamos o objeto Task com os dados da linha atual
                Task task = new Task();
                task.setIdTask(cursor.getInt(idIndice));
                task.setText(cursor.getString(taskIndice));
                task.setStatus(cursor.getInt(statusIndice) == 1);

                // Adicionamos na nossa lista
                taskList.add(task);
            } while (cursor.moveToNext());
        }

        cursor.close(); // Sempre feche o cursor para economizar memória!

        // 4. Configura o Adapter com a lista REAL
        TaskAdapter taskAdapter = new TaskAdapter(this, taskList);
        myList = findViewById(R.id.listView2);
        myList.setAdapter(taskAdapter);

        atualizarContadores();
    }


    public void deletarTask(int id){
        try {
            bd.execSQL("DELETE FROM minhasTarefas WHERE id = " + id);
            loadTask(false); // Recarrega a ListView
            atualizarContadores(); // Atualiza os números no topo
            showToast("Tarefa removida");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void atualizarStatusNoBanco(int id, boolean concluida) {
        int statusInt = concluida ? 1 : 0;
        try {
            bd.execSQL("UPDATE minhasTarefas SET status = " + statusInt + " WHERE id = " + id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getTotalTarefas(){
        Cursor cursor = bd.rawQuery("SELECT COUNT(*) FROM minhasTarefas", null);
        int total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }
        cursor.close();
        return total;
    }

    public int getTarefasConcluidas() {
        // Filtramos onde o status é 1 (true)
        Cursor cursor = bd.rawQuery("SELECT COUNT(*) FROM minhasTarefas WHERE status = 1", null);
        int total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }
        cursor.close();
        return total;
    }

    void atualizarContadores() {
        txtCriadas = findViewById(R.id.txtCriadas);
        txtConcluidas = findViewById(R.id.txtConcluidas);

        int criadas = getTotalTarefas();
        int concluidas = getTarefasConcluidas();

        txtCriadas.setText(String.valueOf(criadas));
        txtConcluidas.setText(concluidas + " de " + criadas);
    }

    public void adicionarItem(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Adicione um tarefa");
        builder.setMessage("O que você precisa fazer?");
        final EditText inputField = new EditText(this);
        builder.setView(inputField);
        builder.setPositiveButton("Adicionar",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String tarefa = inputField.getText().toString();
                        addTask(tarefa);
                    }
                }
        );

        builder.show();
    }

    public void loadTasksConcluidos(View v){
        loadTask(true);
    }

    public  void loadAllTasks(View v){
        loadTask(false);
    }


}