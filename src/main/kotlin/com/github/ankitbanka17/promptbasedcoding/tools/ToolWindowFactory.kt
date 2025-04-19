package com.github.ankitbanka17.promptbasedcoding

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.github.ankitbanka17.promptbasedcoding.tools.MyToolWindow

class MyToolWindowFactory : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        // Create an instance of your MyToolWindow
        val myToolWindow = MyToolWindow(project)

        // Add the content to the ToolWindow
        val contentManager = toolWindow.contentManager
        val content = ContentFactory.getInstance().createContent(myToolWindow.getContent(), "", false)
        contentManager.addContent(content)
    }


}
