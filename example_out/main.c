#include "main.h"
int main() {
	MainClass = BSG_Constructor__MainClass();
	MainClass->baseInstance->baseClass->retain(MainClass->baseInstance);
	IO = BSG_Constructor__IO();
	IO->baseInstance->baseClass->retain(IO->baseInstance);
	StringExtensions = BSG_Constructor__StringExtensions();
	StringExtensions->baseInstance->baseClass->retain(StringExtensions->baseInstance);
	MainClass->baseInstance->baseClass->retain(MainClass->baseInstance);
	MainClass->class->main(MainClass);
	MainClass->baseInstance->baseClass->release(MainClass->baseInstance);
	IO->baseInstance->baseClass->release(IO->baseInstance);
	StringExtensions->baseInstance->baseClass->release(StringExtensions->baseInstance);
}
