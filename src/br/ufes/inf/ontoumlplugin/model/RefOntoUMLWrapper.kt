package br.ufes.inf.ontoumlplugin.model

import RefOntoUML.Classifier
import RefOntoUML.Generalization
import RefOntoUML.parser.SyntacticVerificator
import RefOntoUML.util.RefOntoUMLFactoryUtil
import com.vp.plugin.model.*
import com.vp.plugin.model.factory.IModelElementFactory
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

fun createObservableWrapper(vpProject: IProject) : io.reactivex.Observable<RefOntoUMLWrapper> {
    return Observable.fromCallable {
        createRefOntoUMLModel(vpProject)
    }
}

fun createRefOntoUMLModel(vpProject: IProject) : RefOntoUMLWrapper {
    var wrapper = RefOntoUMLWrapper()

    wrapper = addClasses(wrapper, vpProject)
    wrapper = addGeneralizations(wrapper, vpProject)
    wrapper = addGeneralizationSets(wrapper, vpProject)
    wrapper = addAssociations(wrapper, vpProject)

    return wrapper
}

fun addClasses(wrapper: RefOntoUMLWrapper, vpProject: IProject) : RefOntoUMLWrapper {
    vpProject.toAllLevelModelElementArray(IModelElementFactory.MODEL_TYPE_CLASS).forEach {
        val vpClass = it as IClass
        val vpStereotype = if (vpClass.toStereotypeModelArray().size > 0) vpClass.toStereotypeModelArray()[0].name
        else "Subkind"
        val ontoUmlClass = createOntoUmlClass(wrapper, vpClass, vpStereotype)
        wrapper.addOntoUMLClassifier(vpClass, ontoUmlClass)
    }
    return wrapper
}

fun addGeneralizations(wrapper: RefOntoUMLWrapper, vpProject: IProject) : RefOntoUMLWrapper {
    vpProject.toAllLevelModelElementArray(IModelElementFactory.MODEL_TYPE_GENERALIZATION).forEach {
        val vpGeneralization = it as IGeneralization
        if (vpGeneralization.generalizationSet == null){
            val parent = wrapper.getOntoUMLClassifier(vpGeneralization.from)
            val child = wrapper.getOntoUMLClassifier(vpGeneralization.to)
            RefOntoUMLFactoryUtil.createGeneralization(child, parent)
        }
    }
    return wrapper
}

fun addGeneralizationSets(wrapper: RefOntoUMLWrapper, vpProject: IProject) : RefOntoUMLWrapper {
    vpProject.toAllLevelModelElementArray(IModelElementFactory.MODEL_TYPE_GENERALIZATION_SET).forEach {
        val vpGenSet = it as IGeneralizationSet
        val genIterator = vpGenSet.generalizationIterator()
        val generalizations : MutableList<Generalization> = ArrayList()
        genIterator.forEach {
            val gen = it as IGeneralization
            val generalization = RefOntoUMLFactoryUtil.createGeneralization(wrapper.getOntoUMLClassifier(gen.to), wrapper.getOntoUMLClassifier(gen.from))
            generalizations.add(generalization)
        }
        RefOntoUMLFactoryUtil.createGeneralizationSet(generalizations, vpGenSet.isDisjoint, vpGenSet.isCovering, wrapper.ontoUmlPackage)
    }
    return wrapper
}

fun addAssociations(wrapper: RefOntoUMLWrapper, vpProject: IProject) : RefOntoUMLWrapper {
    vpProject.toAllLevelModelElementArray(IModelElementFactory.MODEL_TYPE_ASSOCIATION).forEach {
        val vpAssociation = it as IAssociation
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