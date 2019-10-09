package person.wangchen11.window;

public class WindowPointer{
	public Window mWindow; 
	public TitleView mTitleView; 
	public CharSequence mTitle;
	public void changeData(WindowPointer other){
		WindowPointer temp=new WindowPointer();
		temp.mWindow=mWindow;
		temp.mTitleView=mTitleView;
		temp.mTitle=mTitle;
		
		mWindow=other.mWindow;
		mTitleView=other.mTitleView;
		mTitle=other.mTitle;
		
		other.mWindow=temp.mWindow;
		other.mTitleView=temp.mTitleView;
		other.mTitle=temp.mTitle;
	}
}