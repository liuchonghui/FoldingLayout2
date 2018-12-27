package android.test.foldinglayout2;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View topicLayout = findViewById(R.id.topic_layout);
        findViewById(R.id.title_bar_text1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "title_bar_text1", Toast.LENGTH_LONG).show();
            }
        });
        findViewById(R.id.title_bar_text2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "title_bar_text2", Toast.LENGTH_LONG).show();
            }
        });
        findViewById(R.id.title_bar_text3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "title_bar_text3", Toast.LENGTH_LONG).show();
            }
        });



        ArrayList<Node> items = new ArrayList<Node>();
        items.add(new Node("Card1", "#3399ff77"));
        items.add(new Node("Card2", "#33ff9977"));
        FoldingLayout previewView = (FoldingLayout) findViewById(R.id.folding_layout);
        previewView.setTopicBlank(topicLayout);
        previewView.setAdapter(new FolderAdapter(this, items));
    }
}
