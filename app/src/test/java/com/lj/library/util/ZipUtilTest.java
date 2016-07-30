package com.lj.library.util;

import android.content.Context;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by jie.liu on 16/6/29.
 */
@RunWith(MockitoJUnitRunner.class)
public class ZipUtilTest {

    @Mock
    private Context mContext;

    /**
     * 跑一个测试类的所有测试方法之前，会执行一次
     */
    @BeforeClass
    public static void init() {

    }

    /**
     * 执行每个测试方法之前都会执行一次
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {

    }

    /**
     * 执行每个测试方法之后都会执行一次
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testZip() throws Exception {
        assertEquals(1L, 1L);
    }

    @Test
    public void testZip1() throws Exception {

    }

    @Test
    public void testUnzipFile() throws Exception {

    }

    @Test
    public void testUnzipFile1() throws Exception {

    }

    @Test
    public void testSetOnZipListener() throws Exception {

    }

    @Test
    public void testGetZipFilePath() throws Exception {

    }

    /**
     * 跑一个测试类的所有测试方法之后，会执行一次
     */
    @AfterClass
    public static void release() {

    }

}