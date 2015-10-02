package com.lovely3x.common.versioncontroller;


/**
 * Class Version
 */
public class Version {

    /**
     * 版本号
     */
    private int versionCode;

    /**
     * 版本名
     */
    private String versionName;
    /**
     * 包名
     */
    private String packageName;
    /**
     * 描述
     */
    private String description;

    /**
     * 发布时间
     */
    private long publishTime;

    /**
     * 是否需要强制更新
     */
    private boolean forceUpdate;

    /**
     * 资源定位符号
     */
    private String url;

    //
    // Constructors
    //
    public Version() {

    }

    public Version(int versionCode, String versionName, String packageName, String description, long publishTime, boolean forceUpdate, String url) {
        this.versionCode = versionCode;
        this.versionName = versionName;
        this.packageName = packageName;
        this.description = description;
        this.publishTime = publishTime;
        this.forceUpdate = forceUpdate;
        this.url = url;
    }

    /**
     * Set the value of versionCode
     *
     * @param newVar the new value of versionCode
     */
    public void setVersionCode(int newVar) {
        versionCode = newVar;
    }

    /**
     * Get the value of versionCode
     *
     * @return the value of versionCode
     */
    public int getVersionCode() {
        return versionCode;
    }

    /**
     * Set the value of versionName
     *
     * @param newVar the new value of versionName
     */
    public void setVersionName(String newVar) {
        versionName = newVar;
    }

    /**
     * Get the value of versionName
     *
     * @return the value of versionName
     */
    public String getVersionName() {
        return versionName;
    }

    /**
     * Set the value of packageName
     *
     * @param newVar the new value of packageName
     */
    public void setPackageName(String newVar) {
        packageName = newVar;
    }

    /**
     * Get the value of packageName
     *
     * @return the value of packageName
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * Set the value of description
     * 描述
     *
     * @param newVar the new value of description
     */
    public void setDescription(String newVar) {
        description = newVar;
    }

    /**
     * Get the value of description
     * 描述
     *
     * @return the value of description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the value of url
     * 资源定位符号
     *
     * @param newVar the new value of url
     */
    public void setUrl(String newVar) {
        url = newVar;
    }

    /**
     * Get the value of url
     * 资源定位符号
     *
     * @return the value of url
     */
    public String getUrl() {
        return url;
    }

    public long getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(long publishTime) {
        this.publishTime = publishTime;
    }

    public boolean isForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
    }
}
