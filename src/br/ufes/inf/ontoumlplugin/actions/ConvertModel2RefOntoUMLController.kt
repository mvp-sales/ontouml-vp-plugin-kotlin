package br.ufes.inf.ontoumlplugin.actions

import RefOntoUML.util.RefOntoUMLResourceUtil
import br.ufes.inf.ontoumlplugin.model.createObservableWrapper
import br.ufes.inf.ontoumlplugin.project.AppConstants
import com.vp.plugin.ApplicationManager
import com.vp.plugin.action.VPAction
import com.vp.plugin.action.VPActionController
import io.reactivex.schedulers.Schedulers
import java.io.File
import javax.swing.JFileChooser
import javax.swing.JOptionPane
import javax.swing.filechooser.FileNameExtensionFilter

class ConvertModel2RefOntoUMLController : VPActionController {

    override fun update(p0: VPAction?) {

    }

    override fun performAction(p0: VPAction?) {
        val project = ApplicationManager.instance().projectManager.project
        val viewManager = ApplicationManager.instance().viewManager
        viewManager.clearMessages(AppConstants.PLUGIN_ID)

        createObservableWrapper(project)
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.trampoline())
            .subscribe(
                { wrapper ->
                    val fileChooser = viewManager.createJFileChooser()
                    val filter = FileNameExtensionFilter("Reference OntoUML (*.refontouml)", "refontouml")
                    fileChooser.fileFilter = filter
                    fileChooser.dialogTitle = "Select the destination directory"
                    fileChooser.dialogType = JFileChooser.SAVE_DIALOG
                    val returnValue = fileChooser.showSaveDialog(null)

                    if (returnValue == JFileChooser.APPROVE_OPTION) {
                        val outputFolder = fileChooser.currentDirectory
                        var fileName = fileChooser.selectedFile.name
                        if (!fileName.contains(".refontouml")) {
                            fileName += ".refontouml"
                        }
                        val file = File(outputFolder.getAbsolutePath() + File.separator + fileName)
                        if (file.exists()) {
                            val result = JOptionPane.showConfirmDialog(null,"The file exists, overwrite?","Existing file",JOptionPane.YES_NO_CANCEL_OPTION)
                            if (result == JOptionPane.YES_OPTION) {
                                RefOntoUMLResourceUtil.saveModel(file.absolutePath, wrapper.ontoUmlPackage)
                            }
                        }else{
                            RefOntoUMLResourceUtil.saveModel(file.absolutePath, wrapper.ontoUmlPackage)
                        }
                        viewManager.showMessage("Model saved at ${file.absolutePath}", AppConstants.PLUGIN_ID)
                    }
                },
                { err -> viewManager.showMessage(err.message, AppConstants.PLUGIN_ID) }
            )
    }
}