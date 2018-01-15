package com.example.jonathon.agilebudgeting;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import static android.content.Intent.ACTION_EDIT;
import static android.content.Intent.ACTION_MAIN;

public class EditDepositActivity extends AppCompatActivity{

    private static final int CREATE_PLANNED_EXPENSE = 0;
    private Plan plan;
    private Item item;
    private int requestCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_deposit_activity);

        Intent intent = getIntent();
        String action = intent.getAction();
        requestCode = intent.getIntExtra("requestCode", -1);
        plan = (Plan) intent.getSerializableExtra("com.example.jonathon.agilebudgeting.PLAN");

        TextView itemPeriod = (TextView) findViewById(R.id.depositPeriodDisplay);
        itemPeriod.setText(plan.getPeriod().getPeriodStartDate());

        if (requestCode == CREATE_PLANNED_EXPENSE) {
            TextView depositDateLabel = (TextView) findViewById(R.id.depositDateLabel);
            EditText depositDateField = (EditText) findViewById(R.id.depositDateField);
            depositDateLabel.setVisibility(View.GONE);
            depositDateField.setVisibility(View.GONE);
        }

        if (action.equals(ACTION_MAIN)) {
            item = null;
        }
        else if (action.equals(ACTION_EDIT)) {
            item = (Item) intent.getSerializableExtra("com.example.jonathon.agilebudgeting.ITEM");
            populateFields();
        }

    }

    private void populateFields() {
        if (item != null) {
            EditText depositDateField = (EditText) findViewById(R.id.depositDateField);
            depositDateField.setText(item.getDate());

            EditText descField = (EditText) findViewById(R.id.depositDescriptionField);
            descField.setText(item.getDescription());

            EditText amountField = (EditText) findViewById(R.id.depositAmountField);
            amountField.setText(item.getAmountString());

            EditText acctField = (EditText) findViewById(R.id.depositAccountField);
            acctField.setText(item.getAccount());
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

        if (null == item) {
            if (CREATE_PLANNED_EXPENSE == requestCode) {
                item = Item.createPlannedItem(plan.getPeriod(), desc, amount, acct, new DBItemPersister());
            } else {
                item = Item.createDeposit(plan.getPeriod(), depositDate, desc, amount, acct, new DBItemPersister());
            }
        }
        else {
            item.setDate(depositDate);
            item.setDescription(desc);
            item.setAmount(amount);
            item.setAccount(acct);
        }
        item.persist();

        Intent returnIntent = new Intent();

        returnIntent.putExtra("com.example.jonathon.agilebudgeting.ITEM", item);

        setResult(RESULT_OK, returnIntent);
        finish();
    }


}
