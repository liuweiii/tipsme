package com.liuwei.tipsme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.liuwei.tipsme.codedefine.ActivityReturnCode;
import com.liuwei.tipsme.models.TipStatus;

public class SelectStatusActivity extends AppCompatActivity {
    private int index;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_status);
        RadioGroup selectStatus = (RadioGroup) findViewById(R.id.radioGroupSelectStatus);
        final RadioButton statusTodo = (RadioButton) findViewById(R.id.radioButtonSelectStatusTodo);
        final RadioButton statusDoing = (RadioButton) findViewById(R.id.radioButtonSelectStatusDoing);
        final RadioButton statusDone = (RadioButton) findViewById(R.id.radioButtonSelectStatusDone);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        this.index = bundle.getInt("index");

        selectStatus.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Intent data = new Intent();
                if (checkedId == statusTodo.getId()) {
                    data.putExtra("status", TipStatus.TODO.name());
                }
                if (checkedId == statusDoing.getId()) {
                    data.putExtra("status", TipStatus.DOING.name());
                }
                if (checkedId == statusDone.getId()) {
                    data.putExtra("status", TipStatus.DONE.name());
                }
                data.putExtra("index", index);
                setResult(ActivityReturnCode.SELECT_STATUS, data);
                finish();
            }
        });
    }
}
