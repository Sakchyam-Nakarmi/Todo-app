package com.example.todo_app;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.todo_app.database.Todo;
import com.example.todo_app.database.TodoDatabase;

import java.util.List;

public class TodoRepository {
    private TodoDatabase database;
    private LiveData<List<Todo>> todoList;

    public TodoRepository(Application application){
        database = TodoDatabase.getDatabase(application);
        todoList = database.todoDao().getAllTodos();
    }

    public void addTodo(Todo todo){
        database.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                database.todoDao().addTodo(todo);
            }
        });
    }

    public void updateTodo(Todo todo){
        database.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                database.todoDao().updateTodo(todo);
            }
        });
    }

    public void deleteTodo(Todo todo){
        database.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                database.todoDao().deleteTodo(todo);
            }
        });
    }

    public void deleteAllTodo(){
        database.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                database.todoDao().deleteAllTodo();
            }
        });
    }

    public LiveData<List<Todo>> getAllTodos(){
        return todoList;
    }

    public LiveData<List<Todo>> getAllTodosSorted() {
        return database.todoDao().getAllTodosSorted();
    }


}
