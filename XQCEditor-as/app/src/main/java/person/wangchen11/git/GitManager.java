package person.wangchen11.git;

public class GitManager {
	private static GitManager mGitManager = null;
	private GitManager(){
	}
	
	public GitManager instance(){
		if(mGitManager==null)
			mGitManager = new GitManager();
		return mGitManager;
	}
	
	public void gitClone(){
		
	}
}
