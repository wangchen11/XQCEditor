package person.wangchen11.questions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import android.content.Context;

public class Question {
	private String mKey = null;
	public String mAssetsPath = null;
	public String mTitle = null;
	public int    mPoint = 0;
	public int    mDifficulty = 0;
	public ArrayList<String> mImages = new ArrayList<String>();
	public int mMarks = 0;
	
	public String getQuestion(Context context){
		try {
			return getAssetsText(context,mAssetsPath+"_questions.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public Question(Context context,String assrtsPath,String queskey) throws IOException {
		mAssetsPath = assrtsPath;
		mKey = queskey;

		Scanner scanner = new Scanner(getAssetsText(context,assrtsPath+"_base.txt"));
		while(scanner.hasNextLine()){
			String line = scanner.nextLine();
			int index = line.indexOf(":");
			if(index<0)
				continue;
			String key = line.substring(0,index);
			String value = line.substring(index+1,line.length());
			if(key.equals("mTitle")){
				mTitle = value;
			}
			if(key.equals("mPoint")){
				try {
					mPoint = Integer.parseInt(value);
				} catch (Exception e) {
				}
			}
			if(key.equals("mDifficulty")){
				try {
					mDifficulty = Integer.parseInt(value);
				} catch (Exception e) {
				}
			}
			if(key.equals("mImages")){
				String[] images = value.split(",");
				if(images!=null){
					for(String image:mImages){
						mImages.add(image);
					}
				}
			}
		}
		scanner.close();
	}
	
	public static String getAssetsText(Context context,String path) throws IOException{
		Scanner scanner = null;
		if(path.startsWith("/"))
			scanner = new Scanner(new File(path));
		else
			scanner = new Scanner(context.getAssets().open(path));
			
		
		StringBuilder stringBuilder = new StringBuilder();
		while(scanner.hasNextLine()){
			stringBuilder.append(scanner.nextLine());
			if(scanner.hasNextLine())
				stringBuilder.append("\n");
		}
		scanner.close();
		return stringBuilder.toString();
	}
	
    public static String stripEnd(String str, String stripChars) {
        int end;
        if (str == null || (end = str.length()) == 0) {
            return str;
        }
        if (stripChars == null) {
            while ((end != 0) && Character.isWhitespace(str.charAt(end - 1))) {
                end--;
            }
        } else if (stripChars.length() == 0) {
            return str;
        } else {
            while ((end != 0) && (stripChars.indexOf(str.charAt(end - 1)) != -1)) {
                end--;
            }
        }
        return str.substring(0, end);
    }
    
	public int getInputCount(){
		return 10;
	}
	
	public String getInput(Context context,int index){
		String str = "";
		try {
			str = getAssetsText(context, mAssetsPath+"input"+(index+1)+".txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stripEnd(str.replaceAll("\r\n", "\n"), null);
	}
	
	public String getOutput(Context context,int index){
		String str = "";
		try {
			str = getAssetsText(context, mAssetsPath+"output"+(index+1)+".txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
		str.replaceAll("\r\n", "\n");
		return stripEnd(str, null);
	}
	
	public String getAnwser(Context context){
		try {
			return getAssetsText(context, mAssetsPath+"_answer.c");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public int getFullMarks(){
		return 100;
	}
	
	public void setMarks(int marks){
		mMarks = marks;
	}
	
	public int getMarks(){
		return mMarks;
	}
	
	public String getKey(){
		return mKey;
	}
}
