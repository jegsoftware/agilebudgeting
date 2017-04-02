package com.example.jonathon.agilebudgeting;

import java.io.Serializable;

/**
 * Created by Jonathon on 1/7/2017.
 */

public class Item implements Serializable {
    protected String description;
    protected double amount;
    protected String account; //TODO: Make account user-maintainable
    protected long itemId;
    protected PlanningPeriod planPeriod;
    protected String date;
    protected IPersistItem persister;
    protected String type;

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

    public PlanningPeriod getPlanPeriod() {
        return planPeriod;
    }

    public void setPlanPeriod(PlanningPeriod planPeriod) {
        this.planPeriod = planPeriod;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getItemId() {
        return itemId;
    }

    public String getType() {
        return type;
    }

    public long persist() {
        return persister.persist(this);
    }

}
