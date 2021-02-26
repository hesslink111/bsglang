// Includes
#include "main.h"

// Main
int main() {
    HelloWorld = BSG_Constructor__HelloWorld();
    HelloWorld->baseInstance->baseClass->retain(HelloWorld->baseInstance);
    IO = BSG_Constructor__IO();
    IO->baseInstance->baseClass->retain(IO->baseInstance);
    HelloWorld->baseInstance->baseClass->retain(HelloWorld->baseInstance);
    HelloWorld->class->main((BSG_AnyInstance*)HelloWorld);
    HelloWorld->baseInstance->baseClass->release(HelloWorld->baseInstance);
    IO->baseInstance->baseClass->release(IO->baseInstance);
}
