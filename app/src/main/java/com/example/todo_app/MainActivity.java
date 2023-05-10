package com.example.todo_app;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.todo_app.database.Todo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final int ADD_TODO_REQ=1;
    private TodoViewModel todoViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton buttonInsertTodo = findViewById(R.id.button_add);
        buttonInsertTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,InsertTodoActivity.class);
                //startActivity(intent,ADD_TODO_REQ);
                startActivityForResult(intent,ADD_TODO_REQ);
//                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),result -> {
//                    if(result.getResultCode() == Activity.RESULT_OK){
//
//                    }
//                })
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        TodoAdapter adapter = new TodoAdapter();
        recyclerView.setAdapter(adapter);

        todoViewModel = new ViewModelProvider(this).get(TodoViewModel.class);
        todoViewModel.getAllTodos().observe(this, new Observer<List<Todo>>() {
            @Override
            public void onChanged(List<Todo> todos) {
                //update recylerview here
                adapter.setTodos(todos);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ADD_TODO_REQ && resultCode == RESULT_OK){
            String title = data.getStringExtra(InsertTodoActivity.EXTRA_TITLE);
            String desc = data.getStringExtra(InsertTodoActivity.EXTRA_DESC);

            Todo todo = new Todo(title,desc,false);
            todoViewModel.insert(todo);

            Toast.makeText(this, "Todo saved", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "Todo not saved", Toast.LENGTH_SHORT).show();
        }
    }
}