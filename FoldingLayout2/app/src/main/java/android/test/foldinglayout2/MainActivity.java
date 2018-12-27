package android.test.foldinglayout2;

import android.app.Activity;
import android.os.Bundle;

import java.util.ArrayList;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ArrayList<Node> items = new ArrayList<Node>();
        items.add(new Node("Card1", "#3399ff77"));
        items.add(new Node("Card2", "#33ff9977"));
        FoldingLayout previewView = (FoldingLayout) findViewById(R.id.folding_layout);

        previewView.setAdapter(new FolderAdapter(this, items));
    }
}
