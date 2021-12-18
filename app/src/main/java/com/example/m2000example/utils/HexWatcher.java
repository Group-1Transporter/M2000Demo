package com.example.m2000example.utils;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.widget.TextView;

public class HexWatcher implements TextWatcher {

    private final TextView view;
    private final StringBuilder sb = new StringBuilder();
    private boolean self = false;
    private boolean enabled = false;

    HexWatcher(TextView view) {
        this.view = view;
    }

    void enable(boolean enable) {
        if (enable) {
            view.setInputType(InputType.TYPE_CLASS_TEXT + InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            view.setInputType(InputType.TYPE_CLASS_TEXT + InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        }
        enabled = enable;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!enabled || self)
            return;

        sb.delete(0, sb.length());
        int i;
        for (i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c >= '0' && c <= '9') sb.append(c);
            if (c >= 'A' && c <= 'F') sb.append(c);
            if (c >= 'a' && c <= 'f') sb.append((char) (c + 'A' - 'a'));
        }
        for (i = 2; i < sb.length(); i += 3)
            sb.insert(i, ' ');
        final String s2 = sb.toString();

        if (!s2.equals(s.toString())) {
            self = true;
            s.replace(0, s.length(), s2);
            self = false;
        }
    }
}
