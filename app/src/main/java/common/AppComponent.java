package common;

import android.app.Application;

import com.google.gson.Gson;

import org.loofer.framework.base.AppManager;
import org.loofer.framework.di.module.AppModule;
import org.loofer.framework.di.module.ClientModule;
import org.loofer.framework.di.module.GlobeConfigModule;
import org.loofer.framework.di.module.ImageModule;
import org.loofer.framework.widget.imageloader.ImageLoader;
import org.loofer.rxerrorhandler.core.RxErrorHandler;
import org.loofer.weread.di.module.CacheModule;
import org.loofer.weread.di.module.ServiceModule;
import org.loofer.weread.mvp.model.api.cache.CacheManager;
import org.loofer.weread.mvp.model.api.service.ServiceManager;

import javax.inject.Singleton;

import dagger.Component;
import okhttp3.OkHttpClient;

/**
 * Created by jess on 8/4/16.
 */
@Singleton
@Component(modules = {AppModule.class, ClientModule.class, ServiceModule.class, ImageModule.class,
        CacheModule.class, GlobeConfigModule.class})
public interface AppComponent {
    Application Application();

    //服务管理器,retrofitApi
    ServiceManager serviceManager();

    //缓存管理器
    CacheManager cacheManager();

    //Rxjava错误处理管理类
    RxErrorHandler rxErrorHandler();

    OkHttpClient okHttpClient();

    //图片管理器,用于加载图片的管理类,默认使用glide,使用策略模式,可替换框架
    ImageLoader imageLoader();

    //gson
    Gson gson();

    //用于管理所有activity
    AppManager appManager();
}
