package br.ufes.inf.ontoumlplugin.actions

import RefOntoUML.*
import RefOntoUML.parser.OntoUMLParser
import RefOntoUML.util.RefOntoUMLResourceUtil
import br.ufes.inf.ontoumlplugin.model.setClassStereotype
import com.vp.plugin.ApplicationManager
import com.vp.plugin.DiagramManager
import com.vp.plugin.action.VPAction
import com.vp.plugin.action.VPActionController
import com.vp.plugin.diagram.IClassDiagramUIModel
import com.vp.plugin.diagram.IDiagramElement
import com.vp.plugin.diagram.IDiagramTypeConstants
import com.vp.plugin.model.IGeneralization
import com.vp.plugin.model.IModelElement
import com.vp.plugin.model.factory.IModelElementFactory
import com.vp.plugin.model.IAssociationEnd
import br.ufes.inf.ontoumlplugin.model.setAssociationStereotype
import br.ufes.inf.ontoumlplugin.model.setMeronymicAssociation
import br.ufes.inf.ontoumlplugin.project.AppConstants
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

class LoadOntoUMLModelController : VPActionController {

    private val project = ApplicationManager.instance().projectManager.project
    private val ontoUml2VpClasses : MutableMap<Classifier, IModelElement>

    init {
        this.ontoUml2VpClasses = HashMap()
    }

    override fun update(p0: VPAction?) {

    }

    override fun performAction(p0: VPAction?) {
        val viewManager = ApplicationManager.instance().viewManager
        viewManager.clearMessages(AppConstants.PLUGIN_ID)

        val fileChooser = ApplicationManager.instance().viewManager.createJFileChooser()
        val filter = FileNameExtensionFilter("Reference OntoUML (*.refontouml)", "refontouml")
        fileChooser.fileFilter = filter
        fileChooser.dialogTitle = "Selecione o arquivo RefOntoUML"
        fileChooser.dialogType = JFileChooser.OPEN_DIALOG
        val returnValue = fileChooser.showOpenDialog(null)

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            val file = fileChooser.selectedFile
            try {
                val model = RefOntoUMLResourceUtil.loadModel(file.absolutePath)
                val ontoUmlPackage = model.contents[0] as Package
                buildClassDiagram(ontoUmlPackage)
                viewManager.showMessage("Model loaded successfully", AppConstants.PLUGIN_ID)
            } catch (e : Exception) {
                viewManager.showMessage(e.message, AppConstants.PLUGIN_ID)
            }
        }

    }

    private fun buildClassDiagram(ontoUmlPackage : Package) {
        val parser = OntoUMLParser(ontoUmlPackage)

        for (c in parser.rigidClasses) {
            createClass(c)
        }

        for (c in parser.antiRigidClasses) {
            createClass(c)
        }

        for(primitive in parser.getAllInstances(PrimitiveType::class.java)) {
            createClass(primitive)
        }

        for (a in parser.associations) {
            val association = a as Association
            if (association is Meronymic) {
                createMeronymicAssociation(association)
            }else{
                createAssociation(association)
            }
        }

        for (genSet in parser.getAllInstances(GeneralizationSet::class.java)) {
            createGeneralizationSet(genSet)
        }

        for (gen in parser.getAllInstances(Generalization::class.java)) {
            if (!isGeneralizationInsideGenSet(parser, gen)) {
                createGeneralization(gen)
            }
        }
    }

    private fun createClass(classifier: Classifier) {
        var vpClass = IModelElementFactory.instance().createClass()
        vpClass.name = classifier.name
        this.ontoUml2VpClasses.put(classifier, vpClass)

        vpClass = setClassStereotype(vpClass, classifier, this.project)
    }

    private fun createMeronymicAssociation(association: Meronymic) {
        val wholeEnd = association.wholeEnd()
        val partEnd = association.partEnd()

        val whole = association.whole()
        val part = association.part()
        val lowerPart = partEnd.lower
        val upperC1 = partEnd.upper
        val lowerWhole = wholeEnd.lower
        val upperC2 = wholeEnd.upper

        val wholeVp = this.ontoUml2VpClasses[whole]
        val partVp = this.ontoUml2VpClasses[part]

        // create normal association between subclass to "ClassWithAssociation"
        var associationModel = IModelElementFactory.instance().createAssociation()
        associationModel.from = partVp
        associationModel.to = wholeVp
        // specify multiplicity for from & to end
        val associationFromEnd = associationModel.fromEnd as IAssociationEnd
        associationFromEnd.multiplicity = getMultiplicityFromValues(lowerPart, upperC1)
        val associationToEnd = associationModel.toEnd as IAssociationEnd
        associationToEnd.multiplicity = getMultiplicityFromValues(lowerWhole, upperC2)
        associationToEnd.aggregationKind = if (association.isIsShareable) IAssociationEnd.AGGREGATION_KIND_AGGREGATION else IAssociationEnd.AGGREGATION_KIND_COMPOSITED

        associationModel = setMeronymicAssociation(associationModel, association, this.project)
    }

    private fun createAssociation(association: Association) {
        val p1 = association.ownedEnd[0]
        val p2 = association.ownedEnd[1]

        val c1 = p1.type as Classifier
        val c2 = p2.type as Classifier

        val lowerC1 = p1.lower
        val upperC1 = p1.upper
        val lowerC2 = p2.lower
        val upperC2 = p2.upper

        val fromClass = this.ontoUml2VpClasses[c1]
        val toClass = this.ontoUml2VpClasses[c2]

        var associationModel = IModelElementFactory.instance().createAssociation()
        associationModel.from = fromClass
        associationModel.to = toClass

        val associationFromEnd = associationModel.fromEnd as IAssociationEnd
        associationFromEnd.multiplicity = getMultiplicityFromValues(lowerC1, upperC1)
        val associationToEnd = associationModel.toEnd as IAssociationEnd
        associationToEnd.multiplicity = getMultiplicityFromValues(lowerC2, upperC2)

        associationModel = setAssociationStereotype(associationModel, association, this.project)
    }

    private fun getMultiplicityFromValues(lower : Int, upper : Int) : String {
        var result = (if (lower == -1) "*" else lower.toString()) + ".." + if (upper == -1) "*" else upper
        if (lower == 0) {
            if (upper == 1) {
                result = IAssociationEnd.MULTIPLICITY_ZERO_TO_ONE
            } else if (upper == -1) {
                result = IAssociationEnd.MULTIPLICITY_ZERO_TO_MANY
            }
        } else if (lower == 1) {
            if (upper == 1) {
                result = IAssociationEnd.MULTIPLICITY_ONE
            } else if (upper == -1) {
                result = IAssociationEnd.MULTIPLICITY_ONE_TO_MANY
            }
        } else if (lower == upper) {
            result = if (lower == -1) "*" else lower.toString()
        }
        return result
    }

    private fun createGeneralizationSet(genSet: GeneralizationSet) {
        val vpGenSet = IModelElementFactory.instance().createGeneralizationSet()
        vpGenSet.isDisjoint = genSet.isIsDisjoint
        vpGenSet.isCovering = genSet.isIsCovering

        for (gen in genSet.generalization) {
            vpGenSet.addGeneralization(createGeneralization(gen))
        }
    }

    private fun createGeneralization(gen: Generalization) : IGeneralization {
        val generalizationModel = IModelElementFactory.instance().createGeneralization()
        val specific = this.ontoUml2VpClasses[gen.specific]
        val general = this.ontoUml2VpClasses[gen.general]

        generalizationModel.from = general
        generalizationModel.to = specific

        return generalizationModel
    }

    private fun isGeneralizationInsideGenSet(parser : OntoUMLParser, gen : Generalization) : Boolean {
        for (genSet in parser.getAllInstances(GeneralizationSet::class.java)) {
            if (genSet.generalization.contains(gen)) return true
        }
        return false
    }


}