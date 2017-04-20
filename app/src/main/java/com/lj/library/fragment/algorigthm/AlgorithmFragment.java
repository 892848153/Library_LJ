package com.lj.library.fragment.algorigthm;

import android.os.Bundle;
import android.util.Log;

import com.lj.library.R;
import com.lj.library.fragment.BaseFragment;

import java.util.BitSet;

/**
 * Created by liujie_gyh on 16/8/20.
 */
public class AlgorithmFragment extends BaseFragment{

    private static final String TAG = AlgorithmFragment.class.getSimpleName();

    @Override
    protected int initLayout(Bundle savedInstanceState) {
        return R.layout.test_fragment;
    }

    @Override
    protected void initComp(Bundle savedInstanceState) {
        testQuickSort();
        findDuplicateAppear();
        findMaxSubSum();
    }

    private void testQuickSort() {
        int[] data = {2, 94, 99, 1, 4, 5, 2, 9, 43, 23, 1 ,32, 4, 77, 8, 122, 4};
        quickSort(data, 0, data.length - 1);
        StringBuilder sb = new StringBuilder();
        for (int i : data) {
            sb.append(i).append(',');
        }
        System.out.println(sb.toString());
    }

    /**
     * 快速排序
     * @param data
     * @param low
     * @param high
     */
    private void quickSort(int[] data, int low, int high) {
        if (low < high) {
            int middle = getMiddle(data, low, high);
            quickSort(data, low, middle-1);
            quickSort(data, middle+1, high);
        }
    }

    private int getMiddle(int[] data, int low, int high) {
        int temp = data[low];
        while (low < high) {
            while (low < high && data[high] >= temp) {
                high--;
            }
            data[low] = data[high];

            while (low < high && data[low] <= temp) {
                low++;
            }
            data[high] = data[low];
        }
        data[low] = temp;
        return low;
    }

    /**
     * 找出取值为0~99的数组中重复出现的数字.
     */
    private void findDuplicateAppear() {
        int [] data = {1, 2, 5, 7, 9, 3, 2, 4, 5, 23, 21, 43, 66,  11, 22, 23, 8, 10, 1};
        // 因为数组中取值范围为0~99, 所以这里是100的大小
        BitSet bitSet = new BitSet(100);
        BitSet occurs = new BitSet(100);

        for (int i : data) {
            if (!occurs.get(i)) {
                // 第一次遍历到这个数
                occurs.set(i);
                bitSet.set(i);
            } else {
                // 重复出现这个数
                bitSet.set(i, false);
            }
        }

        bitSet.xor(occurs);

        for (int i = 0; i < bitSet.length(); i++) {
            if (bitSet.get(i)) {
                Log.e(TAG, i + "");
            }
        }
    }

    /**
     * 求出最大子序列的和 详情见:http://blog.sina.com.cn/s/blog_7c984e770101egyb.html
     */
    private void findMaxSubSum() {
        int [] arr = {-2, 11, -4, 13, -5, -2};
        int max = maxSubSum(arr);
        System.out.println("find max sub sum in: -2, 11, -4, 13, -5, -2" + " result: " + max);
    }

    /**
     * 求出最大子序列的和
     * @param arr
     * @return
     */
    private int maxSubSum(int[] arr) {
        int maxSum = 0;
        int thisSum = 0;
        for (int i = 0; i < arr.length; i++) {
            thisSum += arr[i];
            if (thisSum > maxSum)// thisSum在[0,maxSum]之间时不需要任何处理
                maxSum = thisSum;
            else if (thisSum < 0)// 说明加上当前元素使得子序列为负数了,那么抛弃这段子序列(相当于thisSum赋值为0),从下一轮for开始
                thisSum = 0;
        }
        return maxSum;
    }
}
