package br.ufes.inf.ontoumlplugin.actions

import br.ufes.inf.ontoumlplugin.model.createObservableWrapper
import br.ufes.inf.ontoumlplugin.model.getVerificator
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

        viewManager.clearMessages("br.ufes.inf.ontoumlplugin")

        createObservableWrapper(project)
            .observeOn(Schedulers.computation())
            .flatMap { wrapper -> getVerificator(wrapper) }
            .observeOn(Schedulers.trampoline())
            .subscribe(
                { verificator ->
                    viewManager.showMessage(verificator.result, "br.ufes.inf.ontoumlplugin")
                    for (elem in verificator.map.keys){
                        viewManager.showMessage(elem.toString(), "br.ufes.inf.ontoumlplugin")
                        val values = ArrayList(verificator.map[elem])
                        for (message in values){
                            viewManager.showMessage(message, "br.ufes.inf.ontoumlplugin")
                        }
                    }
                },
                { err ->
                    viewManager.showMessage(err.message, "br.ufes.inf.ontoumlplugin")
                    err.printStackTrace()
                }
            )
    }
}