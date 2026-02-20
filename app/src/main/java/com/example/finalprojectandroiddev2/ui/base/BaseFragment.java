package com.example.finalprojectandroiddev2.ui.base;

import androidx.fragment.app.Fragment;

import com.example.finalprojectandroiddev2.utils.Constants;
import com.example.finalprojectandroiddev2.utils.Logger;

/**
 * Base class for all Fragments. Provides a consistent log tag and Logger access.
 */
public abstract class BaseFragment extends Fragment {

    protected String getLogTag() {
        return Constants.TAG_UI;
    }

    protected void logD(String msg) {
        Logger.d(getLogTag(), msg);
    }

    protected void logE(String msg) {
        Logger.e(getLogTag(), msg);
    }

    protected void logE(String msg, Throwable tr) {
        Logger.e(getLogTag(), msg, tr);
    }
}
