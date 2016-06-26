package com.lj.library.fragment.dagger;

import javax.inject.Inject;

/**
 * Created by liujie_gyh on 16/6/26.
 */
public class ConstructorInjection {

    private FieldInjection mFieldInjection;

    /**
     * 构造函数添加@Inject注解, 这样Dagger会在需要创建该类型对象的时候,
     * 调用该构造函数来创建一个对象. 比如:
     * <p/>
     * class SomeClass {
     *
     * @param fieldInjection
     * @Inject ConstructorInjection mConstructorInjection;
     * <p/>
     * public SomeClass(ConstructorInjection constructorInjection) {
     * mConstructorInjection = constructorInjection;
     * }
     * }
     * </p>
     * 当Dagger调用Component中的方法实例化一个SomeClass, 或者调用方法为SomeClass提供依赖注入时<BR/>
     * Dagger会调用ConstrutorInjection的构造函数来创建一个对象,并赋值给SomeClass的域.
     */
    @Inject
    public ConstructorInjection(FieldInjection fieldInjection) {
        mFieldInjection = fieldInjection;
    }
}
