package com.example.rockettodo.model;

public class Task {
    private int idTask;
    private String text;
    private boolean status;

    public Task(String text) {
        this.text = text;
    }

    public Task(){

    }

    public int getIdTask() {
        return idTask;
    }

    public void setIdTask(int idTask) {
        this.idTask = idTask;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
