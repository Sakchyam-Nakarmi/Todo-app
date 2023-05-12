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
        database = TodoDatabase.getInstance(application);
        todoList = database.todoDao().getAllTodos();
    }

    public void addTodo(Todo todo){
        database.todoDao().addTodo(todo);
    }

    public void updateTodo(Todo todo){

        database.todoDao().updateTodo(todo);
    }

    public void deleteTodo(Todo todo){
        database.todoDao().deleteTodo(todo);
    }

    public void deleteAllTodo(){
        database.todoDao().deleteAllTodo();
    }

    public void deleteDoneTodos(){
        database.todoDao().deleteDoneTodos();
    }

    public LiveData<List<Todo>> getAllTodos(){
        return todoList;
    }

    public LiveData<List<Todo>> getAllTodosSorted() {
        return database.todoDao().getAllTodosSorted();
    }

    public LiveData<List<Todo>> getUncheckedTodos() {
        return database.todoDao().getUncheckedTodos();
    }


}
