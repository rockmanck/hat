package ru.game.hat;

import ru.game.hat.support.Game;
import ru.game.hat.support.Lang;
import ru.game.hat.support.Level;
import ru.game.hat.support.WordSupport;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.SparseArray;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class GameActivity extends BaseActivity {
	
	private static final int TIMER_PERIOD = 1000;
	private Game game;
	private CountDownTimer timer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		final Intent intent = getIntent();
		final Bundle bundle = intent.getBundleExtra(getString(R.string.game_bundle));
		
		// for test purposes:
		final int wordCount = 20;
		final Level level = Level.NORMAL;
		
//		final int wordCount = bundle.getInt(getString(R.string.game_word_count));
//		final Level level = Level.valueOf(bundle.getString(getString(R.string.game_level)));
		final Lang lang = Lang.RUS;
		final SparseArray<String> players = new SparseArray<String>();
		for (int i = 1; i <= 5; i += 1) {
			players.put(i, "player" + i);
		}
		
		try {
			game = WordSupport.newGame(wordCount, level, lang, this, players);
			
			changePlayer();
			changeWord();
			newTimer();
		} catch (Exception e) {
			throw new RuntimeException("Failed to create game", e);
		}
	}

	private void changeWord() {
		final TextView wordView = view(R.id.wordView);
		wordView.setText(game.nextWord().text());
	}

	private String changePlayer() {
		final TextView playerName = view(R.id.playerName);
		final String name = game.nextPlayer();
		playerName.setText(name);
		return name;
	}
	
	private void newTimer() {
		final TextView timerView = view(R.id.timerText);
		final String defaultTime = getString(R.string.default_timer);
		timerView.setText(defaultTime); // TODO rework for preferences
		final Integer time = Integer.valueOf(defaultTime) * 1000;
		
		timer = new CountDownTimer(time, TIMER_PERIOD) {
			// TODO disable button guessed word for a 5 seconds 
			@Override
			public void onTick(long millisUntilFinished) {
				timerView.setText(String.valueOf(millisUntilFinished / TIMER_PERIOD));
			}
			
			@Override
			public void onFinish() {
				timerView.setText("0");
				hide(R.id.guessButton);
				final String next = changePlayer();
				
				final TextView message = view(R.id.nextPlayerMessage);
				message.setText(getString(R.string.next_player_message) + next);
				show(R.id.nextPlayerMessage);
				show(R.id.nextPlayerButton);
				
				final TextView word = view(R.id.wordView);
				word.setText(" ");
			}
		};
		timer.start();
	}
	
	public void nextPlayer(View view) {
		changeWord();
		hide(R.id.nextPlayerButton);
		hide(R.id.nextPlayerMessage);
		show(R.id.guessButton);
		newTimer();
	}
	
	public void wordGuessed(View view) {
		game.wordGuessed();
		if (game.hasWord()) {
			changeWord();
		} else {
			game.finish();
			timer.cancel();
			gameOver();
		}
	}
	
	private void gameOver() {
		// TODO open game over activity with results
		final Bundle bundle = new Bundle();
		bundle.putSerializable("results", game.getResults());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game, menu);
		return true;
	}

}
