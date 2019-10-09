package person.wangchen11.cproject;

import java.io.File;
import java.util.HashSet;
import java.util.Set;


public class ProjectInfo {
	private String mProjectFilePath = null;
	private Set<String> mProSet = new HashSet<String>();
	private boolean mWaitForRefresh = false;
	
	public ProjectInfo(String projectFile) {
		mProjectFilePath = projectFile;
	}
	
	public Set<String> getAllResMap(){
		Set<String> set = new HashSet<String>();
		CProject project = CProject.LoadProject(new File(mProjectFilePath));
		if(project==null)
			return set;
		set.add(getFileKeyString(project.getManifestPath()));
		return set;
	}

	private String getFileKeyString(String str){
		return getFileKeyString(new File(str));
	}
	
	private String getFileKeyString(File file){
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(file.getPath());
		stringBuilder.append("&");
		stringBuilder.append(file.length());
		stringBuilder.append("&");
		stringBuilder.append(file.lastModified());
		return stringBuilder.toString();
	}
	
	public String getProjectFilePath(){
		return mProjectFilePath;
	}
}
