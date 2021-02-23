package io.deltawave.bsg.context

import org.ainslec.picocog.PicoWriter

class ClassContext(
    val globalContext: GlobalContext,
    val mainHIncludes: PicoWriter,
    val mainCInit: PicoWriter,
    val mainCBody: PicoWriter,
    val mainCDeinit: PicoWriter,
    val astMetadata: AstMetadata
) {
    private var nextVarNum: Int = 0
    private var nextLifetime: Int = 0

    val h = PicoWriter("    ")
    val hIfNDef = h.createDeferredWriter()
    val hIncludes = h.createDeferredWriter().writeln().writeln("// Includes")
    val hTypeNum = h.createDeferredWriter().writeln().writeln("// Type Num")
    val hInstance = h.createDeferredWriter().writeln().writeln("// Instance")
    val hBaseInstance = h.createDeferredWriter().writeln().writeln("// Base Instance")
    val hMethodTypedefs = h.createDeferredWriter().writeln().writeln("// Method Typedefs")
    val hClass = h.createDeferredWriter().writeln().writeln("// Class")
    val hBaseMethods = h.createDeferredWriter().writeln().writeln("// Base Methods")
    val hBaseClassSingleton = h.createDeferredWriter().writeln().writeln("// Base Class Singleton")
    val hMethods = h.createDeferredWriter().writeln().writeln("// Methods")
    val hClassSingletons = h.createDeferredWriter().writeln().writeln("// Class Singletons")
    val hConstructor = h.createDeferredWriter().writeln().writeln("// Constructor")
    val hSingleton = h.createDeferredWriter().writeln().writeln("// Singleton")
    val hEndIf = h.createDeferredWriter().writeln()

    val c = PicoWriter("    ")
    val cIncludes = c.createDeferredWriter().writeln("// Includes")
    val cBaseMethods = c.createDeferredWriter().writeln().writeln("// Base Methods")
    val cBaseClassSingleton = c.createDeferredWriter().writeln().writeln("// Base Class Singleton")
    val cMethods = c.createDeferredWriter().writeln().writeln("// Methods")
    val cClassSingletons = c.createDeferredWriter().writeln().writeln("// Class Singletons")
    val cConstructor = c.createDeferredWriter().writeln().writeln("// Constructor")
    val cSingleton = c.createDeferredWriter().writeln().writeln("// Singleton")

    fun getNextTypeNum() = globalContext.getNextTypeNum()

    fun getUniqueVarName(): String {
        return "_tmp_${nextVarNum++}"
    }

    fun getUniqueLifetime(): Lifetime {
        return Lifetime(nextLifetime++)
    }
}