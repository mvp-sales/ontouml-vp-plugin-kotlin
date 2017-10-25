package br.ufes.inf.ontoumlplugin.model

import com.vp.plugin.model.IProject
import com.vp.plugin.model.IStereotype
import com.vp.plugin.model.factory.IModelElementFactory

enum class OntoUMLRelationshipType(val text: String) {
    FORMAL_ASSOCIATION("FormalAssociation"), MEDIATION("Mediation"), CHARACTERIZATION("Characterization"),
    DERIVATION("Derivation"), MATERIAL_ASSOCIATION("MaterialAssociation"),COMPONENT_OF("ComponentOf"),
    MEMBER_OF("MemberOf"), SUBCOLLECTION_OF("SubCollectionOf"), SUBQUANTITY_OF("SubQuantityOf"),
    STRUCTURATION("Structuration");

    companion object {
        fun fromString(text: String): OntoUMLRelationshipType? {
            for (b in OntoUMLRelationshipType.values()) {
                if (b.text.equals(text, true)) {
                    return b
                }
            }
            return null
        }

        fun getStereotypeFromString(project: IProject, text: String): IStereotype? {
            val stereotypes = project.toAllLevelModelElementArray(IModelElementFactory.MODEL_TYPE_STEREOTYPE)
            stereotypes.forEach {
                val stereotype = it as IStereotype
                if (stereotype.baseType == IModelElementFactory.MODEL_TYPE_ASSOCIATION &&
                        stereotype.name.equals(text, true)) {
                    return stereotype
                }
            }
            return null
        }
    }

}