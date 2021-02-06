### BSG Language & Compiler [WIP]

Example usage:
```
java -jar BsgCompiler.jar -i example_in/HelloWorld.bsg example_in/String.bsg example_in/IO.bsg -iod example_out -fio true
```

Example output - see `example_out` folder.

Language sample:
```
import IO;

[Singleton]
class HelloWorld {
    [Main]
    main(): Void {
        IO.println("Hello, world!");
    }
}
```

Output:
```
Hello, world!
```
