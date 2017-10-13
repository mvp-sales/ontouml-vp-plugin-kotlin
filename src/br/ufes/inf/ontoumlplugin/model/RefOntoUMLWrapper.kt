package br.ufes.inf.ontoumlplugin.model

import RefOntoUML.Classifier
import RefOntoUML.Generalization
import RefOntoUML.parser.SyntacticVerificator
import RefOntoUML.util.RefOntoUMLFactoryUtil
import com.vp.plugin.diagram.IDiagramUIModel
import com.vp.plugin.diagram.IShapeTypeConstants
import com.vp.plugin.model.IAssociation
import com.vp.plugin.model.IGeneralization
import com.vp.plugin.model.IGeneralizationSet
import com.vp.plugin.model.IModelElement
import io.reactivex.Observable

class RefOntoUMLWrapper {

    val classElements : MutableMap<IModelElement, RefOntoUML.Classifier>
    val ontoUmlPackage : RefOntoUML.Package

    init {
        this.ontoUmlPackage = RefOntoUMLFactoryUtil.createPackage("package")
        this.classElements = HashMap()
    }

    fun getOntoUMLClassifier(vpElement: IModelElement) : RefOntoUML.Classifier? {
        return classElements.get(vpElement)
    }

    fun addOntoUMLClassifier(vpElement: IModelElement, classifier: Classifier) {
        this.classElements.put(vpElement, classifier)
    }

    fun getOntoUMLClassFromName(className : String) : Classifier? {
        for (entry in this.classElements.entries){
            val elem = entry.key
            if (elem.name == className){
                return entry.value
            }
        }
        return null
    }
}

fun createObservableWrapper(vpDiagram : IDiagramUIModel) : io.reactivex.Observable<RefOntoUMLWrapper> {
    return Observable.fromCallable {
        createRefOntoUMLModel(vpDiagram)
    }
}

fun createRefOntoUMLModel(vpDiagram: IDiagramUIModel) : RefOntoUMLWrapper {
    var wrapper = RefOntoUMLWrapper()

    wrapper = addClasses(wrapper, vpDiagram)
    wrapper = addGeneralizations(wrapper, vpDiagram)
    wrapper = addGeneralizationSets(wrapper, vpDiagram)
    wrapper = addAssociations(wrapper, vpDiagram)

    return wrapper
}

fun addClasses(wrapper : RefOntoUMLWrapper, vpDiagram: IDiagramUIModel) : RefOntoUMLWrapper {
    for(classElement in vpDiagram.toDiagramElementArray(IShapeTypeConstants.SHAPE_TYPE_CLASS)) {
        val vpClass = classElement.metaModelElement
        val vpStereotype = if (vpClass.toStereotypeModelArray().size > 0) vpClass.toStereotypeModelArray()[0].name
                            else "Subkind"
        val ontoUmlClass = createOntoUmlClass(wrapper, vpClass, vpStereotype)
        wrapper.addOntoUMLClassifier(vpClass, ontoUmlClass)
    }
    return wrapper
}

fun addGeneralizations(wrapper: RefOntoUMLWrapper, vpDiagram: IDiagramUIModel) : RefOntoUMLWrapper {
    for(generalizationElement in vpDiagram.toDiagramElementArray(IShapeTypeConstants.SHAPE_TYPE_GENERALIZATION)){
        val vpGeneralization = generalizationElement.metaModelElement as IGeneralization
        if (vpGeneralization.generalizationSet == null){
            val parent = wrapper.getOntoUMLClassifier(vpGeneralization.from)
            val child = wrapper.getOntoUMLClassifier(vpGeneralization.to)
            RefOntoUMLFactoryUtil.createGeneralization(child, parent)
        }
    }
    return wrapper
}

fun addGeneralizationSets(wrapper: RefOntoUMLWrapper, vpDiagram: IDiagramUIModel) : RefOntoUMLWrapper {
    for (genSetElement in vpDiagram.toDiagramElementArray(IShapeTypeConstants.SHAPE_TYPE_GENERALIZATION_SET)){
        val vpGenSet = genSetElement.metaModelElement as IGeneralizationSet
        val genIterator = vpGenSet.generalizationIterator()
        val generalizations : MutableList<Generalization> = ArrayList()
        while (genIterator.hasNext()) {
            val gen = genIterator.next() as IGeneralization
            val generalization = RefOntoUMLFactoryUtil.createGeneralization(wrapper.getOntoUMLClassifier(gen.to), wrapper.getOntoUMLClassifier(gen.from))
            generalizations.add(generalization)
        }
        RefOntoUMLFactoryUtil.createGeneralizationSet(generalizations, vpGenSet.isDisjoint, vpGenSet.isCovering, wrapper.ontoUmlPackage)
    }
    return wrapper
}

fun addAssociations(wrapper: RefOntoUMLWrapper, vpDiagram: IDiagramUIModel) : RefOntoUMLWrapper {
    for (associationElement in vpDiagram.toDiagramElementArray(IShapeTypeConstants.SHAPE_TYPE_ASSOCIATION)) {
        val vpAssociation = associationElement.metaModelElement as IAssociation
        val vpStereotype = if (vpAssociation.toStereotypeArray().size > 0) vpAssociation.toStereotypeArray()[0]
                            else ""
        createOntoUmlAssociation(wrapper, vpAssociation, vpStereotype)
    }
    return wrapper
}

fun getVerificator(wrapper: RefOntoUMLWrapper) : Observable<SyntacticVerificator> {
    return Observable.fromCallable {
        val verificator = SyntacticVerificator()
        verificator.run(wrapper.ontoUmlPackage)
        verificator
    }
}