package com.example.jonathon.agilebudgeting;

/**
 * Created by Jonathon on 1/20/2017.
 */

public class Deposit extends Item {

    private Deposit() {
        description = "";
        amount = 0;
        account = "";
        itemId = -1;
    }

    public static Deposit createDeposit(PlanningPeriod planId, String date, String desc, double amt, String acct, IPersistItem persister) {
        Deposit newDeposit = new Deposit();

        newDeposit.date = date;
        newDeposit.description = desc;
        newDeposit.amount = amt;
        newDeposit.account = acct;
        newDeposit.planPeriod = planId;
        newDeposit.persister = persister;
        newDeposit.type = "Deposit";
        newDeposit.itemId = persister.persist(newDeposit);


        return newDeposit;

    }

    public static Deposit createDeposit(long itemID, IPersistItem persister) {
        Item retrievedItem = persister.retrieve(itemID);
        Deposit deposit = new Deposit();
        deposit.type = "Deposit";
        deposit.setDate(retrievedItem.getDate());
        deposit.setDescription(retrievedItem.getDescription());
        deposit.setAmount(retrievedItem.getAmount());
        deposit.setAccount(retrievedItem.getAccount());
        deposit.setPlanPeriod(retrievedItem.getPlanPeriod());
        deposit.itemId = retrievedItem.getItemId();
        deposit.persister = persister;
        return deposit;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
