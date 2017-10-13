package br.ufes.inf.ontoumlplugin.project

import com.vp.plugin.model.IProject
import com.vp.plugin.model.IProjectListener
import com.vp.plugin.model.IStereotype
import com.vp.plugin.model.factory.IModelElementFactory
import com.vp.plugin.model.ITaggedValueDefinition

class OntoUMLPluginProjectListener : IProjectListener {

    override fun projectNewed(p0: IProject?) {
        addOntoUMLStereotypes()
    }

    override fun projectPreSave(p0: IProject?) {

    }

    override fun projectSaved(p0: IProject?) {

    }

    override fun projectRenamed(p0: IProject?) {

    }

    override fun projectAfterOpened(p0: IProject?) {

    }

    override fun projectOpened(p0: IProject?) {

    }

    private fun addOntoUMLStereotypes() {
        addClassStereotypes()
        addNonPartWholeAssociationStereotypes()
        addPartWholeStereotypes()
    }

    private fun addClassStereotypes() {
        val classTypes = arrayOf("Kind", "Subkind", "Role", "Phase", "Category", "RoleMixin",
                                "Mixin", "Relator", "Mode", "Quality", "Collective", "Quantity",
                                "DataType", "PerceivableQuality", "NonPerceivableQuality","NominalQuality",
                                "PrimitiveType")

        for(classType in classTypes){
            val stereotype : IStereotype = IModelElementFactory.instance().createStereotype()
            stereotype.name = classType
            stereotype.baseType = IModelElementFactory.MODEL_TYPE_CLASS
        }

    }

    private fun addNonPartWholeAssociationStereotypes() {
        val associationTypes = arrayOf("Formal", "Mediation", "Material", "Derivation", "Characterization", "Structuration")

        for (associationType in associationTypes) {
            val stereotype = IModelElementFactory.instance().createStereotype()
            stereotype.name = associationType
            stereotype.baseType = IModelElementFactory.MODEL_TYPE_ASSOCIATION
        }

    }

    private fun addPartWholeStereotypes() {

        addPartWholeStereotype("ComponentOf", "shareable", "essential", "inseparable", "immutableWhole", "immutablePart")
        addPartWholeStereotype("MemberOf", "shareable", "essential", "inseparable", "immutableWhole", "immutablePart")
        addPartWholeStereotype("SubCollectionOf", "shareable", "essential", "inseparable", "immutableWhole", "immutablePart")
        addPartWholeStereotype("SubQuantityOf", "shareable", "essential", "inseparable", "immutableWhole", "immutablePart")

    }

    private fun addPartWholeStereotype(name: String, vararg taggedValues: String) {
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