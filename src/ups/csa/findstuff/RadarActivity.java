package ups.csa.findstuff;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class RadarActivity extends Activity {

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_radar);
		
	    // Get the message from the intent
	    Intent intent = getIntent();
	    String message = intent.getStringExtra("CHOSE");

	    // Create the text view
	    TextView textView = new TextView(this);
	    textView.setTextSize(40);
	    textView.setText(message);

	    // Set the text view as the activity layout
	    setContentView(textView);

		
	}
	
}
