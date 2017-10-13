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
                if (b.text.toLowerCase() == text.toLowerCase()) {
                    return b
                }
            }
            return null
        }

        fun getStereotypeFromString(project: IProject, text: String): IStereotype? {
            val stereotypes = project.toModelElementArray(IModelElementFactory.MODEL_TYPE_STEREOTYPE)
            for (e in stereotypes) {
                val s = e as IStereotype
                if (s.name == text) {
                    return s
                }
            }
            return null
        }
    }

}