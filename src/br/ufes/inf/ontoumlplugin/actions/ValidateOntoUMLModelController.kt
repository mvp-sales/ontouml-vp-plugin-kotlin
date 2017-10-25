package br.ufes.inf.ontoumlplugin.actions

import br.ufes.inf.ontoumlplugin.model.createObservableWrapper
import br.ufes.inf.ontoumlplugin.model.getVerificator
import br.ufes.inf.ontoumlplugin.project.AppConstants
import com.vp.plugin.ApplicationManager
import com.vp.plugin.action.VPAction
import com.vp.plugin.action.VPActionController
import io.reactivex.schedulers.Schedulers

class ValidateOntoUMLModelController : VPActionController {

    override fun update(p0: VPAction?) {

    }

    override fun performAction(p0: VPAction?) {
        val project = ApplicationManager.instance().projectManager.project
        val viewManager = ApplicationManager.instance().viewManager

        viewManager.clearMessages(AppConstants.PLUGIN_ID)

        createObservableWrapper(project)
            .observeOn(Schedulers.computation())
            .flatMap { wrapper -> getVerificator(wrapper) }
            .observeOn(Schedulers.trampoline())
            .subscribe(
                { verificator ->
                    viewManager.showMessage(verificator.result, AppConstants.PLUGIN_ID)
                    for (elem in verificator.map.keys){
                        viewManager.showMessage(elem.toString(), AppConstants.PLUGIN_ID)
                        val values = ArrayList(verificator.map[elem])
                        for (message in values){
                            viewManager.showMessage(message, AppConstants.PLUGIN_ID)
                        }
                    }
                },
                { err ->
                    viewManager.showMessage(err.message, AppConstants.PLUGIN_ID)
                    err.printStackTrace()
                }
            )
    }
}