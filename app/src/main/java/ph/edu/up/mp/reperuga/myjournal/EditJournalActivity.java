package ph.edu.up.mp.reperuga.myjournal;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class EditJournalActivity extends AppCompatActivity {

    private FloatingActionButton fab_update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_journal);

        fab_update = (FloatingActionButton) findViewById(R.id.fab_update);
        fab_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



            }
        });

    }
}
