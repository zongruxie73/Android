/*
 * Copyright (c) 2021 DuckDuckGo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.duckduckgo.app.lint.rule

import com.android.tools.lint.checks.infrastructure.TestFile
import com.android.tools.lint.checks.infrastructure.TestFiles.java

object Stubs {

    /**
     * [TestFile] containing Log.java from the Android SDK.
     *
     * This is a hacky workaround for the Android SDK not being included on the Lint test harness
     * classpath. Ideally, we'd specify ANDROID_HOME as an environment variable.
     */
    val ANDROID_LOG_IMPL_JAVA: TestFile = java(
        """
                package android.util;
                
                public class Log {
                    public static void d(String tag, String msg) {
                        // Stub!
                    }
                }
            """).indented()


    val CUSTOM_LOG_IMPL_JAVA: TestFile = java(
        """
                package com.fabiocarballo.lint;
                
                public class Log {
                    public static void d(String tag, String msg) {
                        // Stub!
                    }
                }
            """.trimIndent()
    )

}