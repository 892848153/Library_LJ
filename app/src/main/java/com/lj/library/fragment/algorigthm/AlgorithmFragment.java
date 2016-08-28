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
}
