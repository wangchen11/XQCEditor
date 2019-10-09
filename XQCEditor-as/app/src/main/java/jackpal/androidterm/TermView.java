/*
 * Copyright (C) 2012 Steven Luo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jackpal.androidterm;

import person.wangchen11.editor.edittext.ViewToShowPopupMenu;
import person.wangchen11.xqceditor.R;
import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.text.ClipboardManager;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Toast;

import jackpal.androidterm.emulatorview.ColorScheme;
import jackpal.androidterm.emulatorview.EmulatorView;
import jackpal.androidterm.emulatorview.TermSession;

import jackpal.androidterm.util.TermSettings;

@SuppressWarnings("deprecation")
public class TermView extends EmulatorView {

    public TermView(Context context, TermSession session, DisplayMetrics metrics) {
        super(context, session, metrics);
    }
    
    @Override
    protected void onCreateContextMenu(ContextMenu menu) {
    	showMenu();
    	super.onCreateContextMenu(menu);
    }

	public void showMenu()
	{
		PopupMenu popupMenu=new PopupMenu(this.getContext(), chooseViewToPopMenu(this) );
		Menu menu=popupMenu.getMenu();
		menu.add(0, R.string.selected_and_copy, 0, R.string.selected_and_copy);
		menu.add(0, android.R.id.paste, 0, android.R.string.paste);
		popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem arg0) {
				int id=arg0.getItemId();
				switch (id) {
				case R.string.selected_and_copy:
					Toast.makeText(getContext(), R.string.into_copy_mode, Toast.LENGTH_SHORT).show();
					toggleSelectingText();
					break;

				case android.R.id.paste:
					ClipboardManager clipboardManager= (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
					CharSequence charSequence=clipboardManager.getText();
					if(charSequence!=null)
						TermView.this.getTermSession().write(charSequence.toString());
					break;

				default:
					break;
				}
				return true;
			}
		});
		popupMenu.show();
	}

	private View getViewToShowPopupMenu(View view)
	{
		if(view instanceof ViewToShowPopupMenu )
		{
			return view;
		}
		if(view instanceof ViewGroup)
		{
			ViewGroup viewGroup=(ViewGroup) view;
			int count=viewGroup.getChildCount();
			for(int i=0;i<count;i++)
			{
				View ret = getViewToShowPopupMenu(viewGroup.getChildAt(i));
				if(ret!=null)
					return ret;
			}
		}
		return null;
	}
	
	private View chooseViewToPopMenu(View view)
	{
		ViewParent parent= view.getParent();
		if(parent!=null&&parent instanceof ViewGroup)
		{
			ViewGroup viewGroup=(ViewGroup) parent;
			View ret = getViewToShowPopupMenu( viewGroup );
			if(ret!=null)
				return ret;
		}
		return this;
	}
	
    public void updatePrefs(TermSettings settings, ColorScheme scheme) {
        if (scheme == null) {
            scheme = new ColorScheme(settings.getColorScheme());
        }

        setTextSize(settings.getFontSize());
        setUseCookedIME(settings.useCookedIME());
        setColorScheme(scheme);
        setBackKeyCharacter(settings.getBackKeyCharacter());
        setAltSendsEsc(settings.getAltSendsEscFlag());
        setControlKeyCode(settings.getControlKeyCode());
        setFnKeyCode(settings.getFnKeyCode());
        setTermType(settings.getTermType());
        setMouseTracking(settings.getMouseTrackingFlag());
    }

    public void updatePrefs(TermSettings settings) {
        updatePrefs(settings, null);
    }
}
