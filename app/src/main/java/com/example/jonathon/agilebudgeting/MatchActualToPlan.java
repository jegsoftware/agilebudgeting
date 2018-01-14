package com.example.jonathon.agilebudgeting;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class MatchActualToPlan extends AppCompatActivity {

    private Plan plan;
    private Item actualItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_actual_to_plan);

        Intent intent = getIntent();

        actualItem = (Item) intent.getSerializableExtra("com.example.jonathon.agilebudgeting.ACTUAL_ITEM");
        plan = (Plan) intent.getSerializableExtra("com.example.jonathon.agilebudgeting.PLAN");

        populateList();

    }

    private void populateList() {
        List<Item> possibleMatches = plan.getPlannedItemsForAccount(actualItem.getAccount());
        Iterator<Item> iter = possibleMatches.iterator();

        LinearLayout listView = (LinearLayout) findViewById(R.id.matchingItemsList);

        while (iter.hasNext()) {
            LinearLayout itemView = new LinearLayout(listView.getContext());
            itemView.setOrientation(LinearLayout.HORIZONTAL);

            Item curItem = iter.next();

            String itemText = curItem.getDescription() + " $" + curItem.getAmountString() + " " + curItem.getAccount();
            CheckBox checkBox = new CheckBox(itemView.getContext());
            checkBox.setText(itemText);
            checkBox.setChecked(actualItem.hasMatch(curItem));

            TextView itemId = new TextView(itemView.getContext());
            itemId.setText(curItem.getItemId().toString());
            itemId.setVisibility(View.GONE);

            itemView.addView(checkBox, 0);
            itemView.addView(itemId, 1);

            listView.addView(itemView);

        }
    }

    public void saveMatches(View view) {
        // TODO: Support unchecking a previously saved match
        LinearLayout listView = (LinearLayout) findViewById(R.id.matchingItemsList);
        int itemCount = listView.getChildCount();
        for (int i=0; i < itemCount; i++) {
            LinearLayout itemView = (LinearLayout) listView.getChildAt(i);
            CheckBox checkBox = (CheckBox) itemView.getChildAt(0);
            if(checkBox.isChecked()) {
                TextView itemIdText = (TextView) itemView.getChildAt(1);
                UUID plannedItemId = UUID.fromString(itemIdText.getText().toString());
                Item plannedItem = Item.createItem(plannedItemId, new DBItemPersister());

                actualItem.addRelatedItem(plannedItem);
            }
        }

        Intent returnIntent = new Intent();

        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
