package com.liuwei.tipsme;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.liuwei.tipsme.common.datetimepicker.DateTimePickDialogUtil;
import com.liuwei.tipsme.models.Tip;
import com.liuwei.tipsme.models.TipStatus;

import java.text.SimpleDateFormat;


public class CreateTipActivity extends AppCompatActivity {
    private EditText tipStartTime;
    private EditText tipContent;
    private RadioGroup selectStatus;
    private RadioButton statusTodo;
    private RadioButton statusDoing;
    private RadioButton statusDone;
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("yyyyy年MM月dd日 HH:mm");
    private ImageView imageViewCreateDone;
    private TipStatus selectedStatus;

    private void initViews(){
        java.util.Date date = new java.util.Date();
        statusTodo = (RadioButton) findViewById(R.id.radioButtonSelectStatusTodoCreate);
        statusDoing = (RadioButton) findViewById(R.id.radioButtonSelectStatusDoingCreate);
        statusDone = (RadioButton) findViewById(R.id.radioButtonSelectStatusDoneCreate);
        selectStatus = (RadioGroup) findViewById(R.id.radioGroupSelectStatusCreate);
        tipContent = (EditText)findViewById(R.id.tipContent);
        imageViewCreateDone = (ImageView)findViewById(R.id.imageViewCreateDone);
        tipStartTime = (EditText) findViewById(R.id.tipStartTime);
    }
    private void initValues(){
        selectedStatus = TipStatus.TODO;
        statusTodo.setSelected(true);
        tipStartTime.setText(timeFormat.format(new java.util.Date()));
    }
    private void bindEvents(){
        selectStatus.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == statusTodo.getId()) {
                    selectedStatus = TipStatus.TODO;
                }
                if (checkedId == statusDoing.getId()) {
                    selectedStatus = TipStatus.DOING;
                }
                if (checkedId == statusDone.getId()) {
                    selectedStatus = TipStatus.DONE;
                }
            }
        });
        imageViewCreateDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = tipContent.getText().toString();
                String startTime = tipStartTime.getText().toString();

                Tip tip = new Tip(content);
                tip.setPlanToStartTimeFromChinese(startTime);
                tip.setStatus(selectedStatus);
                tip.Create();
                finish();
            }
        });
        tipStartTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
                        CreateTipActivity.this, timeFormat.format(new java.util.Date()));
                dateTimePicKDialog.dateTimePicKDialog(tipStartTime);

            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tip);

        initViews();
        initValues();
        bindEvents();
    }
}
