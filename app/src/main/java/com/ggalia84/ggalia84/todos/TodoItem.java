package com.ggalia84.ggalia84.todos;

import com.google.gson.Gson;

public class TodoItem {

    private String name;
    private boolean done;
    private int priority;

    public TodoItem() {
        this.name = "";
        this.done = false;
        this.priority = 1;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }


    @Override
    public String toString() {
        return "[ name:" +name + ", done:" + done + ", priority:" + priority+"]";
    }

    public String serializable(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}