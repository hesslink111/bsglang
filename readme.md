### BSG Language & Compiler [WIP]

Example usage:
```
java -jar BsgCompiler.jar -i example_in/main_class.bsg example_in/string.bsg example_in/io.bsg example_in/string_extensions.bsg example_in/list.bsg -iod example_out -fio true
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