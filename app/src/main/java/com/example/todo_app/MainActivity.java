package com.example.todo_app;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.todo_app.database.Todo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final int ADD_TODO_REQ=1;
    public static final int EDIT_TODO_REQ=2;
    public static TodoViewModel todoViewModel;

    private TodoAdapter adapter;

    public static ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        FloatingActionButton buttonInsertTodo = findViewById(R.id.button_add);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        adapter = new TodoAdapter();
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
                int position = viewHolder.getAdapterPosition();
                deletedTodo = adapter.getTodoAt(position);
                todoViewModel.delete(deletedTodo);


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

        adapter.setOnItemClickListener(todo -> {
            Intent intent = new Intent(MainActivity.this, InsertEditTodoActivity.class);
            intent.putExtra(InsertEditTodoActivity.EXTRA_ID,todo.getId());
            intent.putExtra(InsertEditTodoActivity.EXTRA_CREATED,todo.getCreated());
            intent.putExtra(InsertEditTodoActivity.EXTRA_TITLE,todo.getTitle());
            intent.putExtra(InsertEditTodoActivity.EXTRA_DESC,todo.getDescription());
            intent.putExtra("MODE", EDIT_TODO_REQ);
            activityResultLauncher.launch(intent);

        });

        adapter.setOnItemClickListener(new TodoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Todo todo) {
                Intent intent = new Intent(MainActivity.this, InsertEditTodoActivity.class);
                intent.putExtra(InsertEditTodoActivity.EXTRA_ID, todo.getId());
                intent.putExtra(InsertEditTodoActivity.EXTRA_CREATED, todo.getCreated());
                intent.putExtra(InsertEditTodoActivity.EXTRA_TITLE, todo.getTitle());
                intent.putExtra(InsertEditTodoActivity.EXTRA_DESC, todo.getDescription());
                intent.putExtra(InsertEditTodoActivity.EXTRA_COMP,todo.isCompleted());
                intent.putExtra("MODE", EDIT_TODO_REQ);
                activityResultLauncher.launch(intent);
            }
        });


        buttonInsertTodo.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, InsertEditTodoActivity.class);
            intent.putExtra("MODE",ADD_TODO_REQ);
            activityResultLauncher.launch(intent);
        });

        activityResultLauncher= registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result.getResultCode()==RESULT_OK)
            {
                if(result.getData().getIntExtra("MODE",-1)==2)
                {
                    int id1 = result.getData().getIntExtra("MODE",-1);

                    if (id1 == -1) {
                        Toast.makeText(this, "Task cannot be updated", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String title = result.getData().getStringExtra(InsertEditTodoActivity.EXTRA_TITLE);
                    String desc = result.getData().getStringExtra(InsertEditTodoActivity.EXTRA_DESC);
                    int id = result.getData().getIntExtra("ID",-2);
                    boolean checked = result.getData().getBooleanExtra("Comp",false);

                    Todo todo = new Todo(id,title,desc,checked);
                    todoViewModel.update(todo);
                    Toast.makeText(this, "Task updated", Toast.LENGTH_SHORT).show();

                } else if (result.getData().getIntExtra("MODE",-1)==1) {

                    String title = result.getData().getStringExtra(InsertEditTodoActivity.EXTRA_TITLE);
                    String desc = result.getData().getStringExtra(InsertEditTodoActivity.EXTRA_DESC);

                    Todo todo = new Todo(title,desc,false);
                    todoViewModel.insert(todo);
                    Toast.makeText(this, "Task Added!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(this, "Todo not saved", Toast.LENGTH_SHORT).show();
                }

            }
        });


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
            case R.id.delete_done_task:_task:
                todoViewModel.deleteDoneTodos();
                Toast.makeText(this, "All Completed tasks deleted", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.sort_by_title:
                todoViewModel.getAllTodosSorted().observe(this, new Observer<List<Todo>>() {
                    @Override
                    public void onChanged(List<Todo> todos) {
                        adapter.setTodos(todos);
                    }
                });
                return true;
            case R.id.hide_completed:
                todoViewModel.getUncheckedTodos().observe(this, new Observer<List<Todo>>() {
                    @Override
                    public void onChanged(List<Todo> todos) {
                        adapter.setTodos(todos);
                    }
                });
                return true;
            case R.id.show_all:
                todoViewModel.getAllTodos().observe(this, new Observer<List<Todo>>() {
                    @Override
                    public void onChanged(List<Todo> todos) {
                        adapter.setTodos(todos);
                    }
                });
                return true;
            case R.id.about:
                Intent gotoAboutIntent = new Intent(MainActivity.this,AboutActivity.class);
                startActivity(gotoAboutIntent);
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}