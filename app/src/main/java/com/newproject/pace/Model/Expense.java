package com.newproject.pace.Model;

public class Expense {
    String expense;
    String date;

    public Expense() {
    }

    public Expense(String expense, String date) {
        this.expense = expense;
        this.date = date;

    }


    public String getExpense() {
        return expense;
    }

    public void setExpense(String expense) {
        this.expense = expense;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
