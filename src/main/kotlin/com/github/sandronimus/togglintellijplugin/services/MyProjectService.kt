package com.github.sandronimus.togglintellijplugin.services

import com.intellij.openapi.project.Project
import com.github.sandronimus.togglintellijplugin.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
