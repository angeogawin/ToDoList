package com.dev.ogawin;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

public class ViewHolder {
    RadioButton titleRadio;
    EditText titleEdittext;
    TextView subtitleText;
    TextView exclamationMark;

    public ViewHolder(View view) {
        titleRadio = (RadioButton) view.findViewById(R.id.title_radiobutton);
        titleEdittext = (EditText) view.findViewById(R.id.title_edittext);
        subtitleText = (TextView) view.findViewById(R.id.subtitle);
        exclamationMark = (TextView) view.findViewById(R.id.exclamationMark);
        view.setTag(this);
    }
}
