package com.example.jonathon.agilebudgeting;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import static android.content.Intent.ACTION_EDIT;
import static android.content.Intent.ACTION_MAIN;

public class EditActualExpenseActivity extends AppCompatActivity {

    private static final int MATCH_ACTUAL_PLANNED = 0;
    private Plan plan;
    private ActualItem item;
    private long expenseID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_actual_item);

        Intent intent = getIntent();
        String action = intent.getAction();

        long planId = intent.getLongExtra("com.example.jonathon.agilebudgeting.PLAN_ID", -1);
        plan = Plan.createPlan(planId);
        if (action.equals(ACTION_MAIN)) {
            TextView actualExpensePeriod = (TextView) findViewById(R.id.actualExpensePeriodDisplay);
            actualExpensePeriod.setText(plan.getPeriod().getPeriodStartDate());
            item = null;
        } else if (action.equals(ACTION_EDIT)) {
            long itemId = intent.getLongExtra("com.example.jonathon.agilebudgeting.ITEM_ID", -1);
            item = ActualItem.createActualItem(itemId);
            populateFields();
        }

    }

    private void populateFields() {
        if (item != null) {
            EditText actualExpenseDateField = (EditText) findViewById(R.id.actualExpenseDateField);
            actualExpenseDateField.setText(item.getDate());

            EditText descField = (EditText) findViewById(R.id.actualExpenseDescriptionField);
            descField.setText(item.getDescription());

            EditText amountField = (EditText) findViewById(R.id.actualExpenseAmountField);
            amountField.setText(item.getAmountString());

            EditText acctField = (EditText) findViewById(R.id.actualExpenseAccountField);
            acctField.setText(item.getAccount());
        }
    }

    public void backToPlan(View view) {
        // TODO: Add validation that fields are empty or prompt user for confirmation before going back to the plan
        setResult(RESULT_CANCELED);
        finish();
    }

    public void clearFields(View view) {
        EditText actualExpenseDateField = (EditText) findViewById(R.id.actualExpenseDateField);
        actualExpenseDateField.setText("");

        EditText descField = (EditText) findViewById(R.id.actualExpenseDescriptionField);
        descField.setText("");

        EditText amountField = (EditText) findViewById(R.id.actualExpenseAmountField);
        amountField.setText("");

        EditText acctField = (EditText) findViewById(R.id.actualExpenseAccountField);
        acctField.setText("");

    }

    public void saveUnplannedActualExpense(View view) {
        saveExpense();
        returnToCaller();
    }

    public void findMatchingPlannedExpenseForItem(View view) {

        saveExpense();

        Intent intent = new Intent(this, MatchActualToPlan.class);
        intent.setAction(ACTION_MAIN);
        intent.putExtra("com.example.jonathon.agilebudgeting.PLAN_ID", plan.getPlanId());
        intent.putExtra("com.example.jonathon.agilebudgeting.ACTUAL_ITEM_ID", item.getItemId());

        startActivityForResult(intent, MATCH_ACTUAL_PLANNED);

    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {

        if (requestCode == MATCH_ACTUAL_PLANNED) {
            if (resultCode == RESULT_OK) {
                returnToCaller();
            }
        }

    }

    private void saveExpense() {
        EditText actualExpenseDateField = (EditText) findViewById(R.id.actualExpenseDateField);
        String actualExpenseDate = actualExpenseDateField.getText().toString();

        EditText descField = (EditText) findViewById(R.id.actualExpenseDescriptionField);
        String desc = descField.getText().toString();

        EditText amountField = (EditText) findViewById(R.id.actualExpenseAmountField);
        double amount = Double.parseDouble(amountField.getText().toString());

        EditText acctField = (EditText) findViewById(R.id.actualExpenseAccountField);
        String acct = acctField.getText().toString();

        if (null == item) {
            item = ActualItem.createActualItem(plan.getPlanId(), actualExpenseDate, desc, amount, acct);
        } else {
            item.setDate(actualExpenseDate);
            item.setDescription(desc);
            item.setAmount(amount);
            item.setAccount(acct);
        }

        expenseID = item.persist();
    }

    private void returnToCaller() {
        Intent returnIntent = new Intent();

        returnIntent.putExtra("com.example.jonathon.agilebudgeting.ITEM_ID", expenseID);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

}
