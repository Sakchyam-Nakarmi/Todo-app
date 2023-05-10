package com.example.todo_app;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class TodoViewModel extends AndroidViewModel {
    private TodoRepository repository;

    public TodoViewModel(@NonNull Application application) {
        super(application);
        repository = new TodoRepository(application);
    }
}
