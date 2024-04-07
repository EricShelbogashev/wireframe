package core.model.algebra.properties

interface Normalizable<T : Number, AlgebraObject> {
    fun norm(): T
    fun normalize()
    fun toNormalized(): AlgebraObject
}