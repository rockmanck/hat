package ru.game.hat;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class ResultActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);
		
		final Intent intent = getIntent();
		final Bundle bundle = intent.getBundleExtra(getString(R.string.game_bundle));
		final String resultString = bundle.getString(getString(R.string.game_results));		
		TextView resultView = view(R.id.resultsBox);
		resultView.setText(formatResults(resultString));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.result, menu);
		return true;
	}

	public void startNew(View view) {
		final Intent intent = new Intent(this, ru.game.hat.MainActivity.class);
		startActivity(intent);
	}
	
	private String formatResults(String raw) {
		final String sep = getString(R.string.line_sep);
		final StringBuilder sb = new StringBuilder();
		
		for (String val : raw.split(getString(R.string.result_sep))) {
			final String[] split = val.split(getString(R.string.result_value_sep));
			sb.append(split[0]).append(": ").append(split[1]).append(sep);
		}
		
		return sb.toString();
	}
	
	@Override
	public void onBackPressed() {}
}
