package com.github.dimastepul.friendlyhappiness.services

import com.github.dimastepul.friendlyhappiness.*
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.psi.*
import java.util.concurrent.locks.ReentrantLock

@Service(Service.Level.PROJECT)
class MyProjectService(private val project: Project) {
    private var projectCounts: AmountsDirs? = null
    private val lock = ReentrantLock()
    private fun calculateProjectComponents(): AmountsDirs {
        val psiManager = PsiManager.getInstance(project)
        val roots = ProjectRootManager.getInstance(project).contentRoots
        roots.sortBy { it.path }

        val tree = AmountsDirs("")

        for (root in roots) {
            VfsUtilCore.iterateChildrenRecursively(root, null) {
                if (!isKotilnFileType(it.fileType)) {
                    return@iterateChildrenRecursively true
                }
                val psi = psiManager.findFile(it) ?: return@iterateChildrenRecursively true
                val counts = countClassesAndFuncs(psi) ?: return@iterateChildrenRecursively true
                tree.setCountsForDirectory(it.path, counts)
                true
            }
        }
        return tree
    }

    fun totalAmount(): AmountsDirs {
        lock.lock()
        try {
            if (projectCounts == null) {
                projectCounts = calculateProjectComponents()
            }
            return projectCounts!!
        } finally {
            lock.unlock()
        }
    }
}
