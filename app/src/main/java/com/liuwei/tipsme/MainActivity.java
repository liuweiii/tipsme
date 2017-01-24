package com.liuwei.tipsme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.liuwei.tipsme.codedefine.ActivityRequestCode;
import com.liuwei.tipsme.codedefine.ActivityReturnCode;
import com.liuwei.tipsme.models.Tip;
import com.liuwei.tipsme.models.TipStatus;
import com.mobeta.android.dslv.DragSortListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private DragSortListView listView;
    private SimpleAdapter simleAdapter;
    private List<Map<String, Object>> dataList;

    private ImageView imageButtonCreate;
    private ImageView imageButtonAll;
    private ImageView imageButtonTodo;
    private ImageView imageButtonDoing;
    private ImageView imageButtonDone;

    private int screenHeight;
    private int screenWidth;
    SharedPreferences sp;

    private void initViews() {
        listView = (DragSortListView) findViewById(R.id.listView);
        imageButtonAll = (ImageView) findViewById(R.id.imageButtonAll);
        imageButtonTodo = (ImageView) findViewById(R.id.imageButtonTodo);
        imageButtonDoing = (ImageView) findViewById(R.id.imageButtonDoing);
        imageButtonDone = (ImageView) findViewById(R.id.imageButtonDone);
        imageButtonCreate = (ImageView) findViewById(R.id.imageButtonCreate);
    }

    private void bindEvents() {
        imageButtonAll.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showAll();
            }
        });
        imageButtonTodo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                show(TipStatus.TODO, imageButtonTodo);
            }
        });
        imageButtonDoing.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                show(TipStatus.DOING, imageButtonDoing);
            }
        });
        imageButtonDone.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                show(TipStatus.DONE, imageButtonDone);
            }
        });
        imageButtonCreate.setOnTouchListener(new View.OnTouchListener() {
            int startX;
            int startY;
            boolean lockDown;
            int initStartX;
            int initStartY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lockDown = true;
                        this.startX = (int) event.getRawX();
                        this.startY = (int) event.getRawY();
                        initStartX = this.startX;
                        initStartY = this.startY;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int newX = (int) event.getRawX();
                        int newY = (int) event.getRawY();

                        int dx = newX - this.startX;
                        int dy = newY - this.startY;

                        int l = MainActivity.this.imageButtonCreate.getLeft();
                        int r = MainActivity.this.imageButtonCreate.getRight();
                        int t = MainActivity.this.imageButtonCreate.getTop();
                        int b = MainActivity.this.imageButtonCreate.getBottom();

                        int newT = t + dy;
                        int newB = b + dy;
                        int newL = l + dx;
                        int newR = r + dx;
                        if ((newL < 0) || (newT < 0)
                                || (newR > MainActivity.this.screenWidth)
                                || (newB > MainActivity.this.screenHeight)) {
                            break;
                        }
                        MainActivity.this.imageButtonCreate.layout(newL, newT, newR, newB);
                        this.startX = (int) event.getRawX();
                        this.startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        int lastx = MainActivity.this.imageButtonCreate.getLeft();
                        int lasty = MainActivity.this.imageButtonCreate.getTop();
                        SharedPreferences.Editor editor = MainActivity.this.sp.edit();
                        editor.putInt("lastx", lastx);
                        editor.putInt("lasty", lasty);
                        editor.commit();
                        if (Math.abs(initStartX - startX) < 5 && Math.abs(initStartY - startY) < 5) {
                            Intent intent = new Intent(MainActivity.this, CreateTipActivity.class);
                            startActivityForResult(intent, ActivityReturnCode.CREATE_TIP);
                        }
                        lockDown = false;
                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Point outSize = new Point();
        this.getWindowManager().getDefaultDisplay().getSize(outSize);
        this.screenHeight = outSize.y;
        this.screenWidth = outSize.x;
        this.sp = this.getSharedPreferences("config", Context.MODE_PRIVATE);
        DataBase.InitDB();
        initViews();
        bindEvents();
        showAll();
    }

    private void shadowTipStatusButtons(){
        imageButtonAll.setAlpha(0.3f);
        imageButtonDoing.setAlpha(0.3f);
        imageButtonDone.setAlpha(0.3f);
        imageButtonTodo.setAlpha(0.3f);
    }

    private void show(TipStatus status, ImageView viewButton) {
        shadowTipStatusButtons();
        viewButton.setAlpha(1.0f);
        fillListView(DataBase.GetList(status));
    }


    private void showAll() {
        shadowTipStatusButtons();
        imageButtonAll.setAlpha(1.0f);
        fillListView(DataBase.GetAll());
    }

    private void fillListView(List<Tip> tips) {
        dataList = new ArrayList<Map<String, Object>>();
        for (Tip tip : tips) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", tip.getId());
            map.put("status_drawable", TipStatus.getDrawable(tip.getStatus()));
            map.put("position", tip.getPosition());
            map.put("status", tip.getStatus().name());
            map.put("content", tip.getContent());
            map.put("position", tip.getPosition());
            map.put("startTime", tip.getPlanToStartTimeString());
            dataList.add(map);
        }
        simleAdapter = new SimpleAdapter(this, dataList, R.layout.tips_list,
                new String[]{"id", "status_drawable", "position", "status", "content", "startTime"},
                new int[]{R.id.tipID, R.id.status_drawable, R.id.tipPosition, R.id.status, R.id.tips, R.id.startTime});
        listView.setAdapter(simleAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Intent intent = new Intent(MainActivity.this, SelectStatusActivity.class);
                intent.putExtra("index", arg2);
                startActivityForResult(intent, ActivityReturnCode.SELECT_STATUS);
            }

        });
        listView.setDropListener(new DragSortListView.DropListener() {
            @Override
            public void drop(int from, int to) {
                Map<String, Object> fromData = MainActivity.this.dataList.get(from);
                Map<String, Object> toData = MainActivity.this.dataList.get(to);
                Integer fromPosition = (Integer) fromData.get("position");
                Integer toPosition = (Integer) toData.get("position");
                Tip.SwapPosition(fromPosition, toPosition);
                MainActivity.this.dataList.remove(from);
                MainActivity.this.dataList.add(to, fromData);
                simleAdapter.notifyDataSetChanged();
            }
        });
        listView.setRemoveListener(new DragSortListView.RemoveListener() {
            @Override
            public void remove(int which) {
                Map<String, Object> tip = MainActivity.this.dataList.get(which);
                Integer position = (Integer) tip.get("position");
                Tip.RemoveByPosition(position);
                MainActivity.this.dataList.remove(which);
                simleAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ActivityRequestCode.SELECT_STATUS &&
                resultCode == ActivityRequestCode.SELECT_STATUS) {
            TipStatus status = TipStatus.valueOf(data.getStringExtra("status"));
            Integer index = Integer.valueOf(data.getIntExtra("index", -1));
            Map<String, Object> itemData = this.dataList.get(index);
            itemData.put("status", status.name());
            Tip.SaveStatus((Integer) itemData.get("id"), status);
            updateItem(index, status);
        }
        if(ActivityReturnCode.CREATE_TIP == requestCode){
            showAll();
        }
    }

    private void updateItem(int index, TipStatus status) {
        if (listView == null) {
            return;
        }
        int visiblePosition = listView.getFirstVisiblePosition();
//        View view = listView.getFocusedChild();
        View view = listView.getChildAt(index - visiblePosition);
        ImageView status_drawable = (ImageView) view.findViewById(R.id.status_drawable);
        status_drawable.setImageResource(TipStatus.getDrawable(status));

        simleAdapter = new SimpleAdapter(this, dataList, R.layout.tips_list,
                new String[]{"id", "status_drawable", "position", "status", "content", "startTime"},
                new int[]{R.id.tipID, R.id.status_drawable, R.id.tipPosition, R.id.status, R.id.tips, R.id.startTime});
        listView.setAdapter(simleAdapter);

//        listView.setDragEnabled(!listView.isDragEnabled());
//        simleAdapter.notifyDataSetChanged();
    }
}
