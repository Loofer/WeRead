package org.loofer.framework.di.component;

import org.loofer.framework.di.module.AppModule;
import org.loofer.framework.base.BaseApplication;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by jess on 14/12/2016 13:58
 * Contact with jess.yan.effort@gmail.com
 */
@Singleton
@Component(modules={AppModule.class})
public interface BaseComponent {
    void inject(BaseApplication application);
}
