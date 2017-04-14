package com.xu.swipedelete;

/*
* 打开任意一个item要记录打开的状态，打开就不能在打开了，通过集合来记录，因为永远只有一个在打开，管理的类，谁打开记住谁
* 记录变量是所有，静态变量，不推荐这样写，这里新建类，管理当前打开的布局，这里写了设计模式，
* 单例模式，就一个对象，每个swiplayou都可以方法这个对象里面的私有属性。
*
* */
public class SwipeLayoutManager {
	//私有化构造函数
	private SwipeLayoutManager(){}
	//静态的实例对象
	private static SwipeLayoutManager mInstance = new SwipeLayoutManager();
	//返回的是本身的对象
	public static SwipeLayoutManager getInstance(){
		return mInstance;
	}

	private SwipeLayout currentLayout;//用来记录当前打开的SwipeLayout
	//提供一个set方法才能记录
	public void setSwipeLayout(SwipeLayout layout){
		//提供一个方法
		this.currentLayout = layout;
	}
	
	/**在提供一个方法
	 * 清空当前所记录的已经打开的layout
	 */
	public void clearCurrentLayout(){
		currentLayout = null;
	}
	
	/**
	 * 关闭当前已经打开的SwipeLayout
	 */
	public void closeCurrentLayout(){
		if(currentLayout!=null){
			currentLayout.close();
		}
	}
	
	/**
	 * 判断当前是否应该能够滑动，如果没有打开的，则可以滑动。
	 * 如果有打开的，则判断打开的layout和当前按下的layout是否是同一个
	 *
	 * 还需要判断，滑动的是不是当前的布局，需要传一个对象才可以判断
	 * @return
	 */
	public boolean isShouldSwipe(SwipeLayout swipeLayout){
		if(currentLayout==null){
			//说明当前木有打开的layout
			return true;
		}else {
			//说明有打开的layout
			return currentLayout==swipeLayout;
		}
	}
}
