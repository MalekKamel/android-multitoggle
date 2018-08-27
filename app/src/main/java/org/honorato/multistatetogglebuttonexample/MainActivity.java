package org.honorato.multistatetogglebuttonexample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.annimon.stream.Stream;
import com.sha.kamel.multitogglebutton.MultiToggleButton;
import com.sha.kamel.multitogglebutton.Selected;
import com.sha.kamel.multitogglebutton.ToggleButton;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private MultiToggleButton
            mtb1,
            mtb2,
            mtb3,
            mtb4,
            mtb5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupDynamicButton();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up createTextView, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setupDynamicButton() {
        mtb1 =  findViewById(R.id.mtb1);
        mtb2 =  findViewById(R.id.mtb2);
        mtb3 =  findViewById(R.id.mtb3);
        mtb4 =  findViewById(R.id.mtb4);
        mtb5 =  findViewById(R.id.mtb5);

        mtb1.setOnItemSelectedListener(listener())
                .setLabel("Yes", 0);

        mtb2.setOnItemSelectedListener(listener())
                .setLabelsRes(Arrays.asList(R.string.left, R.string.right))
                .setColorRes(R.color.mtb_green, R.color.mtb_gray);

        mtb3.setOnItemSelectedListener(listener());
        mtb4.setOnItemSelectedListener(listener())
        .maxSelectedItems(2, max -> toast("Can't select more than " + max + " items."));

        mtb5.setOnItemSelectedListener(listener());

        String[] dogs = getResources().getStringArray(R.array.dogs_array);

        mtb5.setItems(dogs, null, new boolean[dogs.length])
                .setOnItemSelectedListener(listener())
                .setPressedColorTextRes(R.color.white)
                .setUnpressedColorTextRes(R.color.white);
    }

    private ToggleButton.OnItemSelectedListener listener(){
        return (toggleButton, item, index, label, selected) -> {
            String msg = "Number " + index + " is " + (selected ? "selected" : "deselected") + ", Label: " + label + ", Selected items" + selectedItemsMsg(toggleButton);
            toast(msg);
        };
    }

    private void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    private String selectedItemsMsg(ToggleButton toggleButton) {
        Selected selected = toggleButton.getSelected();
        String msg;
        if (selected.isAnySelected()){
            if (selected.isSingleItem())
                msg = "One item selected: " + selected.getSingleItemPosition();
            else
                msg = Stream.of(selected.getSelectedPositions())
                        .map(String::valueOf)
                        .reduce((p1, p2) ->  p1 + ", " + p2)
                        .get();

        }
        else msg = "No items selected";
        return msg;
    }
}
