package com.example.tripmanager2;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ViewTransRequestBlock extends ViewBlock {

    BlockTransRequest data;
    MainActivity mainActivity;
    FragmentAddTrans fragment;

    public ViewTransRequestBlock(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mainActivity = (MainActivity) context;
        fragment = mainActivity.transFrag;
    }

    @Override
    public void SetData(BlockBase _block){
        super.SetData(_block);
        data = (BlockTransRequest)_block;
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.SetData(data.from, data.to);
                mainActivity.ChangeFragment(mainActivity.transFrag);
            }
        });
    }
}
