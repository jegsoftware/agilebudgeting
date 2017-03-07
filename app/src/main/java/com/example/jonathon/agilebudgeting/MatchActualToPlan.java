package com.example.jonathon.agilebudgeting;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Iterator;
import java.util.List;

public class MatchActualToPlan extends AppCompatActivity {

    private Plan plan;
    private ActualItem actualItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_actual_to_plan);

        Intent intent = getIntent();

        long actualItemID = intent.getLongExtra("com.example.jonathon.agilebudgeting.ACTUAL_ITEM_ID", -1);
        plan = (Plan) intent.getSerializableExtra("com.example.jonathon.agilebudgeting.PLAN");

        actualItem = ActualItem.createActualItem(actualItemID);

        populateList();

    }

    private void populateList() {
        List<PlannedItem> possibleMatches = plan.getPlannedItemsForAccount(actualItem.getAccount());
        Iterator<PlannedItem> iter = possibleMatches.iterator();

        LinearLayout listView = (LinearLayout) findViewById(R.id.matchingItemsList);

        while (iter.hasNext()) {
            LinearLayout itemView = new LinearLayout(listView.getContext());
            itemView.setOrientation(LinearLayout.HORIZONTAL);

            PlannedItem curItem = iter.next();

            String itemText = curItem.getDescription() + " $" + curItem.getAmountString() + " " + curItem.getAccount();
            CheckBox checkBox = new CheckBox(itemView.getContext());
            checkBox.setText(itemText);
            checkBox.setChecked(actualItem.hasMatch(curItem));

            TextView itemId = new TextView(itemView.getContext());
            itemId.setText(String.valueOf(curItem.getItemId()));
            itemId.setVisibility(View.GONE);

            itemView.addView(checkBox, 0);
            itemView.addView(itemId, 1);

            listView.addView(itemView);

        }
    }

    public void saveMatches(View view) {
        Context context = getApplicationContext();

        LinearLayout listView = (LinearLayout) findViewById(R.id.matchingItemsList);
        int itemCount = listView.getChildCount();
        for (int i=0; i < itemCount; i++) {
            LinearLayout itemView = (LinearLayout) listView.getChildAt(i);
            CheckBox checkBox = (CheckBox) itemView.getChildAt(0);
            if(checkBox.isChecked()) {
                TextView itemIdText = (TextView) itemView.getChildAt(1);
                long plannedItemId = Long.parseLong(itemIdText.getText().toString());
                PlannedItem plannedItem = PlannedItem.createItem(plannedItemId);

                actualItem.addPlannedItem(plannedItem);
            }
        }

        Intent returnIntent = new Intent();

        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
