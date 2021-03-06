package ups.csa.findstuff;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import fr.dgac.ivy.Ivy;
import fr.dgac.ivy.IvyClient;
import fr.dgac.ivy.IvyException;
import fr.dgac.ivy.IvyMessageListener;

public class MainActivity extends Activity {

	private static final String RADAR_APP = "com.google.android.radar";
	private static final String RADAR_LAUNCH = "SHOW_RADAR";
	private static final String ANDROID_MARKET = "market://details?id=";

	private Ivy bus;
	private boolean ivyStarted;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		new IvyBus(getLocalNetwork()).execute();

		setUpButton((Button) findViewById(R.id.wallet), false);
		setUpButton((Button) findViewById(R.id.keys), true);

		((Button) findViewById(R.id.quit))
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						leaveIvy();
						finish();
						onDestroy();
					}
				});

	}

	private void startIvy(String address, int port) {
		try {
			// Starts the bus with a "Ready" message.
			bus = new Ivy("Transmitter", "Ready", null);
			bus.start(address + ":" + port);
			ivyStarted = true;
		} catch (IvyException e) {
			ivyStarted = false;
		}
	}

	private void leaveIvy() {
		if (ivyStarted) {
			bus.stop();
		}
	}

	private void askForCoords(Button button) {
		if (ivyStarted) {
			// Gets the item to find and checks if it is connected.
			final String itemToFind = button.getText().toString();
			if (!bus.getIvyClientsByName(itemToFind).isEmpty()) {
				try {
					bus.bindMsg("^Coords (\\d+\\.\\d+) (\\d+\\.\\d+)$",
							new IvyMessageListener() {

								@Override
								public void receive(IvyClient client,
										String[] args) {
									// Checks if it is the right item.
									if (client.getApplicationName().equals(
											itemToFind)) {
										// Unsubscribes to the bus.
										bus.unBindMsg("^Coords (\\d+\\.\\d+) (\\d+\\.\\d+)$");

										// Gets the item coordinates.
									    double latitude = Double
												.parseDouble(args[0]);
										double longitude = Double
												.parseDouble(args[1]);

										// Displays radar application.
										Intent intent = new Intent(RADAR_APP
												+ "." + RADAR_LAUNCH);
										intent.putExtra("latitude", (float) latitude);
										intent.putExtra("longitude", (float) longitude);										
										startActivity(intent);
									}
								}

							});
					bus.sendMsg("Search " + itemToFind);
				} catch (IvyException e) {
				}
			} else {
				showAlert("Network issue", "The item is not connected to the network right now.\n\nYou must find it yourself, sorry!");
			}
		} else {
			showAlert("Network issue", "You are not connected to the network right now.\n\nPlease connect to it!");
		}
	}

	private void setUpButton(final Button button, final boolean implemented) {
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (implemented) {
					// Checks dependencies.
					if (isAppInstalled(RADAR_APP)) {
						askForCoords(button);
					} else {
						showAppRequestAlert(RADAR_APP);
					}
				} else {
					showAlert("Missing object", "Your item is not referenced right now...");
				}
			}
		});
	}

	private String getLocalNetwork() {
		String network = null;

		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					
					if (!inetAddress.isLoopbackAddress()) {
						NetworkInterface networkInterface = NetworkInterface.getByInetAddress(inetAddress);
						
						for (InterfaceAddress address : networkInterface.getInterfaceAddresses()) {
							if (address.getBroadcast().isSiteLocalAddress()) {
								network = address.getBroadcast().toString().replaceAll("/", "");;
							}
						}
					}
				}
			}
		} catch (SocketException e) {
		}

		return network;
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

	private void showAlert(String title, String message) {
		// Creates the alert properly.
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setIcon(android.R.drawable.ic_dialog_alert);

		// Makes the only button.
		builder.setPositiveButton("OK", null);

		// Displays the alert.
		builder.show();
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

	private class IvyBus extends AsyncTask<Void, Void, Void> {

		private String network;

		public IvyBus(String network) {
			this.network = network;
		}

		@Override
		protected Void doInBackground(Void... params) {
			startIvy(network, Ivy.DEFAULT_PORT);
			return null;
		}

	}

}
