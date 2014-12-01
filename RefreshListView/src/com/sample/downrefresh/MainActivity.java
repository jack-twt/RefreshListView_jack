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
	 * ���ݵļ���
	 */
	private List<String> str;
	/**
	 * ��������������
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
	 * ��ʼ���ؼ�
	 */
	private void initView() {
		//׼������
		prepareData();
		
		mListview = (RefreshListView) findViewById(R.id.rlv_listview);
		//�����ѡ���Ե����ͷ����
		//mListview.addCustomHeaderView(v);
		
		adapter = new TextAdapter();
		mListview.setAdapter(adapter);
		mListview.isEnabledPullDownRefresh(true); // ����ͷˢ��
		mListview.isEnabledLoadingMore(true); // ����βˢ��
		//rlv_listview.addHeaderView(v)ϵͳ�Ŀ��������ӣ��������������Լ�ʵ�ֵ�ListView����Ҫ���Զ����ListView�ж���
		mListview.setOnRefreshListener(new OnRefreshListener() {
			//�ͷ�ˢ�µ�ʱ���������
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
						str.add(0, "����ͷ����ˢ����������");
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

			//�������ײ����ظ��������
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
						str.add("����β����ˢ����������");
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
	 * ��ListVeiw׼�����ݣ�ģ���
	 */
	private void prepareData() {
		str = new ArrayList<String>();
		for(int i = 0; i < 100; i++) {
			str.add("����һ������ListView����Ϣ�����Ϊ��" + i);
		}
	}
	
	/**
	 * ListView������������
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
