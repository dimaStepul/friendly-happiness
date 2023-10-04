package com.github.dimastepul.friendlyhappiness

import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.PsiFile
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.forEachDescendantOfType

const val SEPARATOR = "/"


open class Node(val name: String)

fun splitDirectory(dir: String) = dir.split(SEPARATOR)

fun isKotilnFileType(file: FileType) = file is KotlinFileType


fun countClassesAndFuncs(psi: PsiFile): AmountClassesFuncs? {
    var classes = 0
    var functions = 0
    when (psi) {
        is KtFile -> {
            psi.forEachDescendantOfType<KtClassOrObject> { classes++ }
            psi.forEachDescendantOfType<KtNamedFunction> { functions++ }
        }

        else -> return null
    }
    return AmountClassesFuncs(classes, functions)
}
