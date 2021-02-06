package io.deltawave.bsg

import io.deltawave.bsg.ast.type.BsgType
import io.deltawave.bsg.context.*
import io.deltawave.bsg.parser.BsgParser
import io.deltawave.bsg.util.appendLineNotBlank

object Compiler {
    fun compile(inputFiles: Map<String, String>): Map<String, String> {
        // Parse each file.
        val parsedFiles = inputFiles.map { (fileName, fileContents) ->
            try {
                BsgParser.file.parse(fileContents)
            } catch(ex: Exception) {
                println("Error while parsing $fileName")
                throw ex
            }
        }

        // Create scope.
        val globalScope = GlobalScope(parsedFiles.map { it.cls }.flatMap {
            if("Singleton" in it.attributes) {
                listOf(VarMetadata.LocalOrGlobal(it.name, BsgType.Class(it.name), isGlobal=true))
            } else {
                emptyList()
            }
        })

        // Generate map of output files.
        val outputFiles = mutableMapOf<String, String>()

        // Compile each file.
        val globalContext = GlobalContext()
        val astMeta = AstMetadata(parsedFiles.map { it.cls })
        val mainHFileBuilder = StringBuilder()
        val mainCFileInitBuilder = StringBuilder()
        val mainCFileMainBuilder = StringBuilder()
        val mainCFileDeinitBuilder = StringBuilder()
        parsedFiles.forEach { bsgFile ->
            val ctx = ClassContext(
                    globalContext = globalContext,
                    hFile = StringBuilder(),
                    cFile = StringBuilder(),
                    mainHFile = mainHFileBuilder,
                    mainCFileInit = mainCFileInitBuilder,
                    mainCFileMain = mainCFileMainBuilder,
                    mainCFileDeinit = mainCFileDeinitBuilder,
                    astMetadata = astMeta
            )

            bsgFile.toC(ctx, globalScope)

            outputFiles["${bsgFile.cls.name}.h"] = ctx.hFile.toString()
            outputFiles["${bsgFile.cls.name}.c"] = ctx.cFile.toString()
        }

        // main files.
        outputFiles["main.h"] = mainHFileBuilder.toString()
        outputFiles["main.c"] = StringBuilder()
                .appendLine("#include \"main.h\"")
                .appendLine("int main() {")
                .appendLineNotBlank(mainCFileInitBuilder)
                .appendLineNotBlank(mainCFileMainBuilder)
                .appendLineNotBlank(mainCFileDeinitBuilder)
                .appendLine("}")
                .toString()

        return outputFiles
    }
}