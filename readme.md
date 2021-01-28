### BSG Language & Compiler

Example usage:
```
java -jar BsgCompiler.jar -i main_class.bsg string.bsg io.bsg -iod /Users/will/Desktop/bsg_output -fio true
```

Example output - see `example_out` folder.

Language sample:
```
import IO;

[Singleton]
class MainClass {
    [Main]
    main(): Void {
        IO.println("Hello, world!");
    }
}
```