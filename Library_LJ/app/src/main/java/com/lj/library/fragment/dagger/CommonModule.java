package com.lj.library.fragment.dagger;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * Created by liujie_gyh on 16/6/26.
 */
@Module
public class CommonModule {

    @Provides
    public FieldInjection provideFieldInjection() {
        return new FieldInjection("default");
    }

    @Provides
    @Named("aField")
    public FieldInjection provideNamedFieldA() {
        return new FieldInjection("name a");
    }

    @Provides
    @Named("bField")
    public FieldInjection provideNamedFieldB() {
        return new FieldInjection("name b");
    }

}
