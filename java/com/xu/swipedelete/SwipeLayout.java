package com.xu.swipedelete;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v4.widget.ViewDragHelper.Callback;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class SwipeLayout extends FrameLayout {

	private View contentView;// item内容区域的view
	private View deleteView;// delete区域的view
	private int deleteHeight;// delete区域的高度
	private int deleteWidth;// delete区域的宽度
	private int contentWidth;// content区域的宽度
	private ViewDragHelper viewDragHelper;

	//实现构造方法
	public SwipeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();	//初始化方法，然后在构造方法里面都调用一下
	}

	public SwipeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();	//初始化方法，然后在构造方法里面都调用一下
	}

	public SwipeLayout(Context context) {
		super(context);
		init();	//初始化方法，然后在构造方法里面都调用一下
	}

	//用枚举定义2个常量值，用来表示打开和关闭
	enum SwipeState{
		Open,Close;
	}
	//定义一个变量，表示当前的一个状态
	private SwipeState currentState = SwipeState.Close;//当前默认是关闭状态

	//初始化方法，然后在构造方法里面都调用一下
	private void init() {
		viewDragHelper = ViewDragHelper.create(this, callback);
	}

	//不能用幀布局的默认摆放，因为是叠加在一起的，
	// 需要自定义的一个摆放，在左右摆放，要重写方法
	//需要在onFinishInflate回去2个view对象
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		//获取内容区域的view
		contentView = getChildAt(0);
		//获取删除区域的view
		deleteView = getChildAt(1);
	}

	//需要用到子view的宽高，在这个方法里面获取宽高，因为在onmeseru执行完之后在执行这个方法
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		//获取宽高
		deleteHeight = deleteView.getMeasuredHeight();
		deleteWidth = deleteView.getMeasuredWidth();
		contentWidth = contentView.getMeasuredWidth();
	}

	//不能用幀布局的默认摆放，因为是叠加在一起的，
	// 需要自定义的一个摆放，在左右摆放，要重写方法
	//需要在onFinishInflate回去2个view对象
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		// super.onLayout(changed, left, top, right, bottom);//父类的参数删除
		//先摆放内容子控件的位置
		contentView.layout(0, 0, contentWidth, deleteHeight);
		//在摆放删除子控件的位置
		deleteView.layout(contentView.getRight(), 0, contentView.getRight() + deleteWidth, deleteHeight);
	}

	//callback之前要实现这个方法交给他一下
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		//是否应该处理
		boolean result = viewDragHelper.shouldInterceptTouchEvent(ev);

		//为了保证onTouchEvent一定能执行，这里一定要判断一下啊
		//如果当前有打开的，则需要直接拦截，交给onTouch处理
		if(!SwipeLayoutManager.getInstance().isShouldSwipe(this)){
			//先关闭已经打开的layout
			//关闭的操作放在这里，会不卡，因为放下面的话，不断执行，会掉用这里不断的关闭，这里执行一次
			SwipeLayoutManager.getInstance().closeCurrentLayout();

			//返回结果，自己的需求的话，就修改这个result
			//拦截，交给onTouchEvent处理
			result = true;
		}
		
		return result;
	}

	//重复更改UI的代码，不要放在这里，会造成卡顿
	//还要实现这个方法
	private float downX,downY;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//如果当前有打开删除子控件的，则下面的逻辑不能执行
		if(!SwipeLayoutManager.getInstance().isShouldSwipe(this)){
			//如果不能滑动，说明已经有打开的了，并且触摸的不是同一个view，
			// 先关闭已经打开的layout，但是除了关闭已经打开的，当前还要listview不能滑动，需要请求不拦截事件，下面不能执行
			//将onTouchEvent交给这个处理
			requestDisallowInterceptTouchEvent(true);
			//当前还要listview不能滑动，需要请求不拦截事件，下面不能执行
			return true;
		}

		//手指横向移动的时候，不能垂直移动
		//判断手指滑动的方法，到底是横向还是垂直
		//水平方向的距离大于y方向，偏向于水平方法，反之
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			//getRawx();//这个是参考屏幕的坐标，和下面的区别，都是坐标系，就是参考的不一样，
			//先获取按下的坐标,这个是参考当前view的坐标
			downX = event.getX();
			downY = event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			//1.获取x和y方向移动的距离
			float moveX = event.getX();
			float moveY = event.getY();
			float delatX = moveX - downX;//x方向移动的距离
			float delatY = moveY - downY;//y方向移动的距离
			//由于有正负值值之分，要用绝对值
			if(Math.abs(delatX)>Math.abs(delatY)){
				//请求父view不拦截事件
				//表示移动是偏向于水平方向，那么应该SwipeLayout应该处理，请求listview不要拦截，最后还要返回ture，消费掉
				requestDisallowInterceptTouchEvent(true);
			}
			//更新downX，downY，不然记住的是第一次使用的坐标
			downX = moveX;
			downY = moveY;
			break;
		case MotionEvent.ACTION_UP:
			
			break;
		}
		viewDragHelper.processTouchEvent(event);
		return true;
	}

	//ViewDragHelper的回调方法
	private Callback callback = new Callback() {
		//2个子控件都可以监视
		@Override
		public boolean tryCaptureView(View child, int pointerId) {
			return child==contentView||child==deleteView;
		}
		//水平方向的图像什么什么，凡是现在不能限制边界，拖拽范围正好是删除的宽
		@Override
		public int getViewHorizontalDragRange(View child) {
			return deleteWidth;
		}
		//控制移动的，在这里限制移动的边界
		@Override
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			//当前子控件是内容控件
			if(child==contentView){
				if(left>0)left = 0;
				if(left<-deleteWidth)left = -deleteWidth;
				//当前子控件是删除控件
			}else if (child==deleteView) {
				if(left>contentWidth)left = contentWidth;
				if(left<(contentWidth-deleteWidth))left = contentWidth-deleteWidth;
			}
			return left;
		}
		//做伴随移动就在这里
		@Override
		public void onViewPositionChanged(View changedView, int left, int top,int dx, int dy) {
			super.onViewPositionChanged(changedView, left, top, dx, dy);
			//如果当前改变的自控件是，内容控件的话
			if(changedView==contentView){
				//手动移动deleteView
				//之前的left加上本次移动的距离，之前的top加上移动距离，等等
				deleteView.layout(deleteView.getLeft()+dx,deleteView.getTop()+dy,deleteView.getRight()+dx, deleteView.getBottom()+dy);
				//滑动删除页面的话，内容页面也需要移动
			}else if (deleteView==changedView) {
				//手动移动contentView
				contentView.layout(contentView.getLeft()+dx,contentView.getTop()+dy,
						contentView.getRight()+dx, contentView.getBottom()+dy);
			}
			
			//在这里增加判断开和关闭的逻辑
			//内容控件left的值变成0的时候就关闭的。变成负的时候就开了，还需要加判断，当前状态不等于宽
			if(contentView.getLeft()==0 && currentState!=SwipeState.Close){
				//说明应该将state更改为关闭
				currentState = SwipeState.Close;

				//在这里调用下接口回调方法
				//回调接口关闭的方法
				if(listener!=null){
					listener.onClose(getTag());
				}
				
				//说明当前的SwipeLayout已经关闭，需要让Manager清空一下
				SwipeLayoutManager.getInstance().clearCurrentLayout();
				//当前的left，如果等于负的宽，并且当前状态，不等于开打的状态
			}else if (contentView.getLeft()==-deleteWidth && currentState!=SwipeState.Open) {
				//说明应该将state更改为开
				currentState = SwipeState.Open;

				//在这里调用下接口回调方法
				//回调接口打开的方法
				if(listener!=null){
					listener.onOpen(getTag());
				}
				//当前的Swipelayout已经打开，需要让Manager记录一下下，当前对象
				SwipeLayoutManager.getInstance().setSwipeLayout(SwipeLayout.this);
			}
		}
		//这里做缓慢滑动
		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			super.onViewReleased(releasedChild, xvel, yvel);
			if(contentView.getLeft()<-deleteWidth/2){
				//应该打开
				open();
			}else {
				//应该关闭
				close();
			}
		}
	};
	/**
	 * 打开的方法
	 */
	public void open() {
		//平滑滚动内容子控件，从负的删除控件的宽，到0
		viewDragHelper.smoothSlideViewTo(contentView,-deleteWidth,contentView.getTop());
		//刷新
		ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);
	}
	/**
	 * 关闭的方法
	 */
	public void close() {
		//平滑滚动内容子控件，从0，到0
		viewDragHelper.smoothSlideViewTo(contentView,0,contentView.getTop());
		ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);
	};
	//一定要重写这个方法，
	public void computeScroll() {
		//死代码，在继续执行动画，
		if(viewDragHelper.continueSettling(true)){
			//在重新刷新一下
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}

	//用个变量记录一下
	private OnSwipeStateChangeListener listener;
	//提供一个set方法
	public void setOnSwipeStateChangeListener(OnSwipeStateChangeListener listener){
		this.listener = listener;
	}

	/*
	* 打开和关闭的状态需要暴漏给外面，用接口回调
	* 需要做的好的话，还需要把dragen的百分比还要传出去，外界可以根据移动百分比，做拖拽动画
	* */
	public interface OnSwipeStateChangeListener{
		//这里打开或者关闭的时候，还要暴漏一下数据，getTag。因为不确定外面塞什么数据，只能接收int
		void onOpen(Object tag);
		void onClose(Object tag);
	}
	
}
