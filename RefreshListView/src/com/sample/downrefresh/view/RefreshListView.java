package com.sample.downrefresh.view;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.sample.downrefresh.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * ����ˢ�ºͼ��ظ���
 * @author jack-twt
 */
public class RefreshListView extends ListView implements OnScrollListener {

	/** ����ͷ���ֶ��� */
	private LinearLayout mHeaderView; 
	/** ��ӵ��Զ���ͷ���� */
	private View mCustomHeaderView; 
	/** ����ʱy���ƫ���� */
	private int downY = -1; 
	/** ����ͷ���ֵĸ߶� */
	private int mPullDownHeaderViewHeight; 
	/** ����ͷ���ֵ�view���� */
	private View mPullDownHeaderView;
	/** ����ˢ��  */
	private static final int PULL_DOWN = 0;
	/** �ͷ�ˢ�� */
	private static final int RELEASE_REFRESH = 1; 
	/** ����ˢ����.. */
	private static final int REFRESHING = 2;
	/** ��ǰ����ͷ���ֵ�״̬, Ĭ��Ϊ: ����ˢ��״̬ */
	private int currentState = PULL_DOWN; 
	/** ������ת�Ķ��� */
	private RotateAnimation upAnim;
 	/** ������ת�Ķ��� */
 	private RotateAnimation downAnim; 
	/** ͷ���ֵļ�ͷ */
	private ImageView ivArrow; 
	/** ͷ���ֵĽ���Ȧ */
	private ProgressBar mProgressbar;
	/** ͷ���ֵ�״̬ */
	private TextView tvState;
	/** ͷ���ֵ����ˢ��ʱ�� */
	private TextView tvLastUpdateTime; 
	/** ListView����Ļ��y���ֵ */
	private int mListViewYOnScreen = -1; 
	/** ����ˢ�ºͼ��ظ���Ļص��ӿ� */
	private OnRefreshListener mOnRefreshListener; 
	/** �Ų��ֶ��� */
	private View mFooterView;
	/** �Ų��ֵĸ߶� */
	private int mFooterViewHeight; 
	/** �Ƿ����ڼ��ظ�����, Ĭ��Ϊ: false */
	private boolean isLoadingMore = false; 
	/** �Ƿ���������ˢ�� */
	private boolean isEnabledPullDownRefresh = false; 
	/** �Ƿ����ü��ظ��� */
	private boolean isEnabledLoadingMore = false; 

	public RefreshListView(Context context) {
		super(context);
		initHeader();
		initFooter();
	}

	public RefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initHeader();
		initFooter();
	}

	public RefreshListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initHeader();
		initFooter();
	}

	/**
	 * ��ʼ���Ų���
	 */
	private void initFooter() {
		mFooterView = View.inflate(getContext(), R.layout.refresh_footer_view, null);
		mFooterView.measure(0, 0);
		mFooterViewHeight = mFooterView.getMeasuredHeight();
		
		mFooterView.setPadding(0, -mFooterViewHeight, 0, 0);
		this.addFooterView(mFooterView);
		
		// ����ǰListview����һ�������ļ����¼�
		this.setOnScrollListener(this);
	}

	/**
	 * ��ʼ������ˢ��ͷ����
	 */
	private void initHeader() {
		mHeaderView = (LinearLayout) View.inflate(getContext(), R.layout.refresh_header_view, null);
		mPullDownHeaderView = mHeaderView.findViewById(R.id.ll_refresh_header_view_pull_down);
		ivArrow = (ImageView) mHeaderView.findViewById(R.id.iv_refresh_header_view_pull_down_arrow);
		mProgressbar = (ProgressBar) mHeaderView.findViewById(R.id.pb_refresh_header_view_pull_down);
		tvState = (TextView) mHeaderView.findViewById(R.id.tv_refresh_header_view_pull_down_state);
		tvLastUpdateTime = (TextView) mHeaderView.findViewById(R.id.tv_refresh_header_view_pull_down_last_update_time);
		
		tvLastUpdateTime.setText("���ˢ��ʱ��:" + getLastRefreshTime());
		
		// ��������ˢ��ͷ�ĸ߶�.
		mPullDownHeaderView.measure(0, 0);
		// �õ�����ˢ��ͷ���ֵĸ߶�
		mPullDownHeaderViewHeight = mPullDownHeaderView.getMeasuredHeight();
		System.out.println("ͷ���ֵĸ߶�: " + mPullDownHeaderViewHeight);
		
		// ����ͷ����
		mPullDownHeaderView.setPadding(0, -mPullDownHeaderViewHeight, 0, 0);
		
		this.addHeaderView(mHeaderView);
		
		// ��ʼ������
		initAnimation();
	}
	
	/**
	 * ˢ�µĶ���Ч��
	 */
	private void initAnimation() {
		upAnim = new RotateAnimation(
				0, -180, 
				Animation.RELATIVE_TO_SELF, 0.5f, 
				Animation.RELATIVE_TO_SELF, 0.5f);
		upAnim.setDuration(500);
		upAnim.setFillAfter(true);

		downAnim = new RotateAnimation(
				-180, -360, 
				Animation.RELATIVE_TO_SELF, 0.5f, 
				Animation.RELATIVE_TO_SELF, 0.5f);
		downAnim.setDuration(500);
		downAnim.setFillAfter(true);
	}

	/**
	 * ���һ���Զ����ͷ����.������Ӹ��ָ������Զ���ͷ���֣������ֲ�ͼ��
	 * @param v
	 */
	public void addCustomHeaderView(View v) {
		this.mCustomHeaderView = v;
		mHeaderView.addView(v);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downY = (int) ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			if(downY == -1) {
				downY = (int) ev.getY();
			}
			
			// ���û����������ˢ�¹���, ֱ������switch
			if(!isEnabledPullDownRefresh) {
				break;
			}
			
			if(currentState == REFRESHING) {
				// ��ǰ����ˢ����, ����switch
				break;
			}
			
			// �ж���ӵ��ֲ�ͼ�Ƿ���ȫ��ʾ��, ���û����ȫ��ʾ, ����ص�����
			// ��ִ����������ͷ�Ĵ���, ��תswitch���, ִ�и�Ԫ�ص�touch�¼�.
			if(mCustomHeaderView != null) {
				int[] location = new int[2]; // 0λ��x���ֵ, 1λ��y���ֵ

				if(mListViewYOnScreen == -1) {
					// ��ȡListview����Ļ��y���ֵ.
					this.getLocationOnScreen(location);
					mListViewYOnScreen = location[1];
//					System.out.println("ListView����Ļ�е�y���ֵ: " + mListViewYOnScreen);
				}
				
				// ��ȡmCustomHeaderView����Ļy���ֵ.
				mCustomHeaderView.getLocationOnScreen(location);
				int mCustomHeaderViewYOnScreen = location[1];
//				System.out.println("mCustomHeaderView����Ļ�е�y���ֵ: " + mCustomHeaderViewYOnScreen);
				
				if(mListViewYOnScreen > mCustomHeaderViewYOnScreen) {
//					System.out.println("û����ȫ��ʾ.");
					break;
				}
			}
			
			int moveY = (int) ev.getY();
			
			// �ƶ��Ĳ�ֵ
			int diffY = moveY - downY;
			
			/**
			 * ���diffY��ֵ����0, ������ק
			 * ���� ��ǰListView�ɼ��ĵ�һ����Ŀ����������0
			 * �Ž�������ͷ�Ĳ���
			 */
			if(diffY > 0 && getFirstVisiblePosition() == 0) {
				int paddingTop = -mPullDownHeaderViewHeight + diffY;
//				System.out.println("paddingTop: " + paddingTop);
				
				if(paddingTop > 0 && currentState != RELEASE_REFRESH) {
					System.out.println("��ȫ��ʾ��, ���뵽�ͷ�ˢ��");
					currentState = RELEASE_REFRESH;
					refreshPullDownHeaderState();
				} else if(paddingTop < 0 && currentState != PULL_DOWN) {
					System.out.println("������ʾ��, ���뵽����ˢ��");
					currentState = PULL_DOWN;
					refreshPullDownHeaderState();
				}
				
				mPullDownHeaderView.setPadding(0, paddingTop, 0, 0);
				return true;
			}
			break;
		case MotionEvent.ACTION_UP:
			downY = -1;
			
			if(currentState == PULL_DOWN) {
				// ��ǰ״̬������ˢ��״̬, ��ͷ��������.
				mPullDownHeaderView.setPadding(0, -mPullDownHeaderViewHeight, 0, 0);
			} else if(currentState == RELEASE_REFRESH) {
				// ��ǰ״̬���ͷ�ˢ��, ��ͷ������ȫ��ʾ, ���ҽ��뵽����ˢ����״̬
				mPullDownHeaderView.setPadding(0, 0, 0, 0);
				currentState = REFRESHING;
				refreshPullDownHeaderState();
				
				// �����û��Ļص��ӿ�
				if(mOnRefreshListener != null) {
					mOnRefreshListener.onPullDownRefresh();
				}
			}
			break;
		default:
			break;
		}
		return super.onTouchEvent(ev);
	}

	/**
	 * ����currentState��ǰ��״̬, ��ˢ��ͷ���ֵ�״̬
	 */
	private void refreshPullDownHeaderState() {
		switch (currentState) {
		case PULL_DOWN: // ����ˢ��״̬
			ivArrow.startAnimation(downAnim);
			tvState.setText("����ˢ��");
			break;
		case RELEASE_REFRESH: // �ͷ�ˢ��״̬
			ivArrow.startAnimation(upAnim);
			tvState.setText("�ͷ�ˢ��");
			break;
		case REFRESHING: // ����ˢ����
			ivArrow.clearAnimation();
			ivArrow.setVisibility(View.INVISIBLE);
			mProgressbar.setVisibility(View.VISIBLE);
			tvState.setText("����ˢ����..");
			break;
		default:
			break;
		}
	}
	
	/**
	 * ����ͷ����
	 */
	public void hideHeaderView() {
		//��ǰ������ˢ��״̬����Ҫ�Ѽ�ͷ��ʾ������ˢ��״̬��Ϊ����ˢ�£�����ͷ����
		currentState = PULL_DOWN;
		ivArrow.setVisibility(View.VISIBLE);
		mProgressbar.setVisibility(View.INVISIBLE);
		mPullDownHeaderView.setPadding(0, -mPullDownHeaderViewHeight, 0, 0);
		tvLastUpdateTime.setText("���ˢ�µ�ʱ��Ϊ��" + getLastRefreshTime());
	}
	
	/**
	 * ���ؽŲ���
	 */
	public void hideFooterView() {
		mFooterView.setPadding(0, -mFooterViewHeight, 0, 0);
		isLoadingMore = false;
	}
	
	/**
	 * ��ȡ��ǰʱ��, ��ʽΪ: 1990-12-12 12:12:12
	 * @return
	 */
	private String getLastRefreshTime() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(new Date());
	}
	
	/**
	 * ����ˢ�µļ����¼�
	 * @param listener
	 */
	public void setOnRefreshListener(OnRefreshListener listener) {
		this.mOnRefreshListener = listener;
	}
	
	/**
	 * @author jack-twt
	 * ˢ�»ص��ӿ�
	 */
	public interface OnRefreshListener {
		
		/**
		 * ������ˢ��ʱ �����˷���, ʵ�ִ˷�����ץȡ����.
		 */
		public void onPullDownRefresh();
		
		/**
		 * �����ظ���ʱ, �����˷���. 
		 */
		public void onLoadingMore();
		
	}

	/**
	 * ��������״̬�ı�ʱ�����˷���.
	 * scrollState ��ǰ��״̬
	 * 
	 * SCROLL_STATE_IDLE ͣ��
	 * SCROLL_STATE_TOUCH_SCROLL ��������
	 * SCROLL_STATE_FLING ���Ի���(�͵�һ��)
	 * 
	 */
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if(!isEnabledLoadingMore) {
			// ��ǰû�����ü��ظ���Ĺ���
			return;
		}
		
		if(scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_FLING) {
			int lastVisiblePosition = getLastVisiblePosition();
			if((lastVisiblePosition == getCount() -1) && !isLoadingMore) {
				System.out.println("�������ײ���");
				
				isLoadingMore = true;
				
				mFooterView.setPadding(0, 0, 0, 0);
				// �ѽŲ�����ʾ����, ��ListView��������ͱ�
				this.setSelection(getCount());
				
				if(mOnRefreshListener != null) {
					mOnRefreshListener.onLoadingMore();
				}
			}
		}
		
	}

	/**
	 * ������ʱ�����˷���
	 */
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		
	}
	
	/**
	 * �Ƿ���������ˢ�µĹ���
	 * @param isEnabled true ����
	 */
	public void isEnabledPullDownRefresh(boolean isEnabled) {
		isEnabledPullDownRefresh = isEnabled;
	}

	/**
	 * �Ƿ����ü��ظ���
	 * @param isEnabled
	 */
	public void isEnabledLoadingMore(boolean isEnabled) {
		isEnabledLoadingMore  = isEnabled;
	}

	
	
}
