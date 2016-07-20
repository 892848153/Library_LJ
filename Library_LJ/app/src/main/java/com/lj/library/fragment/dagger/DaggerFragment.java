package com.lj.library.fragment.dagger;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lj.library.R;
import com.lj.library.fragment.BaseFragment;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import dagger.Lazy;

/**
 * Created by liujie_gyh on 16/6/26.
 */
public class DaggerFragment extends BaseFragment {

    @Inject
    FieldInjection mFieldInjection;

    /**
     * 第一次调用Lazy<T>.get()方法时才会创建T对象.
     */
    @Inject
    Lazy<FieldInjection> mLazy;

    /**
     * 每次调用Provider<T>.get()方法都会得到一个新的T对象
     */
    @Inject
    Provider<FieldInjection> mProvider;

    /**
     * 会寻找Module中同样带有@Named(value)的注解的@Provides方法来提供依赖注入
     */
    @Inject
    @Named("aField")
    FieldInjection mAField;

    /**
     * 会寻找Module中同样带有@Named(value)的注解的@Provides方法来提供依赖注入
     */
    @Inject
    @Named("bField")
    FieldInjection mBField;


    @Override
    protected View initLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceSte) {
        return inflater.inflate(R.layout.test_fragment, null);
    }

    @Override
    protected void initComp(Bundle savedInstanceState) {
        CommonComponent component = DaggerCommonComponent.builder().commonModule(new CommonModule()).build();
        //由于CommonModule的构造函数是默认的无参构造函数,所以可以省略,Dagger会为我们自动填充
//        CommonComponent component = DaggerCommonComponent.builder().build();
        //由于所有的Module的构造函数都是默认的无参构造函数,所有Component中会有create方法供我们创建Component
//        CommonComponent component = DaggerCommonComponent.create();

        // Dagger为会该类中所有需要依赖注入的地方提供注入
        component.injectDaggerFragment(this);
        // Dagger会通过带有@Inject注解的构造函数new一个该对象,并提供必要的依赖注入
        ConstructorInjection constructorInjection = component.createConstructorInjection();
        // 自己new一个该对象, dagger无法提供依赖注入,所以它的域FieldInjection=null
        ConstructorInjection injection = new ConstructorInjection(null);

        //第一次调用Lazy<T>.get()方法,Dagger才会我们实例化一个对象, 每次调用Lazy<T>.get()得到的是同一个对象
        FieldInjection fieldInjection = mLazy.get();

        // 每次调用Provider<T>.get()都将得到一个新对象.
        mProvider.get();
        mProvider.get();
        mProvider.get();


    }
}
