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
        val diagram = ApplicationManager.instance().diagramManager.activeDiagram
        val viewManager = ApplicationManager.instance().viewManager

        createObservableWrapper(diagram)
            .observeOn(Schedulers.computation())
            .flatMap { wrapper -> getVerificator(wrapper) }
            .observeOn(Schedulers.trampoline())
            .subscribe(
                { verificator ->
                    for (elem in verificator.map.keys){
                        viewManager.showMessage(elem.toString())
                        val values = ArrayList(verificator.map[elem])
                        for (message in values){
                            viewManager.showMessage(message)
                        }
                    }
                },
                { err -> viewManager.showMessage(err.message) }
            )
    }
}