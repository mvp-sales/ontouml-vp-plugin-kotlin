package br.ufes.inf.ontoumlplugin.model

import com.vp.plugin.model.IAssociationEnd



class AssociationMultiplicity {

    val minMult : Int
    val maxMult : Int
    val strMult : String

    constructor(minMult : Int, maxMult : Int){
        this.minMult = minMult
        this.maxMult = maxMult
        this.strMult = buildMultiplicityString(minMult, maxMult)
    }

    constructor(multiplicity: String) {
        this.strMult = multiplicity

        when (multiplicity) {
            IAssociationEnd.MULTIPLICITY_ONE, IAssociationEnd.MULTIPLICITY_UNSPECIFIED -> {
                this.minMult = 1
                this.maxMult = 1
            }
            IAssociationEnd.MULTIPLICITY_ONE_TO_MANY -> {
                this.minMult = 1
                this.maxMult = -1
            }
            IAssociationEnd.MULTIPLICITY_ZERO_TO_MANY -> {
                this.minMult = 0
                this.maxMult = -1
            }
            IAssociationEnd.MULTIPLICITY_ZERO_TO_ONE -> {
                this.minMult = 0
                this.maxMult = 1
            }
            IAssociationEnd.MULTIPLICITY_MANY -> {
                this.minMult = -1
                this.maxMult = -1
            }
            else -> {
                val multStr = multiplicity.split("[.]+")
                this.minMult = if (multStr[0] == "*") -1 else multStr[0].toInt()
                this.maxMult = if (multStr.size == 2) (if (multStr[1] == "*") -1 else multStr[1].toInt())
                                else (if (multStr[0] == "*") -1 else multStr[0].toInt())
            }
        }
    }

    private fun buildMultiplicityString(min: Int, max: Int): String {
        if (min != max) {
            return min.toString() + ".." + if (max == -1) "*" else max
        }
        return if (min == -1) "*" else min.toString()
    }



}