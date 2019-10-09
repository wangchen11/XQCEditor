package person.wangchen11.add;

import person.wangchen11.xqceditor.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnClickListener{
	private GameView mGameView;
	private TextView mScoreView;
	private TextView mCustomsView;
	private TextView mGoalView;
	private TextView mHighScoreViiew;
	private TextView mNowView;
	private Button   mNewGameButton;
	private Button   mUndoButton;
	private Button   mHelpButton;
	private long mHighScore=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        //Log.i("w", "w:"+metric.widthPixels+" h:"+metric.heightPixels);
        if(metric.heightPixels / (float)metric.widthPixels <= ( 4f/3f + 16f/9f)/2f )
        {
        	//如果屏幕高宽比例过小则将游戏全屏显示 
        	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
	    loadScore();
		setContentView(R.layout.activity_add);
		mGameView=(GameView) findViewById(R.id.gameView1);
		mScoreView=(TextView) findViewById(R.id.textView_score);
		mCustomsView=(TextView) findViewById(R.id.textView_customs);
		mGoalView=(TextView) findViewById(R.id.textView_goal);
		mHighScoreViiew=(TextView) findViewById(R.id.textView_highscore);
		mNowView=(TextView) findViewById(R.id.textView_now);
		mNewGameButton=(Button) findViewById(R.id.button_newgame);
		mUndoButton=(Button) findViewById(R.id.button_undo);
		mHelpButton=(Button) findViewById(R.id.button_help);
		mGameView.setGameListener(new GameListener() {
			@Override
			public void onScoreChange(long score, long scoreGoal) {
				mScoreView.setText(mScoreView.getHint()+""+score);
				mGoalView.setText(mGoalView.getHint()+""+scoreGoal);
				if(score>mHighScore){
					mHighScore=score;
					saveScore();
				}
				mHighScoreViiew.setText(mHighScoreViiew.getHint()+""+mHighScore);
			}
			
			@Override
			public void onGamePass() {
				mGameView.mGame.clearLastLine();
				mGameView.mGame.nextStation();
			}
			
			@Override
			public void onCustomsChange(int customs) {
				mCustomsView.setText(mCustomsView.getHint()+""+customs);
			}
			
			@Override
			public void onGameOver() {
				Log.i("onGameOver", "onGameOver");
				AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
				builder.setCancelable(false);
				builder.setTitle(getResources().getText(R.string._gameover));//("游戏结束！");
				String msg="";
				long score=mGameView.mGame.mGameScore+mGameView.mGame.mNeedAddScore;
				if(score<mGameView.mGame.mGameScoreGoal){
					msg+=getResources().getText(R.string._noenoughscore);
				}
				else{
					msg+=getResources().getText(R.string._nonext);
				}
				msg+=getResources().getText(R.string._score)+""+score;
				builder.setMessage(msg);
				builder.setNegativeButton(getResources().getText(R.string._newGame), new Dialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mGameView.mGame.newGame(mGameView.mGame.getWidth(), mGameView.mGame.getHeight());
					}
				});
				builder.setPositiveButton(getResources().getText(R.string._wait),null);
				builder.show();
			}

			@Override
			public void onLinkedNumberChange() {
				String oldText = mNowView.getText().toString();
				String nowText = mGameView.getGame().getLinkedScoreText();
				if(!oldText.equals(nowText)){
					ScaleAnimation scaleAnimation = new ScaleAnimation(0.6f, 1.0f, 0.6f, 1.0f,ScaleAnimation.RELATIVE_TO_SELF,0.5f,ScaleAnimation.RELATIVE_TO_SELF,0.5f);
					scaleAnimation.setDuration(200);
					mNowView.startAnimation(scaleAnimation);
					mNowView.setText(nowText);
				}
			}

		});
		mNewGameButton.setOnClickListener(this);
		mUndoButton.setOnClickListener(this);
		mHelpButton.setOnClickListener(this);
		loadGame();
	}
	
	private void saveScore(){
		SharedPreferences sharedPreferences= getSharedPreferences("score", 0);
		Editor editor= sharedPreferences.edit();
		editor.putLong("high", mHighScore);
		editor.commit();
	}
	
	private void loadScore(){
		SharedPreferences sharedPreferences= getSharedPreferences("score", 0);
		mHighScore=sharedPreferences.getLong("high", 0L );
	}

	private void saveGame(){
		GameData game=new GameData(this.mGameView.mGame);
		SharedPreferences sharedPreferences= getSharedPreferences("game", 0);
		game.save(sharedPreferences);
	}
	
	private void loadGame(){
		SharedPreferences sharedPreferences= getSharedPreferences("game", 0);
		GameData game=GameData.load(sharedPreferences);
		if(game!=null){
			mGameView.mGame.restore(game);
		}
		
	}
	
	@Override
	protected void onDestroy() {
		saveGame();
		super.onDestroy();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_newgame:
			mGameView.mGame.newGame(mGameView.mGame.getWidth(), mGameView.mGame.getHeight());
			break;
		case R.id.button_help:
			
			break;
		case R.id.button_undo:
			mGameView.mGame.unDo();
			break;
		default:
			break;
		}
	}
}
