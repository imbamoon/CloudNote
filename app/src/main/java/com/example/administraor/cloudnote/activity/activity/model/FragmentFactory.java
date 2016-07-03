package com.example.administraor.cloudnote.activity.activity.model;



import android.app.Fragment;

import com.example.administraor.cloudnote.R;
import com.example.administraor.cloudnote.activity.activity.controller.NoteFragment;
import com.example.administraor.cloudnote.activity.activity.controller.SetFragment;

/**
 * Created by zyw on 2016/4/29.
 */
public class FragmentFactory {
    public static Fragment getInstanceByIndex(int index){
        Fragment fragment = null;
        switch (index){
            case R.id.tab_note:
                fragment = new NoteFragment();
                break;
            case R.id.tab_set:
                fragment = new SetFragment();
                break;
        }
        return fragment;
    }
}
