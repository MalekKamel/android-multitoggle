package org.honorato.multistatetogglebuttonexample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.sha.kamel.multitogglebutton.MultiToggleButton;
import com.sha.kamel.multitogglebutton.ToggleButton;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

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
        MultiToggleButton mtb1 =  this.findViewById(R.id.mtb1);
        MultiToggleButton mtb2 =  this.findViewById(R.id.mtb2);
        MultiToggleButton mtb3 =  this.findViewById(R.id.mtb3);
        MultiToggleButton mtb4 =  this.findViewById(R.id.mtb4);
        MultiToggleButton mtb5 =  this.findViewById(R.id.mtb5);

        mtb1.setOnItemSelectedListener(listener())
        .setLabel("Yes", 0);

        mtb2.setOnItemSelectedListener(listener())
                .setLabelsRes(Arrays.asList(R.string.left, R.string.right))
                .setColorRes(R.color.mtb_green, R.color.mtb_gray);

        mtb3.setOnItemSelectedListener(listener());
        mtb4.setOnItemSelectedListener(listener());

        mtb5.setOnItemSelectedListener(listener());

        String[] dogs = getResources().getStringArray(R.array.dogs_array);

        mtb5.setItems(dogs, null, new boolean[dogs.length])
                .setOnItemSelectedListener(listener())
        .setPressedColorTextRes(R.color.white)
        .setUnpressedColorTextRes(R.color.white);
    }

    private ToggleButton.OnItemSelectedListener listener(){
        return (item, index, label, selected) -> {
            String msg = index + " is " + (selected ? "selected" : "deselected") + ", Label: " + label;
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        };
    }
}
