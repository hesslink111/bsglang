import io.deltawave.bsg.Compiler
import io.deltawave.bsg.util.Uncrustify
import net.sourceforge.argparse4j.ArgumentParsers
import net.sourceforge.argparse4j.impl.Arguments
import net.sourceforge.argparse4j.inf.ArgumentParserException
import java.io.File
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val parser = ArgumentParsers.newFor("Bsg Compiler").build()
            .defaultHelp(true)
            .description("Compile .bsg source files.")
    parser.addArgument("-i", "--input")
            .required(true)
            .help("Input file")
    parser.addArgument("-iod", "--intermediary_out_dir")
            .help("Intermediary output directory. Specify if you want to see the generated .h/.c files.")
    parser.addArgument("-fio", "--format_intermediary_out")
            .type(Boolean::class.java)
            .action(Arguments.storeTrue())
            .setDefault(false)
            .required(false)
            .help("Format intermediary files. Requires uncrustify.")
    parser.addArgument("-m", "--make")
            .type(Boolean::class.java)
            .action(Arguments.storeTrue())
            .setDefault(false)
            .required(false)
            .help("Make executable. Requires gcc.")

    val argNamespace = try {
        parser.parseArgs(args)
    } catch(ex: ArgumentParserException) {
        parser.handleError(ex)
        exitProcess(1)
    }

    val inputFilename = argNamespace.getString("input")
    val tempOutputDir = createTempDir()
    val outputDir = argNamespace.getString("intermediary_out_dir")?.let { File(it) } ?: tempOutputDir
    if(!outputDir.exists()) {
        outputDir.mkdirs()
    }
    val preambleHFile = outputDir.resolve("bsg_preamble.h")
    if(!preambleHFile.exists()) {
        preambleHFile.createNewFile()
    }
    File("bsg_preamble.h").copyTo(preambleHFile, overwrite = true)
    val preambleCFile = outputDir.resolve("bsg_preamble.c")
    if(!preambleCFile.exists()) {
        preambleCFile.createNewFile()
    }
    File("bsg_preamble.c").copyTo(preambleCFile, overwrite = true)

    // Compile.
    val compiledFiles = Compiler.compile(inputFilename)

    // Write files.
    compiledFiles.forEach { (name, contents) ->
        val outputFile = outputDir.resolve(name)
        if(!outputFile.exists()) {
            outputFile.createNewFile()
        }
        outputFile.writeText(contents)

        // Format.
        if((outputFile.name.endsWith(".c") || outputFile.name.endsWith(".h"))
                && argNamespace.getBoolean("format_intermediary_out")) {
            Uncrustify.uncrustify(outputFile.absolutePath)
        }
    }

    // Create executable.
    if(argNamespace.getBoolean("make")) {
        ProcessBuilder("make")
                .directory(outputDir)
                .inheritIO()
                .start()
                .waitFor()
    }

    // Clean up temp files.
    tempOutputDir.deleteRecursively()
}