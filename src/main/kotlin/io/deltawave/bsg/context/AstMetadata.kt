package io.deltawave.bsg.context

import io.deltawave.bsg.ast.BsgClass
import io.deltawave.bsg.ast.type.BsgType

data class AstMetadata(private val classes: List<BsgClass>) {
    private val classMetadata: Map<String, ClassMetadata>
    init {
        val classesByName = classes.associateBy { it.name }
        fun getClassByName(name: String): BsgClass {
            return classesByName[name] ?: error("Could not find class $name")
        }
        this.classMetadata = classesByName.mapValues { (name, cls) ->
            // Gotta find "declaring class" for all fields and methods.
            fun BsgClass.getSuperClasses(): List<BsgType.Class> {
                return this.directSuperClasses
                    .flatMap { listOf(it) + getClassByName(it.name).getSuperClasses() }
            }
            // Searching the super-class list in reverse is equivalent to
            // searching the tree starting with the leaves.
            val superClasses = cls.getSuperClasses()
            val superClassesAndSelf = listOf(BsgType.Class(cls.name, emptyMap())) + superClasses
            val fieldVars = cls.body.fields.map { field ->
                val fieldOf = superClassesAndSelf.reversed().first { superClass ->
                    getClassByName(superClass.name).body.fields.any { f -> f.name == field.name }
                }
                VarMetadata.Field(field.name, field.type, fieldOf = fieldOf)
            }
            val methodVars = cls.body.methods.map { method ->
                val methodOf = superClassesAndSelf.reversed().first { superClass ->
                    getClassByName(superClass.name).body.methods.any { m -> m.name == method.name }
                }
                val methodType = BsgType.Method(method.arguments.map { (_, type) -> type }, method.returnType)
                VarMetadata.Method(method.name, methodType, methodOf = methodOf, method.arguments, method.attributes)
            }
            ClassMetadata(
                    BsgType.Class(name, emptyMap()),
                    fieldVars.associateBy { it.varName },
                    methodVars.associateBy { it.varName },
                    superClasses.toSet(),
                    cls.attributes
            )
        }
    }

    fun getClass(name: String): ClassMetadata = classMetadata[name] ?: error("Could not find class: $name")
}