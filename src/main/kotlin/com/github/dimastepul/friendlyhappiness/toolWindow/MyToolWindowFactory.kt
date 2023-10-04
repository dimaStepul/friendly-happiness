package com.github.dimastepul.friendlyhappiness.toolWindow

import com.github.dimastepul.friendlyhappiness.*
import com.github.dimastepul.friendlyhappiness.services.MyProjectService
import com.intellij.icons.AllIcons
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.service
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBLoadingPanel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.RowLayout
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.ui.JBEmptyBorder
import java.awt.GridLayout
import javax.swing.JComponent
import javax.swing.ScrollPaneConstants

class MyToolWindowFactory : ToolWindowFactory, DumbAware {
    private val SEPARATOR = "/"

    private fun createTab(counts: AmountsDirs): JComponent {
        val panel = panel {
            row {
                label(MyBundle.message("FileName")).resizableColumn()
                label(MyBundle.message("Class"))
                label(MyBundle.message("Func"))
            }.layout(RowLayout.PARENT_GRID)
            addNode(this, counts)
        }
        panel.border = JBEmptyBorder(15)
        return panel
    }

    private fun addNode(panel: Panel, node: Node) {
        when (node) {
            is AmountsDirs -> addDirectory(panel, node)
            is AmountFiles -> addFile(panel, node)
        }
    }

    private fun addDirectory(panel: Panel, dir: AmountsDirs) {
        var leafDir = dir
        val dirName = StringBuilder(dir.name)
        while (leafDir.children.size == 1) {
            val child = leafDir.children.values.first()
            if (child is AmountsDirs) {
                leafDir = child
                dirName.append(SEPARATOR).append(child.name)
            } else {
                break
            }
        }
        panel.row {
            panel {
                row {
                    icon(AllIcons.Nodes.Folder)
                    label(dirName.toString())
                }
            }
        }.layout(RowLayout.PARENT_GRID)
        leafDir.children.values.forEach { addNode(panel, it) }
    }

    private fun addFile(panel: Panel, file: AmountFiles) {
        panel.row {
            panel {
                row {
                    icon(AllIcons.FileTypes.Any_type)
                    label(file.name)
                }
            }
            label(file.amountClassesFuncs.amountClasses.toString())
            label(file.amountClassesFuncs.amountFunctions.toString())
        }.layout(RowLayout.PARENT_GRID)
    }

    private var scrollPane: JBScrollPane = JBScrollPane(
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
    )


    private fun updateTab(tab: JComponent) {
        val rememberScrollPosition = scrollPane.viewport.viewPosition
        scrollPane.setViewportView(tab)
        scrollPane.viewport.viewPosition = rememberScrollPosition
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val disposable = Disposable { project.service<MyProjectService>() }

        val loader = JBLoadingPanel(GridLayout(), disposable)
        loader.add(scrollPane)
        loader.startLoading()

        val contentManager = toolWindow.contentManager
        val content = contentManager.factory.createContent(loader, null, false)
        content.setDisposer(disposable)

        contentManager.addContent(content)

        val myProjectService = project.service<MyProjectService>()
        val counts = myProjectService.totalAmount()
        val tab = createTab(counts)
        updateTab(tab)
        loader.stopLoading()
    }
}
