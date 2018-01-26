package com.example.jonathon.agilebudgeting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;

import static android.content.Intent.ACTION_EDIT;
import static android.content.Intent.ACTION_MAIN;
import static android.content.Intent.ACTION_VIEW;

public class ListActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int EDIT_PLANNED_ITEM = 0;
    private static final int EDIT_DEPOSIT = 1;
    private static final int EDIT_ACTUAL_ITEM = 2;
    protected Plan plan;
    protected String listType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Intent intent = getIntent();
        String action = intent.getAction();

        if (action.equals(ACTION_MAIN)) {
            plan = (Plan) intent.getSerializableExtra("com.example.jonathon.agilebudgeting.PLAN");
            listType = intent.getStringExtra("com.example.jonathon.agilebudgeting.LIST_TYPE");
            populateList();
        }
    }

    protected void populateList() {
        ArrayList<? extends Item> items;
        if ("Deposit".equals(listType)){
            items = plan.getDeposits();
        }
        else if ("PlannedItem".equals(listType)) {
            items = plan.getPlannedItems();
        }
        else if ("ActualItem".equals(listType)){
            items = plan.getActualItems();
        }
        else {
            return;
        }


        LinearLayout listView = (LinearLayout) findViewById(R.id.itemList);
        Iterator<? extends Item> iter = items.iterator();
        while (iter.hasNext())
        {
            LinearLayout itemView = new LinearLayout(listView.getContext());
            itemView.setOrientation(LinearLayout.HORIZONTAL);

            Item curItem = iter.next();
            TextView item = new TextView(itemView.getContext());
            String itemText = curItem.getDescription() + " $" + curItem.getAmountString() + " " + curItem.getAccount();
            if ("Deposit".equals(listType) || "ActualItem".equals(listType)) {
                itemText = curItem.getDate() + " " + itemText;
            }
            Button editButton = new Button(itemView.getContext());
            editButton.setTag(curItem);
            editButton.setOnClickListener(this);
            editButton.setText(R.string.editButtonText);
            editButton.setGravity(1);

            item.setText(itemText);
            item.setGravity(3);

            itemView.addView(item);
            if (    ("PlannedItem".equals(listType) && plan.isPlanningOpen()) ||
                    ("ActualItem".equals(listType)  && plan.isActualsOpen())
                )
            {
                itemView.addView(editButton);
            }
            listView.addView(itemView);
        }
    }

    public void backToPlan(View view) {
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onClick(View v) {
        Item clickedItem = (Item) v.getTag();

        Intent intent;
        int requestCode;

        if ("PlannedItem".equals(listType)) {
            requestCode = EDIT_PLANNED_ITEM;
        }
        else if ("Deposit".equals(listType)) {
            requestCode = EDIT_DEPOSIT;
        }
        else if ("ActualItem".equals(listType)) {
            requestCode = EDIT_ACTUAL_ITEM;
        }
        else {
            return;
        }

        intent = new Intent(this, EditActualExpenseActivity.class);
        intent.setAction(ACTION_EDIT);
        intent.putExtra("com.example.jonathon.agilebudgeting.PLAN", plan);
        intent.putExtra("com.example.jonathon.agilebudgeting.ITEM", clickedItem);
        intent.putExtra("requestCode", requestCode);
        startActivityForResult(intent, requestCode);
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {

        LinearLayout listView = (LinearLayout) findViewById(R.id.itemList);
        listView.removeAllViews();
        populateList();

/*      TODO: Figure out a way to update just the changed item.  Until I do, new values won't show until you leave and return to the list.
        long itemId = data.getLongExtra("com.example.jonathon.agilebudgeting.ITEM_ID",-1);
        Item editedItem;

        if (requestCode == EDIT_PLANNED_ITEM) {
            if (resultCode == RESULT_OK) {
            }
        }
        else if (requestCode == EDIT_DEPOSIT) {
            if (resultCode == RESULT_OK) {
            }
        }
         */

    }

}
