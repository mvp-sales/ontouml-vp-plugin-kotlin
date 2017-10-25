package br.ufes.inf.ontoumlplugin.project

import br.ufes.inf.ontoumlplugin.OntoUMLPlugin
import com.vp.plugin.ApplicationManager
import com.vp.plugin.model.IProject
import com.vp.plugin.model.IProjectListener
import com.vp.plugin.model.IStereotype
import com.vp.plugin.model.factory.IModelElementFactory
import com.vp.plugin.model.ITaggedValueDefinition

class OntoUMLPluginProjectListener : IProjectListener {

    val viewManager = ApplicationManager.instance().viewManager
    private var stateProjectListener = AppConstants.UNSPECIFIED

    override fun projectNewed(p0: IProject?) {
        stateProjectListener = AppConstants.NEWED
    }

    override fun projectPreSave(p0: IProject?) {
        stateProjectListener = AppConstants.PRESAVE
    }

    override fun projectSaved(p0: IProject?) {
        stateProjectListener = AppConstants.SAVED
    }

    override fun projectRenamed(p0: IProject?) {
        if (stateProjectListener == AppConstants.NEWED) {
            val stereotypes = HashMap<String, IStereotype>()

            p0?.toAllLevelModelElementArray(IModelElementFactory.MODEL_TYPE_STEREOTYPE)?.forEach {
                val stereotype = it as IStereotype
                stereotypes.put(stereotype.name, stereotype)
            }
            addOntoUMLStereotypes(stereotypes)
        }

        stateProjectListener = AppConstants.RENAMED
    }

    override fun projectAfterOpened(p0: IProject?) {
        stateProjectListener = AppConstants.AFTER_OPENED
    }

    override fun projectOpened(p0: IProject?) {
        stateProjectListener = AppConstants.OPENED
    }

    private fun addOntoUMLStereotypes(stereotypes: Map<String, IStereotype>) {
        addClassStereotypes(stereotypes)
        addNonPartWholeAssociationStereotypes(stereotypes)
        addPartWholeStereotypes(stereotypes)
        viewManager.showMessage("OntoUML Stereotypes loaded successfully.", AppConstants.PLUGIN_ID)
    }

    private fun addClassStereotypes(stereotypes: Map<String, IStereotype>) {
        val classTypes = arrayOf("Kind", "Subkind", "Role", "Phase", "Category", "RoleMixin",
                                "Mixin", "Relator", "Mode", "Quality", "Collective", "Quantity",
                                "DataType", "PerceivableQuality", "NonPerceivableQuality","NominalQuality",
                                "PrimitiveType")

        classTypes.forEach {
            if (stereotypes.containsKey(it)){
                val stereotype = stereotypes[it]
                if (stereotype?.baseType !== IModelElementFactory.MODEL_TYPE_CLASS){
                    viewManager.showMessage("Stereotype $it already existent with base type ${stereotype?.baseType}", AppConstants.PLUGIN_ID)
                }
            }else{
                val stereotype: IStereotype = IModelElementFactory.instance().createStereotype()
                stereotype.name = it
                stereotype.baseType = IModelElementFactory.MODEL_TYPE_CLASS
            }
        }

    }

    private fun addNonPartWholeAssociationStereotypes(stereotypes: Map<String, IStereotype>) {
        val associationTypes = arrayOf("FormalAssociation", "Mediation", "MaterialAssociation", "Derivation", "Characterization", "Structuration")

        associationTypes.forEach {
            if (stereotypes.containsKey(it)){
                val stereotype = stereotypes[it]
                if (stereotype?.baseType !== IModelElementFactory.MODEL_TYPE_ASSOCIATION){
                    viewManager.showMessage("Stereotype $it already existent with base type ${stereotype?.baseType}", AppConstants.PLUGIN_ID)
                }
            }else{
                val stereotype: IStereotype = IModelElementFactory.instance().createStereotype()
                stereotype.name = it
                stereotype.baseType = IModelElementFactory.MODEL_TYPE_ASSOCIATION
            }
        }

    }

    private fun addPartWholeStereotypes(stereotypes: Map<String, IStereotype>) {

        addPartWholeStereotype(stereotypes,"ComponentOf", "shareable", "essential", "inseparable", "immutableWhole", "immutablePart")
        addPartWholeStereotype(stereotypes,"MemberOf", "shareable", "essential", "inseparable", "immutableWhole", "immutablePart")
        addPartWholeStereotype(stereotypes,"SubCollectionOf", "shareable", "essential", "inseparable", "immutableWhole", "immutablePart")
        addPartWholeStereotype(stereotypes, "SubQuantityOf", "shareable", "essential", "inseparable", "immutableWhole", "immutablePart")

    }

    private fun addPartWholeStereotype(stereotypes: Map<String, IStereotype>, name: String, vararg taggedValues: String) {
        if (stereotypes.containsKey(name)){
            return
        }

        val stereotype = IModelElementFactory.instance().createStereotype()
        stereotype.name = name
        stereotype.baseType = IModelElementFactory.MODEL_TYPE_ASSOCIATION

        val taggedValueDefinitionContainer = IModelElementFactory.instance().createTaggedValueDefinitionContainer()

        for (taggedValue in taggedValues) {
            val taggedValueDefinition = taggedValueDefinitionContainer.createTaggedValueDefinition()
            taggedValueDefinition.type = ITaggedValueDefinition.TYPE_BOOLEAN
            taggedValueDefinition.name = taggedValue
        }

        stereotype.taggedValueDefinitions = taggedValueDefinitionContainer
    }
}

object AppConstants {
    const val UNSPECIFIED = 0
    const val NEWED = 1
    const val PRESAVE = 2
    const val SAVED = 3
    const val RENAMED = 4
    const val OPENED = 5
    const val AFTER_OPENED = 6
    const val PLUGIN_ID = "br.ufes.inf.ontoumlplugin"
}