package com.example.todo_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo_app.database.Todo;

import java.util.ArrayList;
import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoHolder> {
    private List<Todo> todos = new ArrayList<>();
    private OnItemClickListener listener;

    @NonNull
    @Override
    public TodoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.todo_item, parent, false);
        return new TodoHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoHolder holder, int position) {
        Todo currentTodo = todos.get(position);
        holder.title.setText(currentTodo.getTitle());
        holder.description.setText(currentTodo.getDescription());
        holder.completed.isChecked();

    }

    @Override
    public int getItemCount() {
        return todos.size();
    }

    public void setTodos(List<Todo> todos) {
        this.todos = todos;
        notifyDataSetChanged();

    }

    public Todo getTodoAt(int position) {
        return todos.get(position);
    }

    class TodoHolder extends RecyclerView.ViewHolder {
        private TextView title, description;
        private CheckBox completed;

        public TodoHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            completed = itemView.findViewById(R.id.completed);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(todos.get(position));
                    }

                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Todo todo);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;

    }
}
