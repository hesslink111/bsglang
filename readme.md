### BSG Language & Compiler [WIP]

Example usage:
```
java -jar BsgCompiler.jar -i example_in/HelloWorld.bsg -iod example_out -fio -m
```

Intermediary output - see `example_out` folder.

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
$ ./example_out/main
Hello, world!
```
