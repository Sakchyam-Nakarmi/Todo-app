package com.example.todo_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class InsertEditTodoActivity extends AppCompatActivity {
    public static final String EXTRA_ID =
            "com.example.todo_app.EXTRA_ID";
    public static final String EXTRA_TITLE =
            "com.example.todo_app.EXTRA_TITLE";
    public static final String EXTRA_DESC =
            "com.example.todo_app.EXTRA_DESC";

    public static final String EXTRA_COMP =
            "com.example.todo_app.EXTRA_COMP";
    public static final String EXTRA_CREATED =
            "com.example.todo_app.EXTRA_CREATED";

    private boolean completed;
    private EditText editTitle,editDesc;
    private TextView createdDate;


    private int dbId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_todo);

        editTitle = findViewById(R.id.editTitle);
        editDesc = findViewById(R.id.editDesc);
        createdDate = findViewById(R.id.createdDate);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        Intent intent = getIntent();
        int mode = intent.getIntExtra("MODE",-1);
        if(mode==2){
            setTitle("Update Task");
            completed = intent.getBooleanExtra(EXTRA_COMP,false);
            editTitle.setText(intent.getStringExtra(EXTRA_TITLE));
            editDesc.setText(intent.getStringExtra(EXTRA_DESC));
            Calendar calendar= Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm aaa");
            createdDate.setText("Updated at: "+dateFormat.format(calendar.getTime()));
            dbId = intent.getIntExtra(EXTRA_ID,-2);
        }else if(mode ==1) {
            setTitle("Insert Task");
            Calendar calendar= Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm aaa");
            createdDate.setText("Created at: "+dateFormat.format(calendar.getTime()));
        }
        else
        {}

    }

    private void saveTodo(){
        String title = editTitle.getText().toString();
        String desc = editDesc.getText().toString();
        String date = createdDate.getText().toString();

        if(title.trim().isEmpty() || desc.trim().isEmpty()){
            Toast.makeText(this, "Please enter new task", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent data = new Intent();
        data.putExtra("ID",dbId);
        data.putExtra("Comp",completed);
        data.putExtra(EXTRA_TITLE,title);
        data.putExtra(EXTRA_DESC,desc);
        data.putExtra(EXTRA_CREATED,date);

        int id = getIntent().getIntExtra("MODE",-1);
        if(id != -1){
            data.putExtra("MODE",id);
        }

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