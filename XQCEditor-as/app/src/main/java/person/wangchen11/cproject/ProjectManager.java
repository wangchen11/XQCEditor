package person.wangchen11.cproject;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;

public class ProjectManager {
	private Context mContext = null;
	private List<ProjectInfo> mActiveProject = new LinkedList<ProjectInfo>();
	
	private ProjectManager(Context context){
		mContext = context;
	}
	
	private static ProjectManager mProjectManager = null;
	
	public static void init(Context context){
		if(mProjectManager==null)
			mProjectManager = new ProjectManager(context);
	}
	
	public static ProjectManager getInstance(){
		return mProjectManager;
	}
	
	public void fileOpen(String file){
		fileOpen(new File(file));
	}
	
	public void fileOpen(File file){
	}
	
	public void fileChange(String file){
		fileChange(new File(file));
	}
	
	public void fileChange(File file){
		
	}
	
	public ProjectInfo activeProject(CProject project){
		return null;
	}
	
	public ProjectInfo getActiveProjectInfo(CProject project){
		if(project==null)
			return null;
		for(ProjectInfo info:mActiveProject){
			if(project.getProjectFilePath().equals(info.getProjectFilePath())){
				return info;
			}
		}
		return null;
	}
	
}
