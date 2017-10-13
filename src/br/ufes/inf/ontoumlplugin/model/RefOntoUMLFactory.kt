package br.ufes.inf.ontoumlplugin.model

import RefOntoUML.Association
import RefOntoUML.Classifier
import RefOntoUML.util.RefOntoUMLFactoryUtil
import com.vp.plugin.model.*
import com.vp.plugin.model.IAssociationEnd

fun createOntoUmlClass(wrapper: RefOntoUMLWrapper, vpElement: IModelElement, stereotypeStr : String) : Classifier {
    var classifier: RefOntoUML.Classifier
    val classType = OntoUMLClassType.fromString(stereotypeStr)

    when (classType) {
        OntoUMLClassType.KIND -> classifier = RefOntoUMLFactoryUtil.createKind(vpElement.name, wrapper.ontoUmlPackage)
        OntoUMLClassType.SUBKIND -> classifier = RefOntoUMLFactoryUtil.createSubKind(vpElement.name, wrapper.ontoUmlPackage)
        OntoUMLClassType.ROLE -> classifier = RefOntoUMLFactoryUtil.createRole(vpElement.name, wrapper.ontoUmlPackage)
        OntoUMLClassType.PHASE -> classifier = RefOntoUMLFactoryUtil.createPhase(vpElement.name, wrapper.ontoUmlPackage)
        OntoUMLClassType.COLLECTIVE -> classifier = RefOntoUMLFactoryUtil.createCollective(vpElement.name, wrapper.ontoUmlPackage)
        OntoUMLClassType.QUANTITY -> classifier = RefOntoUMLFactoryUtil.createQuantity(vpElement.name, wrapper.ontoUmlPackage)
        OntoUMLClassType.CATEGORY -> classifier = RefOntoUMLFactoryUtil.createCategory(vpElement.name, wrapper.ontoUmlPackage)
        OntoUMLClassType.ROLEMIXIN -> classifier = RefOntoUMLFactoryUtil.createRoleMixin(vpElement.name, wrapper.ontoUmlPackage)
        OntoUMLClassType.RELATOR -> classifier = RefOntoUMLFactoryUtil.createRelator(vpElement.name, wrapper.ontoUmlPackage)
        OntoUMLClassType.MIXIN -> classifier = RefOntoUMLFactoryUtil.createMixin(vpElement.name, wrapper.ontoUmlPackage)
        OntoUMLClassType.MODE -> classifier = RefOntoUMLFactoryUtil.createMode(vpElement.name, wrapper.ontoUmlPackage)
        OntoUMLClassType.PERCEIVABLE_QUALITY -> classifier = RefOntoUMLFactoryUtil.createPerceivableQuality(vpElement.name, wrapper.ontoUmlPackage)
        OntoUMLClassType.NON_PERCEIVABLE_QUALITY -> classifier = RefOntoUMLFactoryUtil.createNonPerceivableQuality(vpElement.name, wrapper.ontoUmlPackage)
        OntoUMLClassType.NOMINAL_QUALITY -> classifier = RefOntoUMLFactoryUtil.createNominalQuality(vpElement.name, wrapper.ontoUmlPackage)
        OntoUMLClassType.DATA_TYPE -> classifier = RefOntoUMLFactoryUtil.createDataType(vpElement.name, wrapper.ontoUmlPackage)
        OntoUMLClassType.PRIMITIVE_TYPE -> classifier = RefOntoUMLFactoryUtil.createPrimitiveType(vpElement.name, wrapper.ontoUmlPackage)
        else -> classifier = RefOntoUMLFactoryUtil.createSubKind(vpElement.name, wrapper.ontoUmlPackage)
    }

    classifier = addOntoUMLAttributes(classifier, wrapper, vpElement)

    return classifier
}

fun addOntoUMLAttributes(classifier: Classifier, wrapper: RefOntoUMLWrapper, vpElement : IModelElement) : Classifier {
    val vpClass = vpElement as IClass
    for (attribute in vpClass.toAttributeArray()) {
        val className = attribute.typeAsString
        val attributeClassifier = wrapper.getOntoUMLClassFromName(className)
        val multiplicity = AssociationMultiplicity(attribute.multiplicity)
        RefOntoUMLFactoryUtil.createAttribute(classifier,
                                            attributeClassifier,
                                            multiplicity.minMult,
                                            multiplicity.maxMult,
                                            attribute.name, false)
    }

    return classifier
}

fun createOntoUmlAssociation(wrapper: RefOntoUMLWrapper, vpAssociation: IAssociation, stereotypeStr: String) : Association {
    val association: Association
    val relationType = OntoUMLRelationshipType.fromString(stereotypeStr)

    when (relationType) {
        OntoUMLRelationshipType.CHARACTERIZATION, OntoUMLRelationshipType.MEDIATION, OntoUMLRelationshipType.FORMAL_ASSOCIATION, OntoUMLRelationshipType.MATERIAL_ASSOCIATION, OntoUMLRelationshipType.STRUCTURATION ->
            association = createCommonAssociation(wrapper, vpAssociation, relationType)
        OntoUMLRelationshipType.COMPONENT_OF, OntoUMLRelationshipType.MEMBER_OF, OntoUMLRelationshipType.SUBQUANTITY_OF, OntoUMLRelationshipType.SUBCOLLECTION_OF ->
            association = createMeronymicAssociation(wrapper, vpAssociation, relationType)
        else -> association = createCommonAssociation(wrapper, vpAssociation, relationType)
    }

    return association
}

fun createCommonAssociation(wrapper: RefOntoUMLWrapper, vpAssociation: IAssociation, type : OntoUMLRelationshipType?) : Association {
    val association: Association

    val source = wrapper.getOntoUMLClassifier(vpAssociation.from)
    val target = wrapper.getOntoUMLClassifier(vpAssociation.to)

    val assEndFrom = vpAssociation.fromEnd as IAssociationEnd
    val assEndTo = vpAssociation.toEnd as IAssociationEnd

    val multFrom = AssociationMultiplicity(assEndFrom.multiplicity)
    val multTo = AssociationMultiplicity(assEndTo.multiplicity)

    when (type) {
        OntoUMLRelationshipType.CHARACTERIZATION ->
            if (target is RefOntoUML.Mode) {
                association = RefOntoUMLFactoryUtil.createCharacterization(target,
                        multTo.minMult,
                        multTo.maxMult,
                        vpAssociation.name,
                        source,
                        multFrom.minMult,
                        multFrom.maxMult,
                        wrapper.ontoUmlPackage)
            } else {
                association = RefOntoUMLFactoryUtil.createCharacterization(source,
                        multFrom.minMult,
                        multFrom.maxMult,
                        vpAssociation.name,
                        target,
                        multTo.minMult,
                        multTo.maxMult,
                        wrapper.ontoUmlPackage)
            }
        OntoUMLRelationshipType.MEDIATION ->
            if (target is RefOntoUML.Relator) {
                association = RefOntoUMLFactoryUtil.createMediation(target,
                        multTo.minMult,
                        multTo.maxMult,
                        vpAssociation.name,
                        source,
                        multFrom.minMult,
                        multFrom.maxMult,
                        wrapper.ontoUmlPackage)
            } else {
                association = RefOntoUMLFactoryUtil.createMediation(source,
                        multFrom.minMult,
                        multFrom.maxMult,
                        vpAssociation.name,
                        target,
                        multTo.minMult,
                        multTo.maxMult,
                        wrapper.ontoUmlPackage)
            }
        OntoUMLRelationshipType.FORMAL_ASSOCIATION ->
            association = RefOntoUMLFactoryUtil.createFormalAssociation(source,
                multFrom.minMult,
                multFrom.maxMult,
                vpAssociation.name,
                target,
                multTo.minMult,
                multTo.maxMult,
                wrapper.ontoUmlPackage)
        OntoUMLRelationshipType.MATERIAL_ASSOCIATION ->
            association = RefOntoUMLFactoryUtil.createMaterialAssociation(source,
                multFrom.minMult,
                multFrom.maxMult,
                vpAssociation.name,
                target,
                multTo.minMult,
                multTo.maxMult,
                wrapper.ontoUmlPackage)
        OntoUMLRelationshipType.STRUCTURATION ->
            association = RefOntoUMLFactoryUtil.createStructuration(source,
                multFrom.minMult,
                multFrom.maxMult,
                vpAssociation.name,
                target,
                multTo.minMult,
                multTo.maxMult,
                wrapper.ontoUmlPackage)
        else -> {
            association = RefOntoUMLFactoryUtil.createAssociation(source,
                    multFrom.minMult,
                    multFrom.maxMult,
                    vpAssociation.name,
                    target,
                    multTo.minMult,
                    multTo.maxMult,
                    wrapper.ontoUmlPackage)
        }
    }

    return association
}

fun createMeronymicAssociation(wrapper: RefOntoUMLWrapper, vpAssociation: IAssociation, type: OntoUMLRelationshipType?) : Association {
    var association: RefOntoUML.Meronymic

    val aggregationKind: String
    val whole: RefOntoUML.Classifier?
    val part: RefOntoUML.Classifier?
    val multWhole: AssociationMultiplicity
    val multPart: AssociationMultiplicity

    val assEndFrom = vpAssociation.fromEnd as IAssociationEnd
    val aggrTypeFrom = assEndFrom.aggregationKind
    val assEndTo = vpAssociation.toEnd as IAssociationEnd
    val aggrTypeTo = assEndTo.aggregationKind

    if (aggrTypeFrom == IAssociationEnd.AGGREGATION_KIND_COMPOSITED || aggrTypeFrom == IAssociationEnd.AGGREGATION_KIND_SHARED) {
        aggregationKind = aggrTypeFrom
        whole = wrapper.getOntoUMLClassifier(vpAssociation.from)
        multWhole = AssociationMultiplicity(assEndFrom.multiplicity)
        part = wrapper.getOntoUMLClassifier(vpAssociation.to)
        multPart = AssociationMultiplicity(assEndTo.multiplicity)
    } else {
        aggregationKind = aggrTypeTo
        whole = wrapper.getOntoUMLClassifier(vpAssociation.to)
        multWhole = AssociationMultiplicity(assEndTo.multiplicity)
        part = wrapper.getOntoUMLClassifier(vpAssociation.from)
        multPart = AssociationMultiplicity(assEndFrom.multiplicity)
    }

    when (type) {
        OntoUMLRelationshipType.COMPONENT_OF ->
            association = RefOntoUMLFactoryUtil.createComponentOf(whole,
                multWhole.minMult,
                multWhole.maxMult,
                vpAssociation.name,
                part,
                multPart.minMult,
                multPart.maxMult,
                wrapper.ontoUmlPackage)
        OntoUMLRelationshipType.MEMBER_OF ->
            association = RefOntoUMLFactoryUtil.createMemberOf(whole,
                multWhole.minMult,
                multWhole.maxMult,
                vpAssociation.name,
                part,
                multPart.minMult,
                multPart.maxMult,
                wrapper.ontoUmlPackage)
        OntoUMLRelationshipType.SUBQUANTITY_OF ->
            association = RefOntoUMLFactoryUtil.createSubQuantityOf(whole,
                multWhole.minMult,
                multWhole.maxMult,
                vpAssociation.name,
                part,
                multPart.minMult,
                multPart.maxMult,
                wrapper.ontoUmlPackage)
        OntoUMLRelationshipType.SUBCOLLECTION_OF ->
            association = RefOntoUMLFactoryUtil.createSubCollectionOf(whole,
                multWhole.minMult,
                multWhole.maxMult,
                vpAssociation.name,
                part,
                multPart.minMult,
                multPart.maxMult,
                wrapper.ontoUmlPackage)
        else ->
            association = RefOntoUMLFactoryUtil.createComponentOf(whole,
                multWhole.minMult,
                multWhole.maxMult,
                vpAssociation.name,
                part,
                multPart.minMult,
                multPart.maxMult,
                wrapper.ontoUmlPackage)
    }

    if(association is RefOntoUML.subQuantityOf || aggregationKind.equals(IAssociationEnd.AGGREGATION_KIND_COMPOSITED)){
        association.setIsShareable(false);
    }else{
        association.setIsShareable(true);
    }

    association = setTaggedValues(vpAssociation, association);

    return association
}

fun setTaggedValues(vpAssociation : IAssociation, ontoUmlAssociation : RefOntoUML.Meronymic) : RefOntoUML.Meronymic {
    val taggedValuesContainer = vpAssociation.taggedValues

    val essential = taggedValuesContainer.getTaggedValueByName("essential")
    val inseparable = taggedValuesContainer.getTaggedValueByName("inseparable")
    val immutablePart = taggedValuesContainer.getTaggedValueByName("immutablePart")
    val immutableWhole = taggedValuesContainer.getTaggedValueByName("immutableWhole")

    ontoUmlAssociation.isIsEssential = essential.valueAsString.toLowerCase().toBoolean()
    ontoUmlAssociation.isIsImmutablePart = ontoUmlAssociation.isIsEssential || immutablePart.valueAsString.toLowerCase().toBoolean()

    ontoUmlAssociation.isIsInseparable = inseparable.valueAsString.toLowerCase().toBoolean()
    ontoUmlAssociation.isIsImmutableWhole = ontoUmlAssociation.isIsInseparable || immutableWhole.valueAsString.toLowerCase().toBoolean()

    return ontoUmlAssociation
}