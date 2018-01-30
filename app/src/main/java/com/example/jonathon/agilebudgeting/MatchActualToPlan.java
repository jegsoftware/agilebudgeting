package com.example.jonathon.agilebudgeting;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

public class MatchActualToPlan extends AppCompatActivity implements IDataCallback<Item> {

    private Plan plan;
    private Item actualItem;
    private ArrayList<Item> possibleMatches;
    private RelationshipPersisterFragment persisterFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_actual_to_plan);

        Intent intent = getIntent();

        actualItem = (Item) intent.getSerializableExtra("com.example.jonathon.agilebudgeting.ACTUAL_ITEM");
        plan = (Plan) intent.getSerializableExtra("com.example.jonathon.agilebudgeting.PLAN");

        populateList();
        persisterFragment = RelationshipPersisterFragment.getInstance(getFragmentManager(), plan.isStoredInCloud());
    }

    private void populateList() {
        possibleMatches = plan.getPlannedItemsForAccount(actualItem.getAccount());
        ListIterator<Item> iter = possibleMatches.listIterator();

        LinearLayout listView = (LinearLayout) findViewById(R.id.matchingItemsList);

        while (iter.hasNext()) {
            LinearLayout itemView = new LinearLayout(listView.getContext());
            itemView.setOrientation(LinearLayout.HORIZONTAL);

            int index = iter.nextIndex();
            Item curItem = iter.next();

            String itemText = curItem.getDescription() + " $" + curItem.getAmountString() + " " + curItem.getAccount();
            CheckBox checkBox = new CheckBox(itemView.getContext());
            checkBox.setText(itemText);
            checkBox.setChecked(actualItem.hasMatch(curItem));

            TextView itemId = new TextView(itemView.getContext());
            itemId.setText(Integer.toString(index));
            itemId.setVisibility(View.GONE);

            itemView.addView(checkBox, 0);
            itemView.addView(itemId, 1);

            listView.addView(itemView);

        }
    }

    public void saveMatches(View view) {
        // TODO: Support unchecking a previously saved match
        LinearLayout listView = (LinearLayout) findViewById(R.id.matchingItemsList);
        ArrayList<Item> matchedItems = new ArrayList<>();
        int itemCount = listView.getChildCount();
        for (int i=0; i < itemCount; i++) {
            LinearLayout itemView = (LinearLayout) listView.getChildAt(i);
            CheckBox checkBox = (CheckBox) itemView.getChildAt(0);
            if(checkBox.isChecked()) {
                TextView itemIdText = (TextView) itemView.getChildAt(1);
                int plannedItemIndex = Integer.valueOf(itemIdText.getText().toString());
                Item plannedItem = possibleMatches.get(plannedItemIndex);
                matchedItems.add(plannedItem);
            }
        }
        persisterFragment.saveRelationships(actualItem, matchedItems);
        disableFields();
    }

    private void disableFields() {
        LinearLayout listView = (LinearLayout) findViewById(R.id.matchingItemsList);
        int itemCount = listView.getChildCount();
        for (int i=0; i < itemCount; i++) {
            LinearLayout itemView = (LinearLayout) listView.getChildAt(i);
            CheckBox checkBox = (CheckBox) itemView.getChildAt(0);
            checkBox.setEnabled(false);
        }
        Button saveMatchesButton = (Button) findViewById(R.id.saveMatchesButton);
        saveMatchesButton.setEnabled(false);
    }

    @Override
    public void dataLoaded(Item result) {

    }

    @Override
    public void dataSaved(Item result) {
        actualItem = result;
        Intent returnIntent = new Intent();

        setResult(RESULT_OK, returnIntent);
        finish();

    }
}
