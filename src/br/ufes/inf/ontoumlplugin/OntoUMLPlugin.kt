package br.ufes.inf.ontoumlplugin

import br.ufes.inf.ontoumlplugin.project.OntoUMLPluginProjectListener
import com.vp.plugin.ApplicationManager
import com.vp.plugin.VPPlugin
import com.vp.plugin.VPPluginInfo

class OntoUMLPlugin : VPPlugin {

    private val projectListener : OntoUMLPluginProjectListener = OntoUMLPluginProjectListener()

    override fun unloaded() {
        ApplicationManager.instance().projectManager.project?.removeProjectListener(projectListener)
    }

    override fun loaded(p0: VPPluginInfo?) {
        ApplicationManager.instance().projectManager.project?.addProjectListener(projectListener)
    }
}