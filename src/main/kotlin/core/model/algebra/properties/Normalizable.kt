package model.algebra.properties

interface Normalizable<T, AlgebraObject> {
    fun norm(): T
    fun normalize()
    fun toNormalized(): AlgebraObject
}