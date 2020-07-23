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

import android.content.Context;
import android.text.ClipboardManager;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.PopupMenu;
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
		menu.add(0, android.R.string.copy, 0, android.R.string.copy);
		menu.add(0, android.R.string.paste, 0, android.R.string.paste);
		popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem arg0) {
				int id=arg0.getItemId();
				switch (id) {
				case  android.R.string.copy:
					toggleSelectingText();
					break;

				case android.R.string.paste:
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

	
	private View chooseViewToPopMenu(View view)
	{
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
