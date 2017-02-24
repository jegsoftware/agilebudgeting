package com.example.jonathon.agilebudgeting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.ParseException;
import java.util.GregorianCalendar;

import static android.content.Intent.ACTION_MAIN;

public class PlanActivity extends AppCompatActivity {

    private static final int CREATE_PLANNED_EXPENSE = 0;
    private static final int CREATE_DEPOSIT = 1;
    private static final int CREATE_ACTUAL_EXPENSE = 2;
    private Plan plan;
    private String planDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        Intent intent = getIntent();
        String action = intent.getAction();
        if (action.equals(ACTION_MAIN)) {
            GregorianCalendar selectedDate = (GregorianCalendar) intent.getSerializableExtra("com.example.jonathon.agilebudgeting.PLAN_DATE");

            plan = Plan.createPlan(getApplicationContext(), selectedDate);
        }
        else
        {
            long planId = intent.getLongExtra("com.example.jonathon.agilebudgeting.PLAN_ID", -1);
            plan = Plan.createPlan(getApplicationContext(), planId);
        }

        EditText planBeginDateField = (EditText) findViewById(R.id.periodBeginDate);
        planDate = plan.getPlanStartDate();
        planBeginDateField.setText(planDate);

        if (plan.isPlanningClosed()) {
            disablePlanning();
        }

        if (plan.isActualsClosed()) {
            disableActuals();
        }

        updateTotals();
    }

    public void addPlannedExpense(View view) {
        saveChanges();
        Intent intent = new Intent(this, EditPlannedExpenseActivity.class);
        intent.setAction(ACTION_MAIN);
        intent.putExtra("com.example.jonathon.agilebudgeting.PLAN_ID", plan.getPlanId());
        startActivityForResult(intent, CREATE_PLANNED_EXPENSE);
    }

    public void addDeposit(View view) {
        saveChanges();
        Intent intent = new Intent(this, EditDepositActivity.class);
        intent.setAction(ACTION_MAIN);
        intent.putExtra("com.example.jonathon.agilebudgeting.PLAN_ID", plan.getPlanId());
        startActivityForResult(intent, CREATE_DEPOSIT);
    }

    public void addActualExpense(View view) {
        saveChanges();
        Intent intent = new Intent(this, EditActualExpenseActivity.class);
        intent.setAction(ACTION_MAIN);
        intent.putExtra("com.example.jonathon.agilebudgeting.PLAN_ID", plan.getPlanId());
        startActivityForResult(intent, CREATE_ACTUAL_EXPENSE);
    }

    public void viewPlannedExpenses(View view) {
        saveChanges();
        Intent intent = new Intent(this, ListActivity.class);
        intent.setAction(ACTION_MAIN);
        intent.putExtra("com.example.jonathon.agilebudgeting.PLAN_ID", plan.getPlanId());
        intent.putExtra("com.example.jonathon.agilebudgeting.LIST_TYPE", "PlannedItem");
        startActivity(intent);
    }

    public void viewDeposits(View view) {
        saveChanges();
        Intent intent = new Intent(this, ListActivity.class);
        intent.setAction(ACTION_MAIN);
        intent.putExtra("com.example.jonathon.agilebudgeting.PLAN_ID", plan.getPlanId());
        intent.putExtra("com.example.jonathon.agilebudgeting.LIST_TYPE", "Deposit");
        startActivity(intent);
    }

    public void viewActualExpenses(View view) {
        saveChanges();
        Intent intent = new Intent(this, ListActivity.class);
        intent.setAction(ACTION_MAIN);
        intent.putExtra("com.example.jonathon.agilebudgeting.PLAN_ID", plan.getPlanId());
        intent.putExtra("com.example.jonathon.agilebudgeting.LIST_TYPE", "ActualItem");
        startActivity(intent);
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {

        if (requestCode == CREATE_PLANNED_EXPENSE) {
            if (resultCode == RESULT_OK) {
                long expenseId = data.getLongExtra("com.example.jonathon.agilebudgeting.ITEM_ID",-1);
                plan.addPlannedItem(PlannedItem.createItem(this, expenseId));
                updateTotals();
            }
        }
        else if (requestCode == CREATE_DEPOSIT) {
            if (resultCode == RESULT_OK) {
                long depositId = data.getLongExtra("com.example.jonathon.agilebudgeting.ITEM_ID",-1);
                Deposit deposit = Deposit.createDeposit(this, depositId);
                plan.addDeposit(deposit);
                updateTotals();
            }
        }
        else if (requestCode == CREATE_ACTUAL_EXPENSE) {
            if (resultCode == RESULT_OK) {
                long expenseId = data.getLongExtra("com.example.jonathon.agilebudgeting.ITEM_ID",-1);
                plan.addActualItem(ActualItem.createActualItem(this, expenseId));
                updateTotals();
            }
        }

    }

    private void updateTotals() {
        TextView totalPlannedSpend = (TextView) findViewById(R.id.totalPlannedSpendDisplay);
        totalPlannedSpend.setText("$" + String.format("%1.2f",plan.getTotalPlannedExpenses()));

        TextView totalDeposits = (TextView) findViewById(R.id.totalDepositsDisplay);
        totalDeposits.setText("$" + String.format("%1.2f",plan.getTotalDeposits()));

        TextView netPlannedAmount = (TextView) findViewById(R.id.expenseDepositsDifferenceDisplay);
        netPlannedAmount.setText("$" + String.format("%1.2f",plan.getNetPlannedAmount()));

        TextView totalActual = (TextView) findViewById(R.id.totalActualSpendDisplay);
        totalActual.setText("$" + String.format("%1.2f",plan.getTotalActualExpenses()));

        TextView netActualAmount = (TextView) findViewById(R.id.planActualDifferenceDisplay);
        netActualAmount.setText("$" + String.format("%1.2f",plan.getNetActualAmount()));
    }

    private void saveChanges() {
        EditText planBeginDateField = (EditText) findViewById(R.id.periodBeginDate);
        String dateInField = planBeginDateField.getText().toString();
        if(!planDate.equals(dateInField)) {
            try {
                plan.setPeriod(dateInField);
                planDate = dateInField;
            } catch (ParseException e) {
                planBeginDateField.setText(planDate);
            }
        }
    }

    public void closePlanning(View view) {
        plan.closePlanning();

        disablePlanning();
    }

    public void closeActuals(View view) {
        plan.closeActuals();

        disableActuals();
    }

    private void disableActuals() {
        Button addActualsButton = (Button) findViewById(R.id.addActualExpenseButton);
        addActualsButton.setEnabled(false);
    }

    private void disablePlanning() {
        EditText planBeginDateField = (EditText) findViewById(R.id.periodBeginDate);
        planBeginDateField.setEnabled(false);

        Button addExpenseButton = (Button) findViewById(R.id.addPlannedExpenseButton);
        addExpenseButton.setEnabled(false);

        Button addDepositButton = (Button) findViewById(R.id.addDepositButton);
        addDepositButton.setEnabled(false);

        Button closeButton = (Button) findViewById(R.id.closePlanningButton);
        closeButton.setEnabled(false);

    }

}
