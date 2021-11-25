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

import com.android.tools.lint.detector.api.*
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UAnnotation
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UMethod

class SingletonDetector : Detector(), SourceCodeScanner {

    override fun applicableAnnotations(): List<String>? {
        return listOf("javax.inject.Singleton")
    }

    override fun visitAnnotationUsage(
        context: JavaContext,
        usage: UElement,
        type: AnnotationUsageType,
        annotation: UAnnotation,
        qualifiedName: String,
        method: PsiMethod?,
        annotations: List<UAnnotation>,
        allMemberAnnotations: List<UAnnotation>,
        allClassAnnotations: List<UAnnotation>,
        allPackageAnnotations: List<UAnnotation>
    ) {
        reportUsage(context, usage)
    }

    private fun reportUsage(context: JavaContext, declaration: UElement) {
        context.report(
            issue = ISSUE,
            scope = declaration,
            location = context.getNameLocation(declaration),
            message = "javax.inject.Singleton is forbidden",
        )
    }

    companion object {
        private val IMPLEMENTATION = Implementation(
            SingletonDetector::class.java,
            Scope.JAVA_FILE_SCOPE
        )

        val ISSUE: Issue = Issue.create(
            id = "SingletonDetector",
            briefDescription = "The Singleton annotation should not be used",
            explanation = """
                The @Singleton annotation should not be used to contribute dependencies
                to the App dagger component.
                Use @SingleIn(AppObjectGraph::class) instead.
            """.trimIndent(),
            category = Category.CORRECTNESS,
            priority = 3,
            severity = Severity.ERROR,
            implementation = IMPLEMENTATION,

        )
    }
}