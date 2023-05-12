package com.example.todo_app;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.todo_app.database.Todo;

import java.util.List;

public class TodoViewModel extends AndroidViewModel {
    private TodoRepository repository;
    private LiveData<List<Todo>> allTodos;

    public TodoViewModel(@NonNull Application application) {
        super(application);
        repository = new TodoRepository(application);
        allTodos = repository.getAllTodos();

    }

    public void insert (Todo todo){
        repository.addTodo(todo);
    }

    public void update (Todo todo){
        repository.updateTodo(todo);
    }

    public void delete (Todo todo){
        repository.deleteTodo(todo);
    }

    public void deleteAllTodos (){
        repository.deleteAllTodo();
    }

    public LiveData<List<Todo>> getAllTodos(){
        return allTodos;
    }

    public LiveData<List<Todo>> getAllTodosSorted() {
        return repository.getAllTodosSorted();
    }


}
