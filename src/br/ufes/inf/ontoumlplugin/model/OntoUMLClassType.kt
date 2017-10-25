package br.ufes.inf.ontoumlplugin.model

import com.vp.plugin.model.IStereotype
import com.vp.plugin.model.factory.IModelElementFactory
import com.vp.plugin.model.IProject



enum class OntoUMLClassType(val text: String) {
    KIND("Kind"), COLLECTIVE("Collective"), QUANTITY("Quantity"),
    SUBKIND("SubKind"), ROLE("Role"), PHASE("Phase"),
    ROLEMIXIN("RoleMixin"), CATEGORY("Category"), MIXIN("Mixin"),
    RELATOR("Relator"), MODE("Mode"), QUALITY("Quality"),

    DATA_TYPE("DataType"), PERCEIVABLE_QUALITY("PerceivableQuality"),
    NON_PERCEIVABLE_QUALITY("NonPerceivableQuality"), NOMINAL_QUALITY("NominalQuality"),
    /*MEASUREMENT_DOMAIN("MeasurementDomain"), ENUMERATION("Enumeration"),
    STRING_NOMINAL_STRUCTURE("StringNominalStructure"), DECIMAL_INTERVAL_DIMENSION("DecimalIntervalDimension"),
    DECIMAL_ORDINAL_DIMENSION("DecimalOrdinalDimension"), DECIMAL_RATIONAL_DIMENSION("DecimalRationalDimension"),
    INTEGER_INTERVAL_DIMENSION("IntegerIntervalDimension"), INTEGER_ORDINAL_DIMENSION("IntegerOrdinalDimension"),
    INTEGER_RATIONAL_DIMENSION("IntegerRationalDimension"),*/ PRIMITIVE_TYPE("PrimitiveType");
    companion object {
        fun fromString(text: String): OntoUMLClassType? {
            for (b in OntoUMLClassType.values()) {
                if (b.text.equals(text, true)) {
                    return b
                }
            }
            return null
        }

        fun getStereotypeFromString(project: IProject, text: String): IStereotype? {
            val stereotypes = project.toModelElementArray(IModelElementFactory.MODEL_TYPE_STEREOTYPE)
            stereotypes.forEach {
                val stereotype = it as IStereotype
                if (stereotype.baseType == IModelElementFactory.MODEL_TYPE_CLASS &&
                        stereotype.name.equals(text, true)) {
                    return stereotype
                }
            }
            return null
        }
    }

}