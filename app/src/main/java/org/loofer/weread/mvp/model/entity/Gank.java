package org.loofer.weread.mvp.model.entity;

import java.util.List;

/**
 * ============================================================
 * 版权： x x 版权所有（c）2016
 * <p>
 * 作者：Loofer
 * 版本：1.0
 * 创建日期 ：2016/12/29 11:28.
 * 描述：
 * <p>
 * 注:如果您修改了本类请填写以下内容作为记录，如非本人操作劳烦通知，谢谢！！！
 * Modified Date Modify Content:
 * <p>
 * ==========================================================
 */
public class Gank {


    /**
     * _id : 58631c7e421aa9723a5a77e7
     * createdAt : 2016-12-28T09:59:26.111Z
     * desc : 站在谷爹的肩膀上，修改 v4 包中 SlidingPaneLayout 的源码来实现滑动返回布局
     * images : ["http://img.gank.io/e12d9e14-a0d6-4117-94bc-327661df336a"]
     * publishedAt : 2016-12-28T11:57:39.616Z
     * source : web
     * type : Android
     * url : https://github.com/bingoogolapple/BGASwipeBackLayout-Android
     * used : true
     * who : 王浩
     */

    public String _id;
    public String createdAt;
    public String desc;
    public String publishedAt;
    public String source;
    public String type;
    public String url;
    public boolean used;
    public String who;
    public List<String> images;

}
