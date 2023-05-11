package com.example.todo_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.todo_app.database.Todo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final int ADD_TODO_REQ=1;
    public static final int EDIT_TODO_REQ=2;
    private TodoViewModel todoViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        FloatingActionButton buttonInsertTodo = findViewById(R.id.button_add);
        buttonInsertTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, InsertEditTodoActivity.class);
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

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            private Drawable deleteIcon;
            private int iconMargin;
            private boolean initiated;

            private Todo deletedTodo;

            private void init() {
                deleteIcon = ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_delete);
                iconMargin = getResources().getDimensionPixelSize(R.dimen.delete_icon_margin);
                initiated = true;
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//                todoViewModel.delete(adapter.getTodoAt(viewHolder.getAdapterPosition()));
//                Toast.makeText(MainActivity.this, "Task deleted!", Toast.LENGTH_SHORT).show();
                int position = viewHolder.getAdapterPosition();
                deletedTodo = adapter.getTodoAt(position);
                todoViewModel.delete(deletedTodo);
                Toast.makeText(MainActivity.this, "Task deleted!", Toast.LENGTH_SHORT).show();


                // Show Snackbar with undo option
                Snackbar snackbar = Snackbar.make(recyclerView, "Task deleted", Snackbar.LENGTH_LONG);
                snackbar.setAction("Undo", v -> {
                    // Undo delete action
                    if (deletedTodo != null) {
                        todoViewModel.insert(deletedTodo);
                    }
                });
                snackbar.setActionTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorAccent));
                snackbar.addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT || event == Snackbar.Callback.DISMISS_EVENT_SWIPE) {
                            // If Snackbar times out or user swipes it away, permanently delete the item
                            if (deletedTodo != null) {
                                todoViewModel.delete(deletedTodo);
                                deletedTodo = null;
                            }
                        }
                        else{
                            Toast.makeText(MainActivity.this, "Task Restored", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                snackbar.show();
            }


            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (!initiated) {
                    init();
                }

                View itemView = viewHolder.itemView;
                int itemHeight = itemView.getHeight();
                int itemWidth = itemView.getWidth();
                int deleteIconTop = itemView.getTop() + (itemHeight - deleteIcon.getIntrinsicHeight()) / 2;
                int deleteIconBottom = deleteIconTop + deleteIcon.getIntrinsicHeight();

                if (dX > 0) { // Swiping to the right (positive direction)
                    int deleteIconLeft = itemView.getLeft() + iconMargin;
                    int deleteIconRight = itemView.getLeft() + iconMargin + deleteIcon.getIntrinsicWidth();
                    deleteIcon.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom);
                } else if (dX < 0) { // Swiping to the left (negative direction)
                    int deleteIconRight = itemView.getRight() - iconMargin;
                    int deleteIconLeft = itemView.getRight() - iconMargin - deleteIcon.getIntrinsicWidth();
                    deleteIcon.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom);
                } else {
                    deleteIcon.setBounds(0, 0, 0, 0); // No swipe action, hide the delete icon
                }

                deleteIcon.draw(c);
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new TodoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Todo todo) {
                Intent intent = new Intent(MainActivity.this, InsertEditTodoActivity.class);
                intent.putExtra(InsertEditTodoActivity.EXTRA_ID,todo.getId());
                intent.putExtra(InsertEditTodoActivity.EXTRA_ID,todo.getCreated());
                intent.putExtra(InsertEditTodoActivity.EXTRA_TITLE,todo.getTitle());
                intent.putExtra(InsertEditTodoActivity.EXTRA_DESC,todo.getDescription());
                startActivityForResult(intent,EDIT_TODO_REQ);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ADD_TODO_REQ && resultCode == RESULT_OK){
            String title = data.getStringExtra(InsertEditTodoActivity.EXTRA_TITLE);
            String desc = data.getStringExtra(InsertEditTodoActivity.EXTRA_DESC);

            Todo todo = new Todo(title,desc,false);
            todoViewModel.insert(todo);

            Toast.makeText(this, "Todo saved", Toast.LENGTH_SHORT).show();
        }
        else if(requestCode == EDIT_TODO_REQ && resultCode == RESULT_OK){
            int id = data.getIntExtra(InsertEditTodoActivity.EXTRA_ID,-1);

            if (id == -1) {
                Toast.makeText(this, "Task cannot be updated", Toast.LENGTH_SHORT).show();
                return;
            }
            String title = data.getStringExtra(InsertEditTodoActivity.EXTRA_TITLE);
            String desc = data.getStringExtra(InsertEditTodoActivity.EXTRA_DESC);

            Todo todo = new Todo(title,desc,false);
            todo.setId(id);
            todoViewModel.update(todo);
        }
        else {
            Toast.makeText(this, "Todo not saved", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_all_task:
                todoViewModel.deleteAllTodos();
                Toast.makeText(this, "All tasks deleted", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}