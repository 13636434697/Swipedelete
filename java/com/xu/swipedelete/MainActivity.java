package com.xu.swipedelete;


import java.util.ArrayList;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private ListView listview;
    //准备数据
    private ArrayList<String> list = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listview = (ListView) findViewById(R.id.listview);
        //1.准备数据
        for (int i = 0; i < 30; i++) {
            //遍历了30个字符串
            list.add("name - "+i);
        }
        listview.setAdapter(new MyAdapter());


        //给listView加了滚动监听，因为点住拖住的话，删除view不关闭了，要判断当前是否打开，垂直方向移动要关闭，水平不能关闭
        listview.setOnScrollListener(new OnScrollListener() {
            //这个是状态改变调用的方法
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //状态传进来了，有三个值，快速滑松开，闲置的时间，触摸时的滚动
                //如果触摸滚动的时候在操作
                if(scrollState==OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    //如果垂直滑动，则需要关闭已经打开的layout
                    SwipeLayoutManager.getInstance().closeCurrentLayout();
                }
            }
            //只要拖动 ，就一直调用这个方法
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    class MyAdapter extends BaseAdapter implements SwipeLayout.OnSwipeStateChangeListener {
        @Override
        public int getCount() {
            return list.size();
        }
        @Override
        public Object getItem(int position) {
            return null;
        }
        @Override
        public long getItemId(int position) {
            return 0;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView==null){
                convertView=View.inflate(MainActivity.this, R.layout.adapter_list, null);
            }
            ViewHolder holder = ViewHolder.getHolder(convertView);

            holder.tv_name.setText(list.get(position));


            //外界，如果要监视打开或者关闭，取出SwipeLayout来设置
            //实现谁打开的话，就要获取当前对应的第几个条目
            holder.swipeLayout.setTag(position);
            //外界，如果要监视打开或者关闭，取出SwipeLayout来设置，this上面实现了接口，new一个也可以
            holder.swipeLayout.setOnSwipeStateChangeListener(this);

            return convertView;
        }
        @Override
        public void onOpen(Object tag) {
            Toast.makeText(MainActivity.this,"第"+(Integer)tag+"个打开",Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onClose(Object tag) {
            Toast.makeText(MainActivity.this,"第"+(Integer)tag+"个关闭", Toast.LENGTH_SHORT).show();
        }

    }

    //外界，如果要监视打开或者关闭，取出SwipeLayout来设置
    static class ViewHolder{
        TextView tv_name,tv_delete;
        //加一个变量，作为adapter的一个布局，初始化
        SwipeLayout swipeLayout;
        //构造方法
        public ViewHolder(View convertView){
            tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            tv_delete = (TextView) convertView.findViewById(R.id.tv_delete);
            //加一个变量，作为adapter的一个布局，初始化
            swipeLayout = (SwipeLayout) convertView.findViewById(R.id.swipeLayout);
        }
        public static ViewHolder getHolder(View convertView){
            ViewHolder holder = (ViewHolder) convertView.getTag();
            if(holder==null){
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }
            return holder;
        }
    }
}
