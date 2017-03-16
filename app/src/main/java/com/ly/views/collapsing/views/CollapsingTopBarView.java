package com.ly.views.collapsing.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.ly.views.collapsing.R;


/**
 * Created by liyang on 2017/3/13.
 * 头部自动隐藏控件
 */
public class CollapsingTopBarView extends ViewGroup {

    public CollapsingTopBarView(Context context) {
        this(context, null);
    }

    public CollapsingTopBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void initContentView() {
        if (contentView == null) return;
        contentView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                pagerTouch = true;
                return false;
            }
        });
    }

    View collapsingView, contentView;

    //执行的位置
    int marginHeight;

    //移动到最下和最上边的位置
    int bottom = 0, top, height;


    //记录坐标
    float mx, my;
    //判断
    boolean pagerTouch;
    //是否在自动移动中
    boolean autoMove = false;

    boolean actionMove;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean f = super.dispatchTouchEvent(ev);

        if (!pagerTouch) {
            actionMove = ev.getAction() == MotionEvent.ACTION_MOVE;
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                my = ev.getY();
                mx = ev.getX();

            } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
                if (!pagerTouch) {
                    float mm = ev.getY() - my;
                    moveToolbar(mm);
                }
                my = ev.getY();
                mx = ev.getX();
            } else {
                mx = 0;
                my = 0;

                if (marginHeight != 0 && Math.abs(marginHeight) != height&&!actionMove) {
                    autoMove(collapsingView);
                }
            }
        }
        pagerTouch = false;
        return f;
    }


    public boolean isTopHide(){
        return marginHeight==-height;
    }

    /**
     * 告诉每次移动的距离 来执行移动
     *
     * @param move
     */
    private void moveToolbar(float move) {
        //判断是否移动到顶或者到底
        if (move > 0 && marginHeight == 0 || move < 0 && marginHeight == -height) {
            autoMove = false;
            return;
        }
        //是否在执行回弹下拉动画中
        if (inAutoMove) return;
        //移动距离不为空
        if (move == 0) return;

        if (collapsingView.getLayoutParams() != null && collapsingView.getLayoutParams() instanceof LayoutParams) {
            LayoutParams layoutParams = (LayoutParams) collapsingView.getLayoutParams();
            marginHeight += move;
            if (marginHeight > bottom) {
                marginHeight = bottom;
            } else {
                //
                marginHeight = Math.abs(marginHeight) >= height ? -height : marginHeight;
            }
//        layoutParams.setMargins(0, marginHeight, 0, 0);//4个参数按顺序分别是左上右下
            layoutParams.setMarginTop(marginHeight);
            collapsingView.setLayoutParams(layoutParams);

            autoMove = true;
        }
    }

    boolean inAutoMove = false, delayed = true;

    /**
     * 自动回弹 下拉
     *
     * @param view
     */
    private void autoMove(View view) {
        if (delayed) {
            delayed = false;
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    inAutoMove = true;
                    if (autoMove&&!actionMove) {
                        if (Math.abs(top) - Math.abs(marginHeight) > Math.abs(top) / 2) {
                            move(true);
                        } else {
                            move(false);
                        }
                    }
                    inAutoMove = false;
                    delayed = true;
                }
            }, 500);
        }
    }


    MyRunnable autoMoveRunnable;

    /**
     * 回弹下拉的动画执行
     *
     * @param up
     */
    private void move(boolean up) {
        if (autoMoveRunnable == null) {
            autoMoveRunnable = new MyRunnable() {
                @Override
                public void run() {
                    if (up) {
                        marginHeight += 4;
                    } else {
                        marginHeight -= 4;
                    }
                    if (marginHeight >= bottom) {
                        marginHeight = bottom;
                    } else if (marginHeight <= top) {
                        marginHeight = top;
                    }
//                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
                    if (collapsingView.getLayoutParams() != null && collapsingView.getLayoutParams() instanceof LayoutParams) {
                        LayoutParams layoutParams = (LayoutParams) collapsingView.getLayoutParams();
//                        layoutParams.setMargins(0, marginHeight, 0, 0);//4个参数按顺序分别是左上右下
//                        collapsingView.setLayoutParams(layoutParams);
                        layoutParams.setMarginTop(marginHeight);
                        collapsingView.setLayoutParams(layoutParams);
                    }
                    if (marginHeight == top || marginHeight == bottom) {
                        autoMove = false;
                        return;
                    }
                    if (autoMove) {
                        move(up);
                    }
                }
            };
        }
        autoMoveRunnable.setUp(up);
        collapsingView.postDelayed(autoMoveRunnable, 10);
    }

    static class MyRunnable implements Runnable {
        boolean up;

        public void setUp(boolean up) {
            this.up = up;
        }

        @Override
        public void run() {

        }
    }

    @Override
    protected void onLayout(boolean b, int l, int i1, int i2, int i3) {

        int hg = 0;
        int count = getChildCount();
        View child;
        for (int i = 0; i < count; i++) {
            child = getChildAt(i);

            if (child.getLayoutParams() instanceof LayoutParams) {

                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                child.layout(0, hg + lp.getMarginTop(), child.getMeasuredWidth(), hg + child.getMeasuredHeight() + lp.getMarginTop());
                hg += child.getMeasuredHeight() + lp.getMarginTop();

//            if(child.getLayoutParams() instanceof LayoutParams) {
                if (lp.isCollapsing() && this.collapsingView == null) {
                    this.collapsingView = child;
                    this.height = child.getMeasuredHeight();
                    this.top = -this.height;
                } else if (lp.isContent() && this.contentView == null) {
                    this.contentView = child;
                    lp.height = child.getMeasuredHeight()- this.collapsingView.getHeight();
                    child.setLayoutParams(lp);
                    initContentView();
                }
//            }
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    public static class LayoutParams extends RelativeLayout.LayoutParams {
        boolean collapsing, content;

        int marginTop;

        public int getMarginTop() {
            return marginTop;
        }

        public void setMarginTop(int marginTop) {
            this.marginTop = marginTop;
        }

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.collapsing);
            collapsing = a.getBoolean(R.styleable.collapsing_collapsing, false);
            content = a.getBoolean(R.styleable.collapsing_content, false);
            a.recycle();
        }

        public boolean isCollapsing() {
            return collapsing;
        }

        public boolean isContent() {
            return content;
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(this.getContext(), attrs);
    }

}
