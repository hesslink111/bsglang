package io.deltawave.bsg

import io.deltawave.bsg.ast.BsgFile
import io.deltawave.bsg.ast.BsgHeaderStatement
import io.deltawave.bsg.ast.type.BsgType
import io.deltawave.bsg.context.*
import io.deltawave.bsg.parser.BsgParser
import io.deltawave.bsg.util.appendLineNotBlank
import org.ainslec.picocog.PicoWriter
import java.io.File

object Compiler {
    fun compile(inputFilename: String): Map<String, String> {
        val inputFile = File(inputFilename)
        return compile(listOf(inputFile.name)) { f -> inputFile.resolveSibling(f).readText() }
    }

    fun compile(inputFilenames: List<String>, inputFiles: Map<String, String>): Map<String, String> {
        return compile(inputFilenames) { f -> inputFiles[f] ?: error("File not found: $f") }
    }

    fun compile(inputFilenames: List<String>, getInputFile: (String) -> String): Map<String, String> {
        // Parse each file.
        val filesCompleted = mutableMapOf<String, BsgFile>()
        val filesRemaining = inputFilenames.toMutableSet()
        while(filesRemaining.isNotEmpty()) {
            val filename = filesRemaining.first()
            filesRemaining.remove(filename)

            val fileContents = getInputFile(filename)
            val parsedFile = try {
                BsgParser.file.parse(fileContents)
            } catch(ex: Exception) {
                println("Error while parsing $filename")
                throw ex
            }

            filesCompleted[filename] = parsedFile

            // Get other files.
            parsedFile.headerStatements
                    .filterIsInstance<BsgHeaderStatement.Import>()
                    .map { import -> "${import.name}.bsg" }
                    .filterTo(filesRemaining) { it !in filesCompleted }
        }

        // Create scope.
        val globalScope = GlobalScope(filesCompleted.values.map { it.cls }.flatMap {
            if("Singleton" in it.attributes) {
                listOf(VarMetadata.LocalOrGlobal(it.name, BsgType.Class(it.name, emptyMap()), isGlobal=true))
            } else {
                emptyList()
            }
        })

        // Generate map of output files.
        val outputFiles = mutableMapOf<String, String>()

        // Compile each file.
        val globalContext = GlobalContext()
        val classes = filesCompleted.values.map { it.cls }
        val astMeta = AstMetadata(classes)
        val mainH = PicoWriter("    ")
        val mainHIncludes = mainH.createDeferredWriter().writeln("// Includes")
        val mainC = PicoWriter("    ")
            .writeln("// Includes")
            .writeln("#include \"main.h\"")
        val mainCMain = mainC.createDeferredWriter().writeln().writeln("// Main")
        mainCMain.writeln_r("int main() {")
        val mainCInit = mainCMain.createDeferredWriter()
        val mainCBody = mainCMain.createDeferredWriter()
        val mainCDeinit = mainCMain.createDeferredWriter()
        mainCMain.writeln_l("}")


        filesCompleted.values.forEach { bsgFile ->
            val ctx = ClassContext(
                    globalContext = globalContext,
                    mainHIncludes = mainHIncludes,
                    mainCInit = mainCInit,
                    mainCBody = mainCBody,
                    mainCDeinit = mainCDeinit,
                    astMetadata = astMeta
            )

            bsgFile.toC(ctx, globalScope)

            outputFiles["${bsgFile.cls.name}.h"] = ctx.h.toString()
            outputFiles["${bsgFile.cls.name}.c"] = ctx.c.toString()
        }

        // main files.
        outputFiles["main.h"] = mainH.toString()
        outputFiles["main.c"] = mainC.toString()

        outputFiles["Makefile"] = StringBuilder()
                .appendLine("all: main")
                .appendLine()
                .appendLine("main: bsg_preamble.c main.c ${classes.map { it.name + ".c" }.joinToString(" ")}")
                .appendLine("\tgcc bsg_preamble.c main.c ${classes.map { it.name + ".c" }.joinToString(" ")} -o main")
                .appendLine()
                .appendLine("clean:")
                .appendLine("\trm -rf main")
                .toString()

        return outputFiles
    }
}