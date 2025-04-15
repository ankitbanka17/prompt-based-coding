package com.github.ankitbanka17.promptbasedcoding.handlers

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import javax.swing.SwingUtilities

object Notifier {
    fun showError(project: Project, message: String) {
        SwingUtilities.invokeLater {
            Messages.showMessageDialog(project, message, "Error", Messages.getErrorIcon())
        }
    }

    fun showInfo(project: Project, message: String) {
        SwingUtilities.invokeLater {
            Messages.showMessageDialog(project, message, "Success", Messages.getInformationIcon())
        }
    }
}
