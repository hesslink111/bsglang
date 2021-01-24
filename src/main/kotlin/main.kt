import io.deltawave.bsg.ast.BsgClass
import io.deltawave.bsg.ast.BsgClassBody
import io.deltawave.bsg.context.AstContext
import io.deltawave.bsg.context.AstMetadata
import io.deltawave.bsg.parser.BsgParser
import java.io.File

fun main(args: Array<String>) {
    println("Hello World!")
    val exampleClass = BsgParser.file.parse(File("example_class.bsg").readText())
    val puppyDogClass = BsgParser.file.parse(File("puppy_dog.bsg").readText())
    val ctx = AstContext(
            StringBuilder(),
            StringBuilder(),
            AstMetadata(listOf(exampleClass, puppyDogClass))
    )
    println("Ast Metadata: ${ctx.astMetadata}")
    exampleClass.toC(ctx)
    puppyDogClass.toC(ctx)

    println("Parsed ast: $puppyDogClass")

    println(".h file:")
    println(ctx.hFile.toString())

    println()
    println()
    println(".c file")
    println(ctx.cFile.toString())
}