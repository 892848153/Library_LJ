package com.lj.library.fragment.multithread;

import java.math.BigInteger;
import java.util.Arrays;

import javax.annotation.concurrent.Immutable;

/**
 *
 * Created by liujie_gyh on 2017/5/4.
 */
public class CachedFactorizer {

    /**
     * 缓存最近的一个请求参数
     */
    private BigInteger lastRequestParam;
    /**
     * 缓存最近一个请求参数的结果
     */
    private BigInteger[] lastResult;

    /**
     * 上下两个同步代码块必须使用相同的锁，因为他们都对相同状态变量的操作
     * @param requestParam
     */
    public void doTheJob(BigInteger requestParam) {
        BigInteger[] result = null;
        synchronized (this) {
            // 这是典型的"先检查后执行"的复合操作，需要保证其以原子操作来执行
            // 所以需要同步。
            if (requestParam.equals(lastRequestParam)) {
                result = lastResult.clone();
            }
        }

        if (result == null) {
            result = getTheResult(requestParam);
            synchronized (this) {
                // lastRequestParam和lastResult是互相约束的，并非独立的
                // 所以对这两个变量的修改必须是在原子操作中进行才能保证不变性条件
                lastRequestParam = requestParam;
                lastResult = result.clone();
            }
        }
    }



    /**
     * 使用volatile保证缓存结果的可见性
     */
    private volatile OneValueCache mCache = new OneValueCache(null, null);

    /**
     * 第二套方案，对于在访问和更新多个相关变量时出现的竞争条件问题，
     * 可以通过将这些变量全部保存在一个不可变对象中来消除.
     * @param requestParam
     */
    public void doTheJob2(BigInteger requestParam) {
        BigInteger[] result = mCache.getFactors(requestParam);
        if (result == null) {
            result = getTheResult(requestParam);
            mCache = new OneValueCache(requestParam, result);
        }
    }

    private BigInteger[] getTheResult(BigInteger requestParam) {
        BigInteger[] result = new BigInteger[3];
        result[0] = new BigInteger("1");
        result[1] = new BigInteger("2");
        result[2] = new BigInteger("3");
        return result;
    }

    @Immutable
    class OneValueCache {
        /**
         * final域能确保初始化过程的安全性，共享这些对象时无须同步
         */
        private final BigInteger lastRequestParam;
        private final BigInteger[] lastResult;

        public OneValueCache(BigInteger i, BigInteger[] factors) {
            lastRequestParam = i;
            lastResult = Arrays.copyOf(factors, factors.length);
        }

        public BigInteger[] getFactors(BigInteger i) {
            if (lastRequestParam == null || !lastRequestParam.equals(i)) {
                return null;
            } else {
                return Arrays.copyOf(lastResult, lastResult.length);
            }
        }
    }
}
