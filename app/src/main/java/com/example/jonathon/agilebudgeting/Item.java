package com.example.jonathon.agilebudgeting;

import android.content.Context;

import java.io.Serializable;

/**
 * Created by Jonathon on 1/7/2017.
 */

public abstract class Item implements Serializable {
    protected String description;
    protected double amount;
    protected String account; //TODO: Make account user-maintainable
    protected long itemId;
    protected long planId;
    protected String date;

    protected Item() {
        date = "";
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public String getAmountString() {
        return String.format("%1.2f", amount);
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public long getPlanId() {
        return planId;
    }

    public void setPlanId(long planId) {
        this.planId = planId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public abstract long persist();

    public long getItemId() {
        return itemId;
    }
}
