package com.lovely3x.common.versioncontroller.impls;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

/**
 * 由于在通知栏必须显式的指定跳转的activity
 * 所以就必须要有一个跳台
 * Created by lovely3x on 15-8-28.
 */
public class ClickNotificationToInstallActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Uri data = getIntent().getData();
        Intent promptInstall = new Intent(Intent.ACTION_VIEW);
        promptInstall.setDataAndType(data, "application/vnd.android.package-archive");
        startActivity(promptInstall);
        if (VersionControllerManagerService.getInstance() != null)
            VersionControllerManagerService.getInstance().stopSelf();
        finish();
    }

}
