package com.lovely3x.fireinstantmessager.views.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.lovely3x.fireinstantmessager.R;
import com.lovely3x.fireinstantmessager.views.fragments.ContactFragment;
import com.lovely3x.fireinstantmessager.views.fragments.MessageFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, TabHost.OnTabChangeListener {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @Bind(R.id.nav_view)
    NavigationView navigationView;

    @Bind(android.R.id.tabhost)
    FragmentTabHost mFragmentHost;

    /**
     * 主页的底部的buttonBar名字
     */
    private String[] mTabNames;
    /**
     * 主页的底部的他不Bar的图片资源
     */
    private int[] mTabRes;
    /**
     * fragments
     */
    private Class[] mFragments;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bind();
        setSupportActionBar(mToolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        initTab();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * 初始化tab
     */
    private void initTab() {
        mTabNames = getResources().getStringArray(R.array.home_tables);
        mFragments = new Class[]{MessageFragment.class, ContactFragment.class};
        mTabRes = new int[]{R.drawable.msg_selector,R.drawable.contact_selector};

        mFragmentHost.setup(this, getSupportFragmentManager(), R.id.fl_activity_main_content);

        mFragmentHost.getTabWidget().setDividerDrawable(null);
        //得到fragment的个数
        int count = mTabNames.length;
        for (int i = 0; i < count; i++) {
            //为每一个Tab按钮设置图标、文字和内容
            TabHost.TabSpec tabSpec = mFragmentHost.newTabSpec(String.valueOf(i)).setIndicator(getTabItemView(i));
            //将Tab按钮添加进Tab选项卡中
            mFragmentHost.addTab(tabSpec, mFragments[i], null);
        }
        mFragmentHost.setOnTabChangedListener(this);
    }
    /**
     * 获取指定下标的视图
     *
     * @param index 下标
     * @return tab视图
     */
    private View getTabItemView(int index) {
        View view = getLayoutInflater().inflate(R.layout.view_tab_item, null);
        ImageView icon = ButterKnife.findById(view, R.id.iv_view_tab_item);
        TextView text = ButterKnife.findById(view, R.id.tv_view_tab_item);
        icon.setImageResource(mTabRes[index]);
        text.setText(mTabNames[index]);
        return view;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camara) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void entryForeground() {
        super.entryForeground();
    }

    @Override
    public void onTabChanged(String tabId) {

    }
}
