package com.example.jonathon.agilebudgeting;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.GregorianCalendar;

import static android.content.Intent.ACTION_EDIT;
import static android.content.Intent.ACTION_MAIN;
import static android.content.Intent.ACTION_VIEW;

public class EditPlannedExpenseActivity extends AppCompatActivity {

    private Plan plan;
    private Item expense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_planned_expense);

        Intent intent = getIntent();
        String action = intent.getAction();

        plan = (Plan) intent.getSerializableExtra("com.example.jonathon.agilebudgeting.PLAN");
        if (action.equals(ACTION_MAIN)) {
            TextView plannedExpensePeriod = (TextView) findViewById(R.id.plannedExpensePeriodDisplay);
            plannedExpensePeriod.setText(plan.getPeriod().getPeriodStartDate());
            expense = null;
        }
        else if (action.equals(ACTION_EDIT)) {
            expense = (Item) intent.getSerializableExtra("com.example.jonathon.agilebudgeting.ITEM");
            populateFields();
        }

    }

    private void populateFields() {
        if (expense != null) {
            EditText descField = (EditText) findViewById(R.id.plannedExpenseDescriptionField);
            descField.setText(expense.getDescription());

            EditText amountField = (EditText) findViewById(R.id.plannedExpenseAmountField);
            amountField.setText(expense.getAmountString());

            EditText acctField = (EditText) findViewById(R.id.plannedExpenseAccountField);
            acctField.setText(expense.getAccount());
        }
    }

    public void backToPlan(View view) {
        // TODO: Add validation that fields are empty or prompt user for confirmation before going back to the plan
        setResult(RESULT_CANCELED);
        finish();
    }

    public void clearFields(View view) {
        EditText descField = (EditText) findViewById(R.id.plannedExpenseDescriptionField);
        descField.setText("");

        EditText amountField = (EditText) findViewById(R.id.plannedExpenseAmountField);
        amountField.setText("");

        EditText acctField = (EditText) findViewById(R.id.plannedExpenseAccountField);
        acctField.setText("");

    }

    public void saveExpense(View view) {
        EditText descField = (EditText) findViewById(R.id.plannedExpenseDescriptionField);
        String desc = descField.getText().toString();

        EditText amountField = (EditText) findViewById(R.id.plannedExpenseAmountField);
        double amount = Double.parseDouble(amountField.getText().toString());

        EditText acctField = (EditText) findViewById(R.id.plannedExpenseAccountField);
        String acct = acctField.getText().toString();

        if (null == expense) {
            expense = Item.createItem(plan.getPeriod(), desc, amount, acct, new DBItemPersister());
        }
        else {
            expense.setDescription(desc);
            expense.setAmount(amount);
            expense.setAccount(acct);
        }
        expense.persist();

        Intent returnIntent = new Intent();

        returnIntent.putExtra("com.example.jonathon.agilebudgeting.ITEM", expense);

        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
