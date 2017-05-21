/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.rs.testbackward;

import com.android.rs.unittest.*;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import android.util.Log;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.util.ArrayList;
import java.util.List;

/**
 * RSTestBackward, functional test for platform RenderScript APIs.
 * To run the test, please use command
 *
 * adb shell am instrument -w com.android.rs.testbackward/android.support.test.runner.AndroidJUnitRunner
 */
@RunWith(Parameterized.class)
public class RSBackwardCompatibilityTests {
    private static final String TAG = RSBackwardCompatibilityTests.class.getSimpleName();

    /**
     * Returns the list of subclasses of UnitTest to run.
     */
    @Parameters(name = "{0}")
    public static Iterable<?> getParams() throws Exception {
        int thisApiVersion = android.os.Build.VERSION.SDK_INT;

        int minApiVersion = 21;
        if (thisApiVersion < minApiVersion) {
            Log.w(TAG,
                String.format("API version is less than %d, no tests running", minApiVersion));
        }

        Context ctx = InstrumentationRegistry.getTargetContext();

        List<UnitTest> validUnitTests = new ArrayList<>();

        Iterable<Class<? extends UnitTest>> testClasses =
            RSTests.getTestClassesForCurrentAPIVersion();
        for (Class<? extends UnitTest> testClass : testClasses) {
            UnitTest test = testClass.getDeclaredConstructor(Context.class).newInstance(ctx);
            validUnitTests.add(test);
        }

        UnitTest.checkDuplicateNames(validUnitTests);

        return validUnitTests;
    }


    @Parameter(0)
    public UnitTest mTest;

    @Test
    @MediumTest
    public void testRSUnitTest() throws Exception {
        String thisDeviceName = android.os.Build.DEVICE;
        int thisApiVersion = android.os.Build.VERSION.SDK_INT;
        Log.i(TAG, String.format("RenderScript backward compatibility testing (%s) "
                + "on device %s, API version %d",
                mTest.toString(), thisDeviceName, thisApiVersion));
        mTest.runTest();
        Assert.assertTrue(mTest.getSuccess());
    }
}