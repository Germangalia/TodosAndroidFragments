package com.ggalia84.ggalia84.todos;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.lang.reflect.Type;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String SHARED_PREFERENCES_TODOS ="SP_TODOS";
    private static final String TODO_LIST ="todo_list" ;
    private Gson gson;
    public TodoArrayList tasks;
    private CustomListAdapter adapter;
    private String taskName;
    private SharedPreferences todosSave;

    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onStop() {
        super.onStop();

//        gson.toJson()
//
//        String initial_json=
//                SharedPreferences.Editor editor = todos.edit();
//        editor.putString(TODO_LIST, initial_json);
//        editor.commit();

        //When the app stop, save the tasks using sharepreferences and

        if (tasks == null) {
            return;
        }

        String tasksToSave = gson.toJson(tasks);

        todosSave = getSharedPreferences(SHARED_PREFERENCES_TODOS, 0);
        SharedPreferences.Editor editor = todosSave.edit();
        editor.putString(TODO_LIST, tasksToSave);
        editor.apply();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Pull to refresh
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipe_container);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                if (!isOnlineWifi()) {
                    Toast.makeText(getBaseContext(),"No tienes conexión. Comprueba tu conexión a Internet.", Toast.LENGTH_SHORT).show();
                    swipeContainer.setRefreshing(false);
                    tasks = loadTasksFromSharedPreferences();
                }else{
                    Toast.makeText(getBaseContext(),"Tienes conexión a internet.", Toast.LENGTH_SHORT).show();
                    // once the network request has completed successfully.
                    loadTasksOnLineION();
                }

            }

        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        /* JSON Example
        [
            {name:"Compra llet", "done": true, "priority": 2},
            {name:"Compra pa", "done": true, "priority": 1},
            {name:"Fer exercici", "done": false, "priority": 3}
        ]
         */



    }

    private void loadTasksOnLineION() {
        Ion.with(this)
                .load("http://acacha.github.io/json-server-todos/db_todos.json")
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        if (e != null) {
                            tasks = loadTasksFromSharedPreferences();
                        }
                        else {
                            Type arrayTodoList = new TypeToken<TodoArrayList>() {}.getType();
                            gson = new Gson();
                            tasks = gson.fromJson(result.toString(), arrayTodoList);
                        }

                        if(tasks == null) { return; }

                        swipeContainer.setRefreshing(false);

                        loadTaskView();

                    }
                });
        //swipeContainer.setRefreshing(false);
    }

    public TodoArrayList loadTasksFromSharedPreferences() {

        // Restore preferences
        SharedPreferences todos = getSharedPreferences(SHARED_PREFERENCES_TODOS, 0);
        String todoList = todos.getString(TODO_LIST, null);

        if (todoList == null) {
            String initial_json = "[{name:\"Ejemplo tarea 1\", \"done\": false, \"priority\": 2}]";
            SharedPreferences.Editor editor = todos.edit();
            editor.putString(TODO_LIST, initial_json);
            editor.apply();
            todoList = todos.getString(TODO_LIST, null);
        }

//        Log.d("TAG_PROVA", "******************************************************************");
//        Log.d("TAG_PROVA", todoList);
//        Log.d("TAG_PROVA", "******************************************************************");

//        Toast.makeText(this, todoList, Toast.LENGTH_LONG).show();

        /* JSON Example
        [
            {name:"Compra llet", "done": true, "priority": 2},
            {name:"Compra pa", "done": true, "priority": 1},
            {name:"Fer exercici", "done": false, "priority": 3}
        ]
         */

        Type arrayTodoList = new TypeToken<TodoArrayList>() {
        }.getType();
        this.gson = new Gson();
        TodoArrayList temp = gson.fromJson(todoList, arrayTodoList);

        if (temp != null) {
            tasks = temp;

        } else {
            //Error TODO
        }
        return tasks;
    }


    public void loadTaskView() {
        ListView todoslv = (ListView) findViewById(R.id.todolistview);

        //We bind our arraylist of tasks to the adapter
        adapter = new CustomListAdapter(this, tasks);
        todoslv.setAdapter(adapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        FloatingActionButton fabRemove = (FloatingActionButton) findViewById(R.id.fab_remove);
        //        fab.setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View view) {
        //
        //                //Intent intent = new Intent(MainActivity.this, Main2Activity.class );
        //                //startActivity(intent);
        //                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        //                  //      .setAction("Action", null).show();
        //            }
        //        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camara) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    View positiveAction;

    public void showAddForm(View view){
        taskName = " ";

        EditText taskNameText;

        MaterialDialog dialog = new MaterialDialog.Builder(this).
                title("Añadir tarea:").
                customView(R.layout.form_add_task, true).
                negativeText("Cancelar").
                positiveText("Guardar").
                negativeColor(Color.parseColor("#ff3333")).
                positiveColor(Color.parseColor("#2196F3")).
                onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        final TodoItem todoItem = new TodoItem();
                        todoItem.setName(taskName);

                        CheckBox selectedDone = (CheckBox) dialog.findViewById(R.id.task_done);
                        todoItem.setDone(selectedDone.isChecked());

                        // Task priority
                        RadioGroup selectedPriority = (RadioGroup) dialog.findViewById(R.id.task_priority);

                        switch (selectedPriority.getCheckedRadioButtonId()) {
                            case R.id.priority_1:
                                todoItem.setPriority(1);
                                break;
                            case R.id.priority_2:
                                todoItem.setPriority(2);
                                break;
                            case R.id.priority_3:
                                todoItem.setPriority(3);
                                break;
                        }
                        tasks.add(todoItem);
                        adapter.notifyDataSetChanged();
                    }
                }).

                build();

        dialog.show();

        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        positiveAction.setEnabled(false);

        taskNameText = (EditText) dialog.getCustomView().findViewById(R.id.task_tittle);

        taskNameText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                taskName = s.toString();
                positiveAction.setEnabled(taskName.trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    public void removeTasks(View view) {

        //When click on erase button, if task done = true, erase the task.

        for (int i = 0; i < tasks.size(); i++) {

            if(tasks.get(i).isDone() == true) {

                tasks.remove(i);

            }
        }
        //Sincronize view
        adapter.notifyDataSetChanged();
    }


    public void editTask(final int position) {

        final EditText taskNameText;
        RadioGroup checkPriority;

        MaterialDialog dialog = new MaterialDialog.Builder(this).
                title("Editar tarea:").
                customView(R.layout.form_add_task, true).
                negativeText("Cancelar").
                positiveText("Guardar").
                negativeColor(Color.parseColor("#ff3333")).
                positiveColor(Color.parseColor("#2196F3")).
                onPositive(new MaterialDialog.SingleButtonCallback() {

                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {

                        tasks.get(position).setName(taskName);
                        if (tasks.get(position).isDone() == true) {
                            tasks.get(position).setDone(true);
                        } else {
                            tasks.get(position).setDone(false);
                        }

                        // Task priority
                        RadioGroup taskPriority = (RadioGroup) dialog.findViewById(R.id.task_priority);

                        switch (taskPriority.getCheckedRadioButtonId()) {
                            case R.id.priority_1:
                                tasks.get(position).setPriority(1);
                                break;
                            case R.id.priority_2:
                                tasks.get(position).setPriority(2);
                                break;
                            case R.id.priority_3:
                                tasks.get(position).setPriority(3);
                                break;
                        }

                        adapter.notifyDataSetChanged();
                    }
                }).


                build();

        dialog.show();

        taskNameText = (EditText) dialog.getCustomView().findViewById(R.id.task_tittle);
        taskNameText.append(tasks.get(position).getName());
        taskName = taskNameText.getText().toString();

        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        positiveAction.setEnabled(false);

        taskNameText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                taskName = s.toString();
                positiveAction.setEnabled(taskName.trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        checkPriority = (RadioGroup) dialog.getCustomView().findViewById(R.id.task_priority);
        if (tasks.get(position).getPriority() == 1){checkPriority.check(R.id.priority_1);}
        if (tasks.get(position).getPriority() == 2){checkPriority.check(R.id.priority_2);}
        if (tasks.get(position).getPriority() == 3){checkPriority.check(R.id.priority_3);}

        checkPriority.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup taskPriority, int checkedId) {
                positiveAction.setEnabled(true);
            }
        });
    }

    public boolean isOnlineWifi() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }

        return false;
    }

}


