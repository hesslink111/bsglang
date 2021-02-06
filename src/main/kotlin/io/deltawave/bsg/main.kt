import io.deltawave.bsg.Compiler
import io.deltawave.bsg.util.Uncrustify
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
//    val outputExecutableFile: File? = namespace.getString("output")?.let { File(it) }

    // Compile.
    val compiledFiles = Compiler.compile(fileNames.associateWith { name -> File(name).readText() })

    // Write files.
    compiledFiles.forEach { (name, contents) ->
        val outputFile = outputDir.resolve(name)
        if(!outputFile.exists()) {
            outputFile.createNewFile()
        }
        outputFile.writeText(contents)

        // Format.
        if(namespace.getBoolean("format_intermediary_out")) {
            Uncrustify.uncrustify(outputFile.absolutePath)
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