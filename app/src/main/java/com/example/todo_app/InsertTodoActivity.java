package com.example.todo_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class InsertTodoActivity extends AppCompatActivity {
    public static final String EXTRA_TITLE =
            "com.example.todo_app.EXTRA_TITLE";
    public static final String EXTRA_DESC =
            "com.example.todo_app.EXTRA_DESC";

    private EditText editTitle,editDesc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_todo);

        editTitle = findViewById(R.id.editTitle);
        editDesc = findViewById(R.id.editDesc);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        setTitle("Insert Task");
    }

    private void saveTodo(){
        String title = editTitle.getText().toString();
        String desc = editDesc.getText().toString();

        if(title.trim().isEmpty() || desc.trim().isEmpty()){
            Toast.makeText(this, "Please enter new task", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent data = new Intent();
        data.putExtra(EXTRA_TITLE,title);
        data.putExtra(EXTRA_DESC,desc);

        setResult(RESULT_OK,data);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.insert_todo_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.save_todo:
                saveTodo();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}