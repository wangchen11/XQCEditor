package person.wangchen11.add;

import java.util.Iterator;
import java.util.LinkedList;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;

public class Game {
	protected static final String TAG="Game";
	protected int mRoundomNumber=4;
	protected Data [][]mData;
	protected LinkedList<Point> mLinkedList=new LinkedList<Point>();
	protected LinkedList<SmashDrawable> mSmashDrawables=new LinkedList<SmashDrawable>();
	
	public long mGameScore=0;
	public long mGameScoreGoal=0;
	public long mNeedAddScore=0;
	public int  mCustoms=0;
	public int  mSmashOfCustoms=0;
	
	private GameData mProState=null;
	
	public Game(int w,int h) {
		newGame(w,h);
	}
	
	public void newGame(int w,int h){
		Log.i(TAG, "newGame");
		mData=new Data[h][w];
		/*
		for(int y=0;y<h;y++){
			for(int x=0;x<w;x++){
				mData[y][x]=new Data(x, y, (int)(Math.random()*mRoundomNumber)+1);
			}
		}*/
		mCustoms=0;
		mGameScore=0;
		mNeedAddScore=0;
		mGameScoreGoal=500;
		nextStation();
	}
	
	public int getHeight(){
		return mData.length;
	}
	public int getWidth(){
		return mData[0].length;
	}
	public boolean hasData(int x,int y){
		if(x<0||y<0||x>=getWidth()||y>=getHeight())
			return false;
		return mData[y][x]!=null;
	}
	public int getData(int x,int y){
		Point lastPoint=getLinkLast();
		if(lastPoint!=null&&lastPoint.x==x&&lastPoint.y==y)
		{
			return sumLinkNumber();
		}
		return mData[y][x].mData;
	}
	public int getColor(int x,int y){
		int data=getData(x, y);
		int tdata=(data^0x55)&0xff;
		int r=tdata|0x80,   g=(tdata<<4)|(tdata>>4)|0x80,   b=g^0xAA|0x80;
		return Color.rgb(r,g,b);
	}
	public static int getColorEx(int data){
		int tdata=(data^0x55)&0xff;
		int r=tdata|0x80,   g=(tdata<<4)|(tdata>>4)|0x80,   b=g^0xAA|0x80;
		return Color.rgb(r,g,b);
	}
	public int getTextColor(int x,int y){
		int data=getData(x, y);
		int tdata=(data^0x55)&0xff;
		int r=tdata|0x80,   g=(tdata<<4)|(tdata>>4)|0x80,   b=g^0xAA|0x80;
		return Color.rgb(255-(r&0xff),255-(g&0xff),255-(b&0xff));
	}
	public PointF getPointF(int x,int y){
		return mData[y][x].mRealPosition;
	}
	public void setData(int x,int y,int data){
		mData[y][x].mData=data;
	}
	
	public void linkTo(int x,int y){
		if(isAnimation())
			return ;
		Point point=new Point(x, y);
		if(hasData(x, y))
		{
			if(mLinkedList.isEmpty())
			{
				mLinkedList.addLast(point);
			}
			else
			if(getLinkLastButOne()!=null && getLinkLastButOne().equals(point))
			{//撤销
				mLinkedList.removeLast();
			}
			else
			if( (!mLinkedList.contains(point)) )
			{
				Point last=mLinkedList.getLast();
				if(mData[y][x].mData==mData[last.y][last.x].mData)
				if( Math.abs(last.x-x)<=1 && Math.abs(last.y-y)<=1 )
				{
					mLinkedList.addLast(point);
				}
			}
		}
		if(mGameListener!=null)
			mGameListener.onLinkedNumberChange();
	}
	
	public LinkedList<Point> getPointLink(){
		return mLinkedList;
	}
	
	public void stopLink(){
		if(isAnimation())
		{
			Log.i(TAG, "isAnimation!");
			return ;
		}
		if(mLinkedList.size()>1){
			backUp();
			mNeedAddScore+=getLinkedScore();
			//int linkNumber=getLinkNumber();
			int linkSum=sumLinkNumber();
			Point point=getLinkLast();
			mData[point.y][point.x].mData=linkSum;
			for(int i=0;i<mLinkedList.size()-1;i++){
				point=mLinkedList.get(i);
				clearPoint(point);
			}
			letDataToNormal();
			mRunNumber=24;
		}
		mLinkedList.clear();
		if(mGameListener!=null)
			mGameListener.onLinkedNumberChange();
	}
	
	public void clearLastLine(){
		clearLine(getHeight()-1);
	}
	
	public void clearLine(int line){
		for(int x=0;x<getWidth();x++){
			clearPoint(new Point(x,line));
		}
	}
	
	public void clearPoint(Point point){
		if(mData[point.y][point.x]!=null){
			mSmashDrawables.add(new SmashDrawable(point.x+0.5f, point.y+0.5f, getWidth(), getHeight(), 1.0f, getColor(point.x, point.y)));
			mData[point.y][point.x]=null;
		}
	}
	
	public int getLinkedScore(){
		if(mLinkedList.size()>1)
		{
			Point point=mLinkedList.getFirst();
			return mData[point.y][point.x].mData*mLinkedList.size()*mLinkedList.size();
		}
		return 0;
	}
	
	public String getLinkedScoreText(){
		if(mLinkedList.size()>1)
		{
			Point point=mLinkedList.getFirst();
			int score = mData[point.y][point.x].mData*mLinkedList.size()*mLinkedList.size();
			return ""+mData[point.y][point.x].mData+"x"+(mLinkedList.size()*mLinkedList.size())+"="+score;
		}
		return "";
	}
	
	private void letDataToNormal(){
		for(int x=0;x<getWidth();x++){
			for(int y=getHeight()-1;y>=0;y--){
				if(!hasData(x, y)){
					Point point=findUp(x,y);
					if(point == null)
						break;
					else{
						mData[y][x]=mData[point.y][point.x];
						mData[point.y][point.x]=null;
					}
				}
			}
		}

		for(int x=0;x<getWidth();x++){
			if(!hasData(x, getHeight()-1)){
				Point point=findRight(x, getHeight()-1);
				if(point == null)
					break;
				else{
					for(int y=0;y<getHeight();y++){
						mData[y][x]=mData[y][point.x];
						mData[y][point.x]=null;
					}
				}
			}
		}
	}
	
	public void nextStation(){
		letDataFull();
		mCustoms++;
		mGameScoreGoal+=mCustoms*500;
		if(mGameListener!=null)
		{
			mGameListener.onCustomsChange(mCustoms);
			mGameListener.onScoreChange(mGameScore, mGameScoreGoal);
		}
		mSmashOfCustoms+=mCustoms*10;
	}
	
	private void letDataFull(){
		letDataToNormal();
		for(int x=0;x<getWidth();x++){
			int num=0;
			for(int y=getHeight()-1;y>=0;y--){
				if(!hasData(x, y)){
					num++;
					mData[y][x]=new Data(x, -num, (int)(Math.random()*mRoundomNumber)+1);
				}
			}
		}
		
		if(isOver()){
			if(mGameListener!=null)
				mGameListener.onGameOver();
		}
		else{
			mRunNumber=24;
		}
	}
	
	public Point getLinkLast(){
		if(mLinkedList.isEmpty())
			return null;
		return mLinkedList.getLast();
	}
	
	public Point getLinkLastButOne(){
		if(mLinkedList.size()>=2){
			return mLinkedList.get(mLinkedList.size()-2);
		}
		return null;
	}
	
	public int getLinkSize(){
		return mLinkedList.size();
	}
	
	public int sumLinkNumber(){
		int sum=0;
		for(int i=0;i<mLinkedList.size();i++){
			Point point=mLinkedList.get(i);
			sum+=mData[point.y][point.x].getNumber();
		}
		return sum;
	}
	/*
	public int getLinkNumber(){
		Point point=getLinkLast();
		return mData[point.y][point.x].getNumber();
	}*/
	
	public PointF getLinkPoint(int index){
		Point point=mLinkedList.get(index);
		return mData[point.y][point.x].getPoint();
	}
	
	private Point findUp(int x,int y){
		for(int i=y-1;i>=0;i--){
			if(mData[i][x]!=null)
				return new Point(x,i);
		}
		return null;
	}

	private Point findRight(int x,int y){
		for(int i=x+1;i<getWidth();i++){
			if(mData[y][i]!=null)
				return new Point(i,y);
		}
		return null;
	}
	
	public LinkedList<SmashDrawable> getDrawable(){
		return mSmashDrawables;
	}
	
	public boolean isAnimation(){
		return mRunNumber>0;
	}
	
	public boolean isOver(){
		for(int y=0;y<mData.length;y++)
		{
			for(int x=0;x<mData[y].length;x++)
			{
				if(hasData(x, y)){
					int data=mData[y][x].mData;
					for(int dy=-1;dy<=1;dy++)
					for(int dx=-1;dx<=1;dx++)
					{
						if(dx==0&&dy==0)
							continue;
						if(hasData(x+dx,y+dy) && data==mData[y+dy][x+dx].mData )
							return false;
					}
				}
			}
		}
		return true;
	}
	
	private int mRunNumber=0;
	public void run(int time){
		Iterator< SmashDrawable> smashIterator=mSmashDrawables.iterator();
		while(smashIterator.hasNext())
		{
			smashIterator.next().run(time);
		}
		/*
		for(int i=0;i<mSmashDrawables.size();i++){
			mSmashDrawables.get(i).run(time);
		}*/
		for(int i=0;i<mSmashDrawables.size();i++){
			if(!mSmashDrawables.get(i).isAlive())
			{
				mSmashDrawables.remove(i);
				i--;
			}
		}
		if(mNeedAddScore>0)
		{
			int toAdd=(int) (mNeedAddScore*0.1f);
			if(toAdd<1)
				toAdd=1;
			mGameScore+=toAdd;
			mNeedAddScore-=toAdd;
			if(mGameListener!=null)
				mGameListener.onScoreChange(mGameScore, mGameScoreGoal);
		}
		if(mSmashOfCustoms>0){
			mSmashOfCustoms--;
			mSmashDrawables.add(new SmashDrawable((float)(Math.random()*getWidth()), (float)(Math.random()*getHeight()), getWidth(), getHeight(), 2.0f,getColorEx((int)(Math.random()*1024))));
			return ;
		}
		mRunNumber--;
		if(mRunNumber<0)
		{
			mRunNumber=-1;
		}
		if(mRunNumber==0){
			for(int y=0;y<mData.length;y++){
				for(int x=0;x<mData[y].length;x++){
					Data data=mData[y][x];
					if(data!=null)
						data.setPoint(x, y);
				}
			}
			if(isOver()){
				if(mGameScore+mNeedAddScore>=mGameScoreGoal)
				{
					if(mGameListener!=null)
						mGameListener.onGamePass();
					Log.i(TAG, "过关:"+mCustoms);
				}else
				{
					if(mGameListener!=null)
						mGameListener.onGameOver();
					//newGame(getWidth(), getHeight());
					//Log.i(TAG, "游戏结束:"+mCustoms);
				}
			}
		}
		else
		{
			for(int y=0;y<mData.length;y++){
				for(int x=0;x<mData[y].length;x++){
					Data data=mData[y][x];
					if(data!=null){
						data.colsTo(x, y);
					}
				}
			}
		}
	}
	
	GameListener mGameListener=null;
	public void setGameListener(GameListener gameListener){
		mGameListener=gameListener;
		if(mGameListener!=null)
		{
			mGameListener.onScoreChange(mGameScore, mGameScoreGoal);
			mGameListener.onCustomsChange(mCustoms);
			mGameListener.onLinkedNumberChange();
		}
	}
	
	public void backUp(){
		mProState=new GameData(this);
	}
	
	public boolean canUnDo(){
		return mProState!=null;
	}
	
	public boolean unDo(){
		if(!canUnDo())
			return false;
		GameData game=mProState;
		mProState=null;
		mRoundomNumber=game.mRoundomNumber;
		mData=game.mData;
		mLinkedList.clear();
		for(int i=0;i<game.mLinkedList.size();i++){
			Point point=game.mLinkedList.get(i);
			if(hasData(point.x, point.y)){
				mSmashDrawables.add(new SmashDrawable(point.x+0.5f, point.y+0.5f, getWidth(), getHeight(), 1.0f, getTextColor(point.x, point.y)));
			}
		}
		mGameScore=game.mGameScore;
		mNeedAddScore=game.mNeedAddScore;
		mGameScoreGoal=game.mGameScoreGoal;
		mCustoms=game.mCustoms;
		mSmashOfCustoms=game.mSmashOfCustoms;
		if(mGameListener!=null)
		{
			mGameListener.onScoreChange(mGameScore, mGameScoreGoal);
			mGameListener.onCustomsChange(mCustoms);
			mGameListener.onLinkedNumberChange();
		}
		backUp();
		return true;
	}
	
	/**
	 * 还原 
	 * @param game
	 * @return
	 */
	public boolean restore(GameData game){
		Log.i(TAG, "newGame");
		mProState=null;
		mRoundomNumber=game.mRoundomNumber;
		mData=game.mData;
		mLinkedList.clear();
		for(int i=0;i<game.mLinkedList.size();i++){
			Point point=game.mLinkedList.get(i);
			if(hasData(point.x, point.y)){
				mSmashDrawables.add(new SmashDrawable(point.x+0.5f, point.y+0.5f, getWidth(), getHeight(), 1.0f, getTextColor(point.x, point.y)));
			}
		}
		mGameScore=game.mGameScore;
		mNeedAddScore=game.mNeedAddScore;
		mGameScoreGoal=game.mGameScoreGoal;
		mCustoms=game.mCustoms;
		mSmashOfCustoms=game.mSmashOfCustoms;
		if(mGameListener!=null)
		{
			mGameListener.onScoreChange(mGameScore, mGameScoreGoal);
			mGameListener.onCustomsChange(mCustoms);
			mGameListener.onLinkedNumberChange();
		}
		backUp();
		return true;
	}
}

class Data{
	int mData;
	PointF mRealPosition=new PointF();
	public Data(float x,float y,int data) {
		mData=data;
		mRealPosition.x=x;
		mRealPosition.y=y;
	}
	public int getNumber(){
		return mData;
	}
	public PointF getPoint(){
		return new PointF(mRealPosition.x,mRealPosition.y);
	}
	public void setPoint(float x,float y){
		mRealPosition.x=x;
		mRealPosition.y=y;
	}
	public void colsTo(int x,int y){
		float add=(x-mRealPosition.x)*0.2f;
		if(Math.abs(add)<0.01f){
			if(Math.abs(add)<=Float.MIN_VALUE)
				add=0f;
			else
				add=Math.signum(add)*0.01f;
		}
		mRealPosition.x+=add;
		
		add=(y-mRealPosition.y)*0.2f;
		if(Math.abs(add)<0.01f){
			if(Math.abs(add)<=Float.MIN_VALUE)
				add=0f;
			else
				add=Math.signum(add)*0.01f;
		}
		mRealPosition.y+=add;
	}
	public Data copy(){
		return new Data(mRealPosition.x, mRealPosition.y, mData);
	}
}

class GameData {
	public int mRoundomNumber=4;
	public Data [][]mData;
	public LinkedList<Point> mLinkedList=new LinkedList<Point>();
	
	public long mGameScore=0;
	public long mGameScoreGoal=0;
	public long mNeedAddScore=0;
	public int  mCustoms=0;
	public int  mSmashOfCustoms=0;
	private GameData(int w,int h){
		mData=new Data[h][w];
	}
	
	public GameData(Game game){
		mRoundomNumber=game.mRoundomNumber;
		mData=new Data[game.getHeight()][game.getWidth()];
		for(int y=0;y<mData.length;y++){
			for(int x=0;x<mData[y].length;x++){
				if(game.hasData(x, y))
					mData[y][x]=game.mData[y][x].copy();
				else
					mData[y][x]=null;
			}
		}
		mLinkedList.clear();
		mLinkedList.addAll(game.mLinkedList);
		mGameScore=game.mGameScore;
		mGameScoreGoal=game.mGameScoreGoal;
		mNeedAddScore=game.mNeedAddScore;
		mCustoms=game.mCustoms;
		mSmashOfCustoms=game.mSmashOfCustoms;
		
	}
	
	public boolean save(SharedPreferences sharedPreferences){
		Editor editor= sharedPreferences.edit();
		editor.clear();
		editor.putInt("h", mData.length);
		editor.putInt("w", mData[0].length);
		editor.putInt("mRoundomNumber", mRoundomNumber );
		editor.putLong("mGameScore", mGameScore);
		editor.putLong("mGameScoreGoal", mGameScoreGoal);
		editor.putLong("mNeedAddScore", mNeedAddScore);
		editor.putInt("mCustoms", mCustoms);
		editor.putInt("mSmashOfCustoms", mSmashOfCustoms);
		for(int y=0;y<mData.length;y++){
			for(int x=0;x<mData[y].length;x++){
				if(mData[y][x]!=null)
					editor.putInt("mData_"+x+"_"+y, mData[y][x].mData);
			}
		}
		editor.putBoolean("hasgame", true );
		return editor.commit();
	}
	public static GameData load(SharedPreferences sharedPreferences){
		int w=0,h=0;
		if(!sharedPreferences.getBoolean("hasgame", false))
			return null;
		w=sharedPreferences.getInt("w", 0);
		h=sharedPreferences.getInt("h", 0);
		if(w==0 || h==0)
			return null;
		GameData gameData=new GameData(w,h);
		gameData.mRoundomNumber=sharedPreferences.getInt("mRoundomNumber", 4 );
		gameData.mGameScore=sharedPreferences.getLong("mGameScore", 0);
		gameData.mGameScoreGoal=sharedPreferences.getLong("mGameScoreGoal", 0);
		gameData.mNeedAddScore=sharedPreferences.getLong("mNeedAddScore", 0);
		gameData.mCustoms=sharedPreferences.getInt("mCustoms", 0);
		gameData.mSmashOfCustoms=sharedPreferences.getInt("mSmashOfCustoms", 0);
		for(int y=0;y<gameData.mData.length;y++){
			for(int x=0;x<gameData.mData[y].length;x++){
				int data=sharedPreferences.getInt("mData_"+x+"_"+y, 0);
				if(data==0)
					gameData.mData[y][x]=null;
				else
					gameData.mData[y][x]=new Data(x, y, data);
			}
		}
		return gameData;
	} 
}

interface GameListener{
	public void onScoreChange(long score,long scoreGoal);
	public void onCustomsChange(int customs);
	public void onGamePass();
	public void onGameOver();
	public void onLinkedNumberChange();
}
