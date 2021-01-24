package io.deltawave.bsg.context

import io.deltawave.bsg.ast.BsgClass
import io.deltawave.bsg.ast.type.BsgType

data class AstMetadata(private val classes: List<BsgClass>) {
    private val classMetadata: Map<String, ClassMetadata>
    init {
        val classesByName = classes.associateBy { it.name }
        this.classMetadata = classesByName.mapValues { (name, cls) ->
            // Gotta find "declaring class" for all fields and methods.
            fun BsgClass.getSuperClasses(): List<BsgClass> {
                return this.directSuperClasses
                        .map { classesByName[it] ?: error("Could not find class $it") }
                        .flatMap { it.getSuperClasses() }
            }
            // Searching the super-class list in reverse is equivalent to
            // searching the tree starting with the leaves.
            val superClasses = cls.getSuperClasses()
            val superClassesAndSelf = listOf(cls) + superClasses
            val fieldVars = cls.body.fields.map { field ->
                val fieldOfClass = superClassesAndSelf.reversed().first { superClass ->
                    superClass.body.fields.any { f -> f.name == field.name }
                }
                val fieldOf = BsgType.Class(fieldOfClass.name)
                VarMetadata.Field(field.name, field.type, fieldOf = fieldOf)
            }
            val methodVars = cls.body.methods.map { method ->
                val methodOfClass = superClassesAndSelf.reversed().first { superClass ->
                    superClass.body.methods.any { m -> m.name == method.name }
                }
                val methodOf = BsgType.Class(methodOfClass.name)
                val methodType = BsgType.Method(method.arguments.map { (_, type) -> type }, method.returnType)
                VarMetadata.Method(method.name, methodType, methodOf = methodOf, method.arguments)
            }
            val superClassTypes = superClasses.map { superClass -> BsgType.Class(superClass.name) }
            ClassMetadata(
                    BsgType.Class(name),
                    fieldVars.associateBy { it.varName },
                    methodVars.associateBy { it.varName },
                    superClassTypes.toSet()
            )
        }
    }

    fun getClass(name: String): ClassMetadata = classMetadata[name] ?: error("Could not find class: $name")
}