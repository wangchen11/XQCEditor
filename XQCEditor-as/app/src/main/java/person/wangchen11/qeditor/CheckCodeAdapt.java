package person.wangchen11.qeditor;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;

import person.wangchen11.editor.edittext.WarnAndError;
import person.wangchen11.gnuccompiler.CheckInfo;

public class CheckCodeAdapt {
	public static LinkedList<WarnAndError> getCWarnAndErrors(LinkedList<CheckInfo> checkInfos,File curFile){
		LinkedList<WarnAndError> warnAndErrors = new LinkedList<WarnAndError>();
		Iterator<CheckInfo> iterator = checkInfos.iterator();
		while(iterator.hasNext()){
			CheckInfo checkInfo = iterator.next();
			if(curFile.equals(new File(checkInfo.mFilePath)))
				warnAndErrors.addLast(new WarnAndError(checkInfo.mLineAt-1, checkInfo.mType,checkInfo.mMsg));
		}
		return warnAndErrors;
	}
}
