package com.example.administrator.endlessbanner;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import java.util.LinkedList;
import java.util.List;

/**
 * @author wanghuilin
 * @time 2016/3/25  16:59
 */
public class Banner extends RelativeLayout {

    private ViewPager viewPager;//滚动显示的viewpager
    private List<View> views;//显示的图片集合
    private BannerAdapter bannerAdapter;//viewpager的适配器
    private RadioGroup radioGroup;//使用radioGroup实现下面的指示器
    private RadioButton radioButton;//指示器设置

    public Banner(Context context) {
        this(context, null);
    }

    public Banner(Context context, AttributeSet attrs) {
        super(context, attrs);
        //自定义Banner的布局
        View view = LayoutInflater.from(context).inflate(R.layout.banner_layout, this, true);
        //滚动显示ViewPager
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        radioGroup = (RadioGroup) view.findViewById(R.id.radiogroup);
        views = new LinkedList<>();
        bannerAdapter = new BannerAdapter();
        viewPager.setAdapter(bannerAdapter);
        //设置viewpager的滚动监听
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                //改变当前显示的pager的位置
                pagerPosition = position;
                position = position % views.size();
                //改变被选中的指示器的状态
                for (int i = 0; i < radioGroup.getChildCount(); i++) {
                    ((RadioButton) radioGroup.getChildAt(i)).setChecked(i == position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    /**
     * 设置显示的资源
     *
     * @param data
     */


    public void setData(Object... data) {
        int id[] = new int[data.length];
        for (int i = 0; i < id.length; i++) {
            id[i] = i;
        }
        for (int i = 0; i < data.length; i++) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.banner_item_layout, viewPager, false);
            ImageView imageView = (ImageView) view.findViewById(R.id.iv_banner_item);
            if (data[i] instanceof Integer) {

                imageView.setImageResource((Integer) data[i]);
            } else {
//                TODO 如果设置的数据源为图片地址或者其他类型，根据具体情况为imageView设置图片源
            }
            views.add(view);
            //指示器小圆点的设置
            View indicator = LayoutInflater.from(getContext()).inflate(R.layout.indicator_item_layout, viewPager, false);
            radioButton = (RadioButton) indicator.findViewById(R.id.radioButton);
            radioButton.setId(id[i]);// 为radioButton设置一个id,不设置的话第一个radiobutton总是没法设置成选中中状态（不太清楚好像是个bug）
            if (i == 0) {
                radioButton.setChecked(true); //将第一个指示器设置为选中
            }
            RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(dip2px(getContext(), 10f), dip2px(getContext(), 10f));
            layoutParams.leftMargin = dip2px(getContext(), 20);
            radioGroup.addView(radioButton, layoutParams);
        }
        bannerAdapter.notifyDataSetChanged();


    }

    /**
     * ViewPager的适配器
     */
    class BannerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            //将count设置成整数最大值，虚拟实现无线轮播
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(views.get(position % views.size()));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = views.get(position % views.size());
            ViewGroup parent = (ViewGroup) view.getParent();
            //如果当前要显示的view有父布局先将父布局移除（view只能有一个父布局）
            if (parent != null) {
                parent.removeView(view);
            }
            container.addView(view);
            return view;
        }
    }

    //dp和px的转换方法
    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    int pagerPosition;
    Handler handler;


    /**
     * 通过嵌套发送消息循环滚动viewpager
     *
     * @param delayMillis 轮播图片的时间
     */
    public void bannerPlay(final long delayMillis) {
//        设置显示的初始位置，模拟向左无限滑动（设置合适的值就行，一般人也不会滑动很多）
        pagerPosition = views.size() * 100;
        if (handler == null) {
            handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            viewPager.setCurrentItem(++pagerPosition);
                        }
                    });
                    Message message = handler.obtainMessage(0);
                    handler.sendMessageDelayed(message, delayMillis);
                }
            };
            Message message = handler.obtainMessage(0);
            handler.sendMessageDelayed(message, delayMillis);
        }
    }
}
