package ups.csa.findstuff;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	//On déclare toutes les variables dont on aura besoin

	Button button0;
	Button button1;
	Button button2;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);


		//On récupère tous les éléments de notre interface graphique grâce aux ID
		button0 = (Button) findViewById(R.id.button0);
		button1 = (Button) findViewById(R.id.button1);
		button2 = (Button) findViewById(R.id.button2);




		button0.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {			
				LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				double longitude = location.getLongitude();
				double latitude = location.getLatitude();		
				
				Intent intent = new Intent("com.google.android.radar.SHOW_RADAR"); 
		        intent.putExtra("latitude", (float) latitude); 
		        intent.putExtra("longitude", (float) longitude); 
		        startActivity(intent);
			}
		});


		button1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, RadarActivity.class);
				intent.putExtra("CHOSE", "clé");
				MainActivity.this.startActivity(intent);
			}
		});


		button2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				MainActivity.this.finish();
				MainActivity.this.onDestroy();
			}
		}
				);

	}

	
	
}
