package com.github.dimastepul.friendlyhappiness

import com.intellij.openapi.components.service
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.github.dimastepul.friendlyhappiness.services.MyProjectService
import junit.framework.TestCase


@TestDataPath("\$CONTENT_ROOT/src/test/testData")
class MyPluginTest : BasePlatformTestCase() {


    fun testProjectService() {
        myFixture.copyDirectoryToProject("testData", ".")
        val projectService = project.service<MyProjectService>()

        val counts = projectService.totalAmount()
        TestCase.assertEquals(AmountClassesFuncs(3, 2), counts.countComponentsForDir("/src/kotlinFiles/" +
                "subDirectory/B.kt"))
        TestCase.assertEquals(AmountClassesFuncs(0, 0), counts.countComponentsForDir("/src/kotlinFiles/C.kt"))
        TestCase.assertEquals(AmountClassesFuncs(1, 2), counts.countComponentsForDir("/src/kotlinFiles/A.kt"))


        TestCase.assertFalse(counts.isDirectoryExists("src/FakeFiles/asdfs.txt"))
        TestCase.assertFalse(counts.isDirectoryExists("src/FakeFiles/fakeKotlin.txt"))
        TestCase.assertFalse(counts.isDirectoryExists("src/FakeFiles/Rustlin.rs"))

        TestCase.assertFalse(counts.isDirectoryExists("src/xmlFiles/foo.xml"))
        TestCase.assertFalse(counts.isDirectoryExists("src/FakeFiles/foo_alert.xml"))

    }

    override fun getTestDataPath() = "src/test/"
}