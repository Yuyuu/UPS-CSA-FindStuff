package ups.csa.findstuff;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	private static final String RADAR_APP = "com.google.android.radar";
	private static final String RADAR_LAUNCH = "SHOW_RADAR";
	private static final String ANDROID_MARKET = "market://details?id=";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		setUpButton((Button) findViewById(R.id.wallet), false);
		setUpButton((Button) findViewById(R.id.keys), true);

		((Button) findViewById(R.id.quit)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				MainActivity.this.finish();
				MainActivity.this.onDestroy();
			}
		});

	}
	
	private void setUpButton(Button button, final boolean implemented) {
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (implemented) {
					// Checks dependencies.
					if (isAppInstalled(RADAR_APP)) {
						// Custom code.
						LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
						Location location = lm
								.getLastKnownLocation(LocationManager.GPS_PROVIDER);
						double longitude = location.getLongitude();
						double latitude = location.getLatitude();

						// Displays radar application.
						Intent intent = new Intent(RADAR_APP + "." + RADAR_LAUNCH);
						intent.putExtra("latitude", (float) (latitude + 5));
						intent.putExtra("longitude", (float) (longitude + 5));
						startActivity(intent);
					} else {
						showAppRequestAlert(RADAR_APP);
					}
				} else {
					Intent intent = new Intent(MainActivity.this, RadarActivity.class);
					intent.putExtra("CHOSE", "Not implemented yet.");
					MainActivity.this.startActivity(intent);
				}
			}
		});
	}

	private boolean isAppInstalled(String appPackageName) {
		boolean appInstalled = true;

		try {
			getPackageManager().getPackageInfo(appPackageName,
					PackageManager.GET_ACTIVITIES);
		} catch (PackageManager.NameNotFoundException e) {
			appInstalled = false;
		}

		return appInstalled;
	}

	private void showAppRequestAlert(final String appPackageName) {
		// Creates the alert properly.
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Application required");
		builder.setMessage("This functionality requires another Android application.\n\nPlease install it!");
		builder.setIcon(android.R.drawable.ic_dialog_alert);

		// Makes first button.
		builder.setPositiveButton("Go to Market",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent goToMarket = new Intent(Intent.ACTION_VIEW)
								.setData(Uri.parse(ANDROID_MARKET
										+ appPackageName));
						startActivity(goToMarket);
					}

				});

		// Makes second button.
		builder.setNegativeButton("Cancel", null);
		
		// Displays the alert.
		builder.show();
	}

}
