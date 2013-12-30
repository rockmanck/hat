package ru.game.hat;

import ru.game.hat.support.Level;
import ru.game.hat.util.PreferencesUtil;
import ru.game.hat.util.StringUtils;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;

import static ru.game.hat.util.CollectionUtils.*;

public class MainActivity extends BaseActivity implements OnItemSelectedListener {
	public static String PREFERENCES_FILE_KEY = "rugamehat_preferences_ui";
	private PreferencesUtil prefs;
	private String typeKey;
	private SparseIntArray playerControls = new SparseIntArray();
	private int playerControlsSize;
	
	private SparseArray<String> players = new SparseArray<String>();
	private int playersCount = 0;
	
	private final class PlayerTextChangeListener implements TextWatcher {
		private final int id;
		public PlayerTextChangeListener(int id) {
			this.id = id;
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			final String name = s.toString();
			
			if (StringUtils.isEmpty(name)) {
				removePlayer(id);
				return;
			}
			
			savePlayer(id, name);
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		prefs = new PreferencesUtil(this);
		typeKey = getString(R.string.preferences_game_type);
		
		setContentView(R.layout.activity_main);
		initLevelSpinner();
		
		// TODO: select preferred game type
		int type = -1;//prefs.get(typeKey, Integer.class); TODO remember text, not element id
		if (type == -1) {
			type = R.id.roundRadio;
			prefs.set(typeKey, type);
		}
		final RadioButton radio = view(type);
		radio.setChecked(true);
		if (type == R.id.roundRadio) hide(R.id.teamHint);
		// disable game type choice
		RadioButton r = (RadioButton)view(R.id.teamRadio);
		r.setEnabled(false);
		r = (RadioButton)view(R.id.roundRadio);
		r.setEnabled(false);
		
		// select preferred level 
		final Spinner spinner = view(R.id.levelSpinner);
		final String levelKey = getString(R.string.preferences_level);
		Integer levelPos = prefs.get(levelKey, Integer.class);
		if (levelPos == -1) {
			levelPos = 0;
			prefs.set(levelKey, levelPos);
		}
		spinner.setSelection(levelPos);
		
		final LinearLayout controls = view(R.id.playersContainer);
		for (int i = 0; i < controls.getChildCount(); i += 1) {
			final LinearLayout child = (LinearLayout)controls.getChildAt(i);
			playerControls.put(i, child.getId());
			assignPlayerListener(i, child.getChildAt(0).getId());
		}
		playerControlsSize = playerControls.size();
		
		restorePlayers();
	}
	
	/**
	 * Restores players from prefs. <br>
	 * Assigns text watcher for inputs. <br>
	 * Disabled start button if there is no player or count of players not even for Team game.
	 */
	private void restorePlayers() {
		int i = 0;
		final String playerKey = getString(R.string.preferences_player);
		String playerName = prefs.get(playerKey + i, String.class);
		while (playerName != null && !playerName.isEmpty()) {
			playersCount += 1;
			players.put(i, playerName);
			final LinearLayout container = view(playerControls.get(i));
			show(container.getId());
			
			final EditText edit = (EditText)container.getChildAt(0);
			edit.setText(playerName);
			
			i += 1;
			playerName = prefs.get(playerKey + i, String.class);
		}
		
		// TODO: don't start game without players
		if (players.size() == 0) {
			startButtonEnabled(false);
		}
	}
	
	private void startButtonEnabled(boolean val) {
		final Button startButton = view(R.id.startButton);
		startButton.setEnabled(true);// TODO: fix to val
	}
	
	private void assignPlayerListener(int id, int inputId) {
		final EditText playerInput = view(inputId);
		playerInput.addTextChangedListener(new PlayerTextChangeListener(id));
	}
	
	private void savePlayer(int id, String name) {
		players.put(id, name);
		prefs.set(getString(R.string.preferences_player) + id, name);
	}
	
	private void removePlayer(int id) {
		players.remove(id);
		prefs.set(getString(R.string.preferences_player) + id, "");
	}

	private void initLevelSpinner() {
		final Spinner spinner = view(R.id.levelSpinner);
		spinner.setOnItemSelectedListener(this);
		
		// Create an ArrayAdapter using the string array and a default spinner layout
		final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.levelsArray, android.R.layout.simple_spinner_item);
		
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void onTypeChange(View view) {
		boolean checked = ((RadioButton) view).isChecked();
		
		final int id = view.getId();
		prefs.set(typeKey, id);
		if (checked && id == R.id.roundRadio) {
			hide(R.id.teamHint);
		} else if (checked && id == R.id.teamRadio) {
			show(R.id.teamHint);
		}
	}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		prefs.set(getString(R.string.preferences_level), pos);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		parent.setSelection(0);
	}
	
	public void startGame(View view) {
		final Intent intent = new Intent(this, ru.game.hat.GameActivity.class);
		
		final Bundle bundle = new Bundle();
		bundle.putString(getString(R.string.game_players), join(sparseValues(players), ","));
		bundle.putString(getString(R.string.game_level), parseLevel());
		bundle.putInt(getString(R.string.game_word_count), 80);
		
		intent.putExtra(getString(R.string.game_bundle), bundle);
		startActivity(intent);
	}
	
	private String parseLevel() {
		final Spinner spinner = view(R.id.levelSpinner);
		String spinnerVal = spinner.getSelectedItem().toString();
		if (spinnerVal.equalsIgnoreCase(getString(R.string.level_easy))) {
			return Level.LOW.name();
		} else if (spinnerVal.equalsIgnoreCase(getString(R.string.level_hard))) {
			return Level.HIGH.name();
		} else {
			return Level.NORMAL.name();
		}
	}
	
	public void addPlayer(View view) {
		show(playerControls.get(playersCount));
		playersCount += 1;
		if (playersCount == playerControlsSize) {
			final Button newPlayerButton = view(R.id.addUserButton);
			newPlayerButton.setEnabled(false);
		}
	}
	
	public void delPlayer(View view) {
		final LinearLayout parent = (LinearLayout) view.getParent();
		final LinearLayout container = (LinearLayout) parent.getParent();
		final int playerControlIndex = container.indexOfChild(parent);
		playersCount -= 1; // TODO: fix middle deletion
		hide(playerControls.get(playerControlIndex));
		if (playersCount < playerControlsSize) {
			final Button newPlayerButton = view(R.id.addUserButton);
			newPlayerButton.setEnabled(true);
		}
		removePlayer(playerControlIndex);
	}
	
	@Override
	public void onBackPressed() {}
}
