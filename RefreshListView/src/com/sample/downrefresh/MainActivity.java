package com.sample.downrefresh;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sample.downrefresh.R;
import com.sample.downrefresh.view.RefreshListView;
import com.sample.downrefresh.view.RefreshListView.OnRefreshListener;

public class MainActivity extends Activity {

	/**
	 * 数据的集合
	 */
	private List<String> str;
	/**
	 * 数据适配器对象
	 */
	private TextAdapter adapter;
	private RefreshListView mListview;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
	}
	
	/**
	 * 初始化控件
	 */
	private void initView() {
		//准备数据
		prepareData();
		
		mListview = (RefreshListView) findViewById(R.id.rlv_listview);
		//这里可选择性的添加头布局
		//mListview.addCustomHeaderView(v);
		
		adapter = new TextAdapter();
		mListview.setAdapter(adapter);
		mListview.isEnabledPullDownRefresh(true); // 启用头刷新
		mListview.isEnabledLoadingMore(true); // 启用尾刷新
		//rlv_listview.addHeaderView(v)系统的可以这样加，但是这是我们自己实现的ListView，需要在自定义的ListView中定义
		mListview.setOnRefreshListener(new OnRefreshListener() {
			//释放刷新的时候加载数据
			@Override
			public void onPullDownRefresh() {
				new AsyncTask<Void, Void, Void>() {
					
					@Override
					protected void onPreExecute() {
						// TODO Auto-generated method stub
						super.onPreExecute();
					}

					@Override
					protected Void doInBackground(Void... params) {
						SystemClock.sleep(3000);
						str.add(0, "这是头布局刷出来的数据");
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						adapter.notifyDataSetChanged();
						mListview.hideHeaderView();
						super.onPostExecute(result);
					}
					
				}.execute(new Void[]{});
			}

			//滑动到底部加载更多的数据
			@Override
			public void onLoadingMore() {
				new AsyncTask<Void, Void, Void>() {
					@Override
					protected void onPreExecute() {
						// TODO Auto-generated method stub
						super.onPreExecute();
					}

					@Override
					protected Void doInBackground(Void... params) {
						SystemClock.sleep(5000);
						str.add("这是尾布局刷出来的数据");
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						adapter.notifyDataSetChanged();
						mListview.hideFooterView();
						super.onPostExecute(result);
					}
				}.execute(new Void[]{});
			}
		});
		
	}

	/**
	 * 给ListVeiw准备数据，模拟的
	 */
	private void prepareData() {
		str = new ArrayList<String>();
		for(int i = 0; i < 100; i++) {
			str.add("这是一条来自ListView的消息，编号为：" + i);
		}
	}
	
	/**
	 * ListView的数据适配器
	 * @author Administrator
	 *
	 */
	private class TextAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return str.size();
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView view;
			if(convertView == null) {
				view = new TextView(MainActivity.this);
			} else {
				view = (TextView) convertView;
			}
			view.setTextSize(16);
			view.setTextColor(0xff000000);
			view.setText(str.get(position));
			return view;
		}
		
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
	}
	
}
