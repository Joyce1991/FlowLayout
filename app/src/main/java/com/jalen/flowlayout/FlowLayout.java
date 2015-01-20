package com.jalen.flowlayout;


import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
/**
 * <strong>FlowLayout类继承于{@link android.view.ViewGroup}</strong>
 * <p>实现功能：<br/>
 * 		1.根据计算当前行剩余空间是否足够存放一个child view，不足则自动换行<br/>
 * 		2.选中一个tab，则把当前选中tab标识为selected，其他tab标识为unselected<br/>
 * 		3.通过回调机制通知实现了接口{@link com.jalen.flowlayout.FlowLayout.OnCheckedChangeListener}的类当前状态变化</p>
 * @author jalen
 * @version 1.1
 * @since	1.1
 * @date 2015-1-19下午3:38:21
 */
public class FlowLayout extends ViewGroup {
	private static final String TAG = "FlowLayout";
	/**
	 * 标签
	 */
	private static final String[] tabs = {"全部", "快递", "药品监管码", "二维码" , "IntelliJ" ,"IDEA" ,
            "editor" ,"is" ,"a" ,"powerful" ,"tool" ,"for" ,"creating" ,"and" ,"modifying" ,"source" ,"code"};
	/**
	 * 标识当前选中的item的position，mCheckedId={@value}
	 */
	private int mCheckedId = 0;

	public FlowLayout(Context context) {
		this(context, null);
	}

	public FlowLayout(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}

	public FlowLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		
		// 根据标签tabs创建tab
		for (int i = 0; i < tabs.length; i++) {
			addTab(i, tabs[i]);
		}
	}
	
	/**
     * <p>接口定义：当child view 的选定状态有变化时调用这个回调函数</p>
     */
    public interface OnCheckedChangeListener {
        /**
         * <p>当child view的选定状态发生变化时调用这个方法</p>
         *
         * @param group 包含child view的FlowLayout
         * @param checkedId 用于标识选中状态的child view的唯一checkID
         * checkedId the unique identifier of the newly checked radio button
         */
        void onCheckedChanged(FlowLayout group, int checkedId);
    }
    /**
     * item选择状态监听器
     */
    private OnCheckedChangeListener mOnCheckedChangeListener;
    /**
     * <p>注册一个回调监听器，当其中一个item的选择状态变化时激发这个监听器</p>
     *
     * @param listener 监听器
     */
    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        mOnCheckedChangeListener = listener;
    }
/*
    *//**
     * 根据传递进来的参数id设置当前当前选中的item
     * @param id 将要设置选中的item的id
     * @date 2015-1-19上午10:07:22
     * @since 1.1
     *//*
    public void setCheckedId(int id) {
    	mCheckedId = id;
    	setCurrentItem(id);
    }
    */
	/**
	 * 存储所有的View
	 */
	private List<List<View>> mAllViews = new ArrayList<List<View>>();
	/**
	 * 每一行的高度
	 */
	private List<Integer> mLineHeight = new ArrayList<Integer>();
	/**
	 * 分析: 
	 * 		为什么要有第1步呢？因为你需要知道每行展示哪几个子View,
	 * 每一行的高度是多少，才好计算每个子View的y轴坐标
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// 1.重新计算填补mAllViews、mLineHeight
		// 清除mAllViews、mLineHeight
		mAllViews.clear();
		mLineHeight.clear();
		// 当前viewgroup的宽度(执行了onMeasure之后的值)
		int width = getWidth();
		
		// 记录行宽高
		int lineWidth = 0;
		int lineHeight = 0;
		
		List<View> lineViews = new ArrayList<View>();
		
		int cCount = getChildCount();
		
		for (int i = 0; i < cCount; i++) {
			View child = this.getChildAt(i);
			MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
			
			// 获取子View的测量宽高
			int childWidth = child.getMeasuredWidth();
			int childHeight = child.getMeasuredHeight();
			
			// ----------------------行宽高
			// 如果需要换行
			if (childWidth + lineWidth + lp.leftMargin + lp.rightMargin > width - getPaddingLeft() - getPaddingRight()){
				// 记录LineHeight
				mLineHeight.add(lineHeight);
				// 记录当前行的Views
				mAllViews.add(lineViews);

				// 重置我们的行宽和行高
				lineWidth = 0;
				lineHeight = childHeight + lp.topMargin + lp.bottomMargin;
				// 重置我们的View集合
				lineViews = new ArrayList<View>();
			}
			lineWidth += childWidth + lp.leftMargin + lp.rightMargin;
			lineHeight = Math.max(lineHeight, childHeight + lp.topMargin
					+ lp.bottomMargin);
			// 把child添加至lineViews
			lineViews.add(child);
		}// for end
		
		// 处理最后一行
		mLineHeight.add(lineHeight);
		mAllViews.add(lineViews);
		
		// 2.计算并设置每个子View的位置
		// 计算绘制子View的起始位置
		int left = getPaddingLeft();
		int top = getPaddingTop();
		
		// 行数
		int lineNum = mAllViews.size();
		
		for (int i = 0; i < lineNum; i++) {
			// 当前行的所有的View
			lineViews = mAllViews.get(i);
			lineHeight = mLineHeight.get(i);
			
			for(int j = 0; j < lineViews.size(); j++){
				View child = lineViews.get(j);
				// 判断child的显示状态
				if(child.getVisibility() == View.GONE){
					continue;
				}
				
				MarginLayoutParams lp = (MarginLayoutParams) child
						.getLayoutParams();

				int lc = left + lp.leftMargin;
				int tc = top + lp.topMargin;
				int rc = lc + child.getMeasuredWidth();
				int bc = tc + child.getMeasuredHeight();

				// 为子View进行布局
				child.layout(lc, tc, rc, bc);
				
				left += child.getMeasuredWidth() + lp.leftMargin
						+ lp.rightMargin;
			}
			// 下一行的起始位置
			left = getPaddingLeft() ; 
			top += lineHeight ; 
		}
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// 父类提供的宽高和测量模式
		int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
		int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
		int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
		int modeHeight = MeasureSpec.getMode(heightMeasureSpec);
		
		// wrap_content模式
		int width = 0;
		int height = 0;
		
		// 记录每一行的宽度与高度
		int lineWidth = 0;
		int lineHeight = 0;
		
		// 得到内部元素的个数
		int cCount = getChildCount();
		
		for (int i = 0; i < cCount; i++) {
			View child = this.getChildAt(i);
			// 测量子View的宽高
			measureChild(child, widthMeasureSpec, heightMeasureSpec);
			// 得到LayoutParams
			MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
			
			// 得到子View的宽=子view的测量宽度+margin值
			int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
			// 得到子view的高=子view的测量高度+margin值
			int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
			
			// 换行
			if((lineWidth + childWidth) > (sizeWidth - getPaddingLeft() - getPaddingRight())){
				// 对比确定width
				width = Math.max(width, lineWidth);
				// 记录高度
				height += lineHeight;
				// 重置lineWidth、lineHeight
				lineWidth = childWidth;
				lineHeight = childHeight;
			}
			// 不换行
			else{
				// 更新行宽、高
				lineWidth += childWidth;
				lineHeight = Math.max(lineHeight, childHeight);
			}
			// 当前子View为最后一个child时
			if(i == cCount -1){
				width = Math.max(width, lineWidth);
				height += lineHeight;
			}
		}
		
		Log.e(TAG, "sizeWidth = " + sizeWidth);
		Log.e(TAG, "sizeHeight = " + sizeHeight);
		
		// 设置FlowLayout的实际宽高
		setMeasuredDimension(
				modeWidth == MeasureSpec.EXACTLY ? sizeWidth : width + getPaddingLeft() + getPaddingRight(), 
				modeHeight == MeasureSpec.EXACTLY ? sizeHeight : height + getPaddingTop() + getPaddingBottom()
					);
	}
	
	@Override
	public LayoutParams generateLayoutParams(AttributeSet attrs) {
		// TODO Auto-generated method stub
		return new MarginLayoutParams(getContext(), attrs);
	}
	
	/**
	 * child item view 被点击时的监听器
	 */
	private final OnClickListener mTabClickListener = new OnClickListener() {
		public void onClick(View view) {
			// 获取当前view所代表的id号，更新UI
			TabView textView = (TabView) view;
			// 获取当前索引号
			int oldSelectedIndex = mCheckedId;
			int newSelectedIndex = textView.getIndex();
			mCheckedId = newSelectedIndex;
			Log.d(TAG, "上一个被选中的tab索引号是 oldSelected = " + oldSelectedIndex +
					"\n当前选中状态 mCheckId = " + mCheckedId);
			setCurrentItem(mCheckedId);
		}
	};
	/**
	 * 像当前viewgroup添加child view
	 * @param index 索引
	 * @param text	tab显示的文本
	 * @date 2015-1-19下午2:13:37
	 * @since	1.1
	 */
	private void addTab(int index, String text){
		final TabView tabView = new TabView(getContext());
        tabView.mIndex = index;
        tabView.setClickable(true);
        tabView.setOnClickListener(mTabClickListener);
        tabView.setText(text);
        tabView.setSelected(index == mCheckedId);
        tabView.setTextColor(getResources().getColorStateList(R.color.custom_text));
        tabView.setBackgroundResource(R.drawable.tv_collection_bg);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);  
        lp.setMargins(8, 4, 8, 4);
        tabView.setLayoutParams(lp);
        // 添加监听器
        tabView.setOnClickListener(mTabClickListener);
        // 添加到FlowLayout中
        this.addView(tabView);
	}
	/**
	 * 设置当前选中tabview
	 * @param index 索引号
	 * @date 2015-1-19下午1:44:13
	 * @since	1.1
	 */
	public void setCurrentItem(int index) {
		// 遍历所有child view，更新child view的选择状态
		int tabCount = this.getChildCount();
		for (int i = 0; i < tabCount; i++) {
			final TabView tabView = (TabView) this.getChildAt(i);
			final boolean isSelected = (i == index);
			tabView.setSelected(isSelected);
		}
		// 调用OnCheckedChangeListener.onCheckedChanged()方法通知选中状态更新
		mOnCheckedChangeListener.onCheckedChanged(this, index);
	}

	/**
	 * 自定义tab，继承于{@link android.widget.TextView}
	 * @author jalen
	 * @version 1.1
	 * @since	1.1
	 * @date 2015-1-19下午1:31:53
	 */
	private class TabView extends TextView {
		/**
		 * 用于索引至textview的item id号
		 */
        private int mIndex;

        public TabView(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}
        
		public int getIndex() {
            return mIndex;
        }
    }
}
