package com.example.jonathon.agilebudgeting;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.GregorianCalendar;

import static android.content.Intent.ACTION_EDIT;
import static android.content.Intent.ACTION_MAIN;

public class EditDepositActivity extends AppCompatActivity{

    private Plan plan;
    private Deposit deposit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_deposit_activity);

        Intent intent = getIntent();
        String action = intent.getAction();

        long planId = intent.getLongExtra("com.example.jonathon.agilebudgeting.PLAN_ID", -1);
        plan = Plan.createPlan(this, planId);
        if (action.equals(ACTION_MAIN)) {
            TextView depositPeriod = (TextView) findViewById(R.id.depositPeriodDisplay);
            depositPeriod.setText(plan.getPeriod().getPeriodStartDate());
            deposit = null;
        }
        else if (action.equals(ACTION_EDIT)) {
            long itemId = intent.getLongExtra("com.example.jonathon.agilebudgeting.ITEM_ID", -1);
            deposit = Deposit.createDeposit(getApplicationContext(), itemId);
            populateFields();
        }

    }

    private void populateFields() {
        if (deposit != null) {
            EditText depositDateField = (EditText) findViewById(R.id.depositDateField);
            depositDateField.setText(deposit.getDate());

            EditText descField = (EditText) findViewById(R.id.depositDescriptionField);
            descField.setText(deposit.getDescription());

            EditText amountField = (EditText) findViewById(R.id.depositAmountField);
            amountField.setText(deposit.getAmountString());

            EditText acctField = (EditText) findViewById(R.id.depositAccountField);
            acctField.setText(deposit.getAccount());
        }
    }

    public void backToPlan(View view) {
        // TODO: Add validation that fields are empty or prompt user for confirmation before going back to the plan
        setResult(RESULT_CANCELED);
        finish();
    }

    public void clearFields(View view) {
        EditText depositDateField = (EditText) findViewById(R.id.depositDateField);
        depositDateField.setText("");

        EditText descField = (EditText) findViewById(R.id.depositDescriptionField);
        descField.setText("");

        EditText amountField = (EditText) findViewById(R.id.depositAmountField);
        amountField.setText("");

        EditText acctField = (EditText) findViewById(R.id.depositAccountField);
        acctField.setText("");

    }

    public void saveDeposit(View view) {
        EditText depositDateField = (EditText) findViewById(R.id.depositDateField);
        String depositDate = depositDateField.getText().toString();

        EditText descField = (EditText) findViewById(R.id.depositDescriptionField);
        String desc = descField.getText().toString();

        EditText amountField = (EditText) findViewById(R.id.depositAmountField);
        double amount = Double.parseDouble(amountField.getText().toString());

        EditText acctField = (EditText) findViewById(R.id.depositAccountField);
        String acct = acctField.getText().toString();

        if (null == deposit) {
            deposit = Deposit.createDeposit(plan.getPlanId(), depositDate, desc, amount, acct);
        }
        else {
            deposit.setDate(depositDate);
            deposit.setDescription(desc);
            deposit.setAmount(amount);
            deposit.setAccount(acct);
        }
        long depositId = deposit.persist(this);

        Intent returnIntent = new Intent();

        returnIntent.putExtra("com.example.jonathon.agilebudgeting.ITEM_ID", depositId);

        setResult(RESULT_OK, returnIntent);
        finish();
    }


}
