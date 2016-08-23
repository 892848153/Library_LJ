package com.lj.library.fragment.algorigthm;

import android.os.Bundle;

import com.lj.library.R;
import com.lj.library.fragment.BaseFragment;
import com.lj.library.util.EncryptionUtils;
import com.lj.library.util.Logger;

/**
 * Created by liujie_gyh on 16/8/20.
 */
public class AlgorithmFragment extends BaseFragment{

    @Override
    protected int initLayout(Bundle savedInstanceState) {
        return R.layout.test_fragment;
    }

    @Override
    protected void initComp(Bundle savedInstanceState) {
        testQuickSort();

        String originalContent = "e________";
        String privateKey = "wocao";

        String confusedContent = EncryptionUtils.xorEncrypt(originalContent, privateKey);
        Logger.i(confusedContent, "");
        String decryptContent = EncryptionUtils.xorDecrypt(confusedContent, privateKey);
        Logger.i(decryptContent, "");
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
}
