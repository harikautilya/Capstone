package com.example.kautilya.application.base;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public abstract class BaseActivity<T extends ViewDataBinding> extends AppCompatActivity {

    final protected String TAG = this.getClass().getName();
    T viewBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = DataBindingUtil.setContentView(this, getLayoutId());
    }

    @LayoutRes
    protected abstract int getLayoutId();

    public T getViewBinding() {
        return viewBinding;
    }

    protected void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
