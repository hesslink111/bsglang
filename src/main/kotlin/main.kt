import io.deltawave.bsg.ast.type.BsgType
import io.deltawave.bsg.context.AstContext
import io.deltawave.bsg.context.AstMetadata
import io.deltawave.bsg.context.GlobalScope
import io.deltawave.bsg.context.VarMetadata
import io.deltawave.bsg.parser.BsgParser
import io.deltawave.bsg.util.Uncrustify
import net.sourceforge.argparse4j.ArgumentParsers
import net.sourceforge.argparse4j.inf.ArgumentParserException
import java.io.File
import java.lang.Exception
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val parser = ArgumentParsers.newFor("Bsg Compiler").build()
            .defaultHelp(true)
            .description("Compile .bsg source files.")
    parser.addArgument("-i", "--input")
            .required(true)
            .nargs("*")
            .help("Input files")
    parser.addArgument("-iod", "--intermediary_out_dir")
            .help("Intermediary output directory. Specify if you want to see the generated .h/.c files.")
    parser.addArgument("-fio", "--format_intermediary_out")
            .type(Boolean::class.java)
            .setDefault(false)
            .help("Format intermediary files. Requires uncrustify.")
//    parser.addArgument("-o", "--output")
//            .help("Output executable. Requires gcc.")

    val namespace = try {
        parser.parseArgs(args)
    } catch(ex: ArgumentParserException) {
        parser.handleError(ex)
        exitProcess(1)
    }

    val fileNames = namespace.getList<String>("input")
    val tempOutputDir = createTempDir()
    val outputDir = namespace.getString("intermediary_out_dir")?.let { File(it) } ?: tempOutputDir
    if(!outputDir.exists()) {
        outputDir.mkdirs()
    }
    val preambleFile = outputDir.resolve("bsg_preamble.h")
    if(!preambleFile.exists()) {
        preambleFile.createNewFile()
    }
    File("bsg_preamble.h").copyTo(preambleFile, overwrite = true)
//    val outputExecutableFile: File? = namespace.getString("output")?.let { File(it) }

    // Parse.
    val parsedFiles = fileNames.map {
        try {
            BsgParser.file.parse(File(it).readText())
        } catch(ex: Exception) {
            println("Error while parsing $it")
            throw ex
        }
    }

    val globalScope = GlobalScope(parsedFiles.map { it.cls }.flatMap {
        if("Singleton" in it.attributes) {
            listOf(VarMetadata.LocalOrGlobal(it.name, BsgType.Class(it.name)))
        } else {
            emptyList()
        }
    })

    // Transpile to c.
    val astMeta = AstMetadata(parsedFiles.map { it.cls })
    val mainHFileBuilder = StringBuilder()
    val mainCFileInitBuilder = StringBuilder()
    val mainCFileMainBuilder = StringBuilder()
    parsedFiles.forEach { file ->
        val ctx = AstContext(
                hFile = StringBuilder(),
                cFile = StringBuilder(),
                mainHFile = mainHFileBuilder,
                mainCFileInit = mainCFileInitBuilder,
                mainCFileMain = mainCFileMainBuilder,
                astMetadata = astMeta
        )

        file.toC(ctx, globalScope)

        val outputHeaderFile = outputDir.resolve("${file.cls.name}.h")
        if(!outputHeaderFile.exists()) {
            outputHeaderFile.createNewFile()
        }
        val outputCFile = outputDir.resolve("${file.cls.name}.c")
        if(!outputCFile.exists()) {
            outputCFile.createNewFile()
        }
        outputHeaderFile.writeText(ctx.hFile.toString())
        outputCFile.writeText(ctx.cFile.toString())

        // Format c files.
        if(namespace.getBoolean("format_intermediary_out")) {
            Uncrustify.uncrustify(outputHeaderFile.absolutePath)
            Uncrustify.uncrustify(outputCFile.absolutePath)
        }
    }

    // Create main file.
    val mainHeaderFile = outputDir.resolve("main.h")
    if(!mainHeaderFile.exists()) {
        mainHeaderFile.createNewFile()
    }
    val mainCFile = outputDir.resolve("main.c")
    if(!mainCFile.exists()) {
        mainCFile.createNewFile()
    }
    mainHeaderFile.writeText(mainHFileBuilder.toString())
    mainCFile.writeText("#include \"main.h\"\n")
    mainCFile.appendText("int main() {\n")
    mainCFile.appendText(mainCFileInitBuilder.toString())
    mainCFile.appendText(mainCFileMainBuilder.toString())
    mainCFile.appendText("}\n")
    if(namespace.getBoolean("format_intermediary_out")) {
        Uncrustify.uncrustify(mainHeaderFile.absolutePath)
        Uncrustify.uncrustify(mainCFile.absolutePath)
    }

//    // Create executable.
//    if(outputExecutableFile != null) {
//        ProcessBuilder("gcc", outputCFile.absolutePath, "-o", outputExecutableFile.absolutePath)
//                .directory(outputDir)
//                .start()
//                .waitFor()
//    }

    // Clean up temp files.
    tempOutputDir.deleteRecursively()
}