package com.lj.library.fragment.dagger;

import dagger.Component;

/**
 * Created by liujie_gyh on 16/6/26.
 */
@Component(modules = {CommonModule.class})
public interface CommonComponent {

    ConstructorInjection createConstructorInjection();

    void injectDaggerFragment(DaggerFragment daggerFragment);

}
