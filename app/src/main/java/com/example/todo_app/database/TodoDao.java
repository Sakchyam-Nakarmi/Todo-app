package com.example.todo_app.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TodoDao {
    @Insert
    void addTodo(Todo todo);

    @Update
    void updateTodo(Todo todo);

    @Delete
    void deleteTodo(Todo todo);

    @Query("delete from todos")
    void deleteAllTodo();

    @Query("SELECT * FROM todos") //add order by date later !!!
    LiveData<List<Todo>> getAllTodos();
}
