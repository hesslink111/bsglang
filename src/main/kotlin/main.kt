import io.deltawave.bsg.context.AstContext
import io.deltawave.bsg.context.AstMetadata
import io.deltawave.bsg.parser.BsgParser
import net.sourceforge.argparse4j.ArgumentParsers
import net.sourceforge.argparse4j.inf.ArgumentParserException
import java.io.File
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
    val outputExecutableFile: File? = namespace.getString("output")?.let { File(it) }

    // Parse.
    val parsedClasses = fileNames.map { BsgParser.file.parse(File(it).readText()) }

    // Transpile to c.
    parsedClasses.forEach { cls ->
        val ctx = AstContext(
                hFile = StringBuilder(),
                cFile = StringBuilder(),
                astMetadata = AstMetadata(parsedClasses)
        )

        cls.toC(ctx)

        val outputHeaderFile = outputDir.resolve("${cls.name}.h")
        if(!outputHeaderFile.exists()) {
            outputHeaderFile.createNewFile()
        }
        val outputCFile = outputDir.resolve("${cls.name}.c")
        if(!outputCFile.exists()) {
            outputCFile.createNewFile()
        }
        outputHeaderFile.writeText(ctx.hFile.toString())
        outputCFile.writeText(ctx.cFile.toString())

        // Format c files.
        if(namespace.getBoolean("format_intermediary_out")) {
            ProcessBuilder("uncrustify", "-c", "-", "-f", outputHeaderFile.absolutePath, "-o", outputHeaderFile.absolutePath, "--no-backup")
                    .start()
                    .waitFor()
            ProcessBuilder("uncrustify", "-c", "-", "-f", outputCFile.absolutePath, "-o", outputCFile.absolutePath, "--no-backup")
                    .start()
                    .waitFor()
        }
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