package org.loofer.weread.mvp.model.api.service;

import org.loofer.framework.http.BaseServiceManager;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by jess on 8/5/16 13:01
 * contact with jess.yan.effort@gmail.com
 */
@Singleton
public class ServiceManager implements BaseServiceManager {
    private CommonService mCommonService;
    private HomeService mHomeService;

    /**
     * 如果需要添加service只需在构造方法中添加对应的service,在提供get方法返回出去,只要在ServiceModule提供了该service
     * Dagger2会自行注入
     *
     * @param commonService
     */
    @Inject
    public ServiceManager(CommonService commonService, HomeService homeService) {
        this.mCommonService = commonService;
        this.mHomeService = homeService;
    }

    public CommonService getCommonService() {
        return mCommonService;
    }


    public HomeService getHomeService() {
        return mHomeService;
    }


    /**
     * 这里可以释放一些资源(注意这里是单例，即不需要在activity的生命周期调用)
     */
    @Override
    public void onDestory() {

    }
}
