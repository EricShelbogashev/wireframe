package model.engine

// Общий контракт трассируемых объектов, методы должны предоставлять возможности для трассировки
// объект также должен обладать свойствами
interface Traceable {
    val properties: Properties
    fun intersect() {
        // TODO:
    }
}