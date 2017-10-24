package br.ufes.inf.ontoumlplugin.model

import RefOntoUML.Classifier
import com.vp.plugin.model.IAssociation
import com.vp.plugin.model.IClass
import com.vp.plugin.model.IProject
import com.vp.plugin.model.IStereotype
import com.vp.plugin.model.factory.IModelElementFactory

fun setClassStereotype(vpClass: IClass, ontoUmlElement: RefOntoUML.Classifier, project : IProject) : IClass {
    if (ontoUmlElement is RefOntoUML.Kind){
        addStereotypeClass(vpClass, "Kind", project)
    }else if (ontoUmlElement is RefOntoUML.SubKind){
        addStereotypeClass(vpClass, "SubKind", project)
    }else if(ontoUmlElement is RefOntoUML.Role){
        addStereotypeClass(vpClass, "Role", project);
    }else if(ontoUmlElement is RefOntoUML.Phase){
        addStereotypeClass(vpClass, "Phase", project);
    }else if(ontoUmlElement is RefOntoUML.Relator){
        addStereotypeClass(vpClass, "Relator", project);
    }else if(ontoUmlElement is RefOntoUML.RoleMixin){
        addStereotypeClass(vpClass, "RoleMixin", project);
    }else if(ontoUmlElement is RefOntoUML.Category){
        addStereotypeClass(vpClass, "Category", project);
    }else if(ontoUmlElement is RefOntoUML.Quantity){
        addStereotypeClass(vpClass, "Quantity", project);
    }else if(ontoUmlElement is RefOntoUML.Collective){
        addStereotypeClass(vpClass, "Collective", project);
    }else if(ontoUmlElement is RefOntoUML.Mixin){
        addStereotypeClass(vpClass, "Mixin", project);
    }else if(ontoUmlElement is RefOntoUML.Mode){
        addStereotypeClass(vpClass, "Mode", project);
    }else if(ontoUmlElement is RefOntoUML.PerceivableQuality){
        addStereotypeClass(vpClass, "PerceivableQuality", project);
    }else if(ontoUmlElement is RefOntoUML.NonPerceivableQuality){
        addStereotypeClass(vpClass, "NonPerceivableQuality", project);
    }else if(ontoUmlElement is RefOntoUML.NominalQuality){
        addStereotypeClass(vpClass, "NominalQuality", project);
    }else if(ontoUmlElement is RefOntoUML.Quality){
        addStereotypeClass(vpClass, "Quality", project);
    }else if(ontoUmlElement is RefOntoUML.PrimitiveType){
        addStereotypeClass(vpClass, "PrimitiveType", project);
    }else if(ontoUmlElement is RefOntoUML.DataType){
        addStereotypeClass(vpClass, "DataType", project);
    }

    val newVpClass = setVPAttributes(vpClass, ontoUmlElement)

    return newVpClass
}

fun addStereotypeClass(vpClass: IClass, stereotypeStr : String, project: IProject) {
    val stereotype = OntoUMLClassType.getStereotypeFromString(project, stereotypeStr)
    vpClass.addStereotype(stereotype)
}

fun setVPAttributes(vpClass: IClass, ontoUmlElement: Classifier) : IClass {
    for (attribute in ontoUmlElement.attribute) {
        val vpAttribute = IModelElementFactory.instance().createAttribute()
        vpAttribute.name = attribute.name
        vpAttribute.setType(attribute.type.name)
        val multiplicity = AssociationMultiplicity(attribute.lower, attribute.upper)
        vpAttribute.multiplicity = multiplicity.strMult
        vpClass.addAttribute(vpAttribute)
    }

    return vpClass
}

fun setMeronymicAssociation(vpAssociation : IAssociation, ontoUmlAssociation : RefOntoUML.Meronymic, project : IProject) : IAssociation {
    val stereotype : IStereotype?

    if(ontoUmlAssociation is RefOntoUML.memberOf){
        stereotype = addStereotypeAssociation(vpAssociation, "MemberOf", project);
    }else if(ontoUmlAssociation is RefOntoUML.componentOf){
        stereotype = addStereotypeAssociation(vpAssociation, "ComponentOf", project);
    }else if(ontoUmlAssociation is RefOntoUML.subQuantityOf){
        stereotype = addStereotypeAssociation(vpAssociation, "subQuantityOf", project);
    }else{
        stereotype = addStereotypeAssociation(vpAssociation, "subCollectionOf", project);
    }

    if (stereotype != null){
        val container = vpAssociation.taggedValues
        val inseparable = container.getTaggedValueByName("inseparable")
        val immutableWhole = container.getTaggedValueByName("immutableWhole")
        val immutablePart = container.getTaggedValueByName("immutablePart")
        val essential = container.getTaggedValueByName("essential")
        val shareable = container.getTaggedValueByName("shareable")

        inseparable.setValue(if (ontoUmlAssociation.isIsInseparable) "True" else "False")
        immutableWhole.setValue(if (ontoUmlAssociation.isIsImmutableWhole) "True" else "False")
        immutablePart.setValue(if (ontoUmlAssociation.isIsImmutablePart) "True" else "False")

        essential?.setValue(if (ontoUmlAssociation.isIsEssential) "True" else "False")

        shareable.setValue(if (ontoUmlAssociation.isIsShareable) "True" else "False")
    }

    return vpAssociation
}

fun addStereotypeAssociation(vpAssociation: IAssociation, stereotypeStr: String, project: IProject) : IStereotype? {
    val stereotype = OntoUMLRelationshipType.getStereotypeFromString(project, stereotypeStr)
    vpAssociation.addStereotype(stereotype)

    return stereotype
}

fun setAssociationStereotype(vpAssociation: IAssociation, ontoUmlAssociation : RefOntoUML.Association, project: IProject) : IAssociation {
    if(ontoUmlAssociation is RefOntoUML.FormalAssociation){
        addStereotypeAssociation(vpAssociation, "FormalAssociation", project);
    }else if(ontoUmlAssociation is RefOntoUML.Mediation){
        addStereotypeAssociation(vpAssociation, "Mediation", project);
    }else if(ontoUmlAssociation is RefOntoUML.Characterization){
        addStereotypeAssociation(vpAssociation, "Characterization", project);
    }else if(ontoUmlAssociation is RefOntoUML.Derivation){
        addStereotypeAssociation(vpAssociation, "Derivation", project);
    }else if(ontoUmlAssociation is RefOntoUML.Structuration){
        addStereotypeAssociation(vpAssociation, "Structuration", project);
    }

    return vpAssociation;
}
