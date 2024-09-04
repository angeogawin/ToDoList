package com.dev.ogawin;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.dev.ogawin.databinding.ActiviteAproposBinding;
import com.dev.ogawin.databinding.ActiviteTodayBinding;


public class ActiviteAPropos extends AppCompatActivity {
    private ActiviteAproposBinding binding;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActiviteAproposBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

}