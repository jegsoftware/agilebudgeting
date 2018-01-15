package com.example.jonathon.agilebudgeting;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import static android.content.Intent.ACTION_EDIT;
import static android.content.Intent.ACTION_MAIN;

public class EditActualExpenseActivity extends AppCompatActivity {

    private static final int CREATE_PLANNED_EXPENSE = 0;
    private static final int CREATE_DEPOSIT = 1;
    private static final int CREATE_ACTUAL_EXPENSE = 2;
    private static final int EDIT_PLANNED_ITEM = 0;
    private static final int EDIT_DEPOSIT = 1;
    private static final int EDIT_ACTUAL_ITEM = 2;
    private static final int MATCH_ACTUAL_PLANNED = 0;
    private Plan plan;
    private Item item;
    private int requestCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_actual_item);

        Intent intent = getIntent();
        String action = intent.getAction();
        requestCode = intent.getIntExtra("requestCode", -1);
        if (EDIT_PLANNED_ITEM == requestCode) {
            requestCode = CREATE_PLANNED_EXPENSE;
        } else if (EDIT_ACTUAL_ITEM == requestCode) {
            requestCode = CREATE_ACTUAL_EXPENSE;
        } else if (EDIT_DEPOSIT == requestCode) {
            requestCode = CREATE_DEPOSIT;
        }

        plan = (Plan) intent.getSerializableExtra("com.example.jonathon.agilebudgeting.PLAN");

        TextView itemPeriod = (TextView) findViewById(R.id.actualExpensePeriodDisplay);
        itemPeriod.setText(plan.getPeriod().getPeriodStartDate());

        TextView itemPeriodLabel = (TextView) findViewById(R.id.actualExpensePeriodLabel);
        Button matchButton = (Button) findViewById(R.id.actualExpenseMatchButton);
        Button saveButton = (Button) findViewById(R.id.actualExpenseUnplannedButton);
        switch (requestCode) {
            case CREATE_PLANNED_EXPENSE:
                TextView dateLabel = (TextView) findViewById(R.id.actualExpenseDateLabel);
                EditText dateField = (EditText) findViewById(R.id.actualExpenseDateField);
                dateLabel.setVisibility(View.GONE);
                dateField.setVisibility(View.GONE);
                matchButton.setVisibility(View.GONE);
                saveButton.setText(R.string.plannedExpenseSaveButton);
                itemPeriodLabel.setText(R.string.plannedExpensePeriodLabel);
                break;
            case CREATE_DEPOSIT:
                matchButton.setVisibility(View.GONE);
                saveButton.setText(R.string.depositSaveButton);
                itemPeriodLabel.setText(R.string.depositPeriodLabel);
                break;
        }

        if (action.equals(ACTION_MAIN)) {
            item = null;
        } else if (action.equals(ACTION_EDIT)) {
            item = (Item) intent.getSerializableExtra("com.example.jonathon.agilebudgeting.ITEM");
            populateFields();
        }

    }

    private void populateFields() {
        if (item != null) {
            EditText dateField = (EditText) findViewById(R.id.actualExpenseDateField);
            dateField.setText(item.getDate());

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
        EditText dateField = (EditText) findViewById(R.id.actualExpenseDateField);
        dateField.setText("");

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
        intent.putExtra("com.example.jonathon.agilebudgeting.PLAN", plan);
        intent.putExtra("com.example.jonathon.agilebudgeting.ACTUAL_ITEM", item);

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
        EditText dateField = (EditText) findViewById(R.id.actualExpenseDateField);
        String date = dateField.getText().toString();

        EditText descField = (EditText) findViewById(R.id.actualExpenseDescriptionField);
        String desc = descField.getText().toString();

        EditText amountField = (EditText) findViewById(R.id.actualExpenseAmountField);
        double amount = Double.parseDouble(amountField.getText().toString());

        EditText acctField = (EditText) findViewById(R.id.actualExpenseAccountField);
        String acct = acctField.getText().toString();

        if (null == item) {
            if (CREATE_PLANNED_EXPENSE == requestCode) {
                item = Item.createPlannedItem(plan.getPeriod(), desc, amount, acct, new DBItemPersister());
            } else if (CREATE_DEPOSIT == requestCode) {
                item = Item.createDeposit(plan.getPeriod(), date, desc, amount, acct, new DBItemPersister());
            } else if (CREATE_ACTUAL_EXPENSE == requestCode) {
                item = Item.createActualItem(plan.getPeriod(), date, desc, amount, acct, new DBItemPersister());
            }
        }
        else {
            item.setDate(date);
            item.setDescription(desc);
            item.setAmount(amount);
            item.setAccount(acct);
        }
        item.persist();
    }

    private void returnToCaller() {
        Intent returnIntent = new Intent();

        returnIntent.putExtra("com.example.jonathon.agilebudgeting.ITEM", item);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

}
