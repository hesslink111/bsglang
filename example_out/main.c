#include "main.h"
int main() {
	MainClass = BSG_Constructor__MainClass();
	MainClass->baseInstance->baseClass->retain(MainClass->baseInstance);
	IO = BSG_Constructor__IO();
	IO->baseInstance->baseClass->retain(IO->baseInstance);
	MainClass->baseInstance->baseClass->retain(MainClass->baseInstance);
	MainClass->class->main(MainClass);
}
