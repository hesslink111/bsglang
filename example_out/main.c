#include "main.h"
int main() {
	MainClass = BSG_Constructor__MainClass();
	MainClass->baseInstance->baseClass->retain(MainClass->baseInstance);
	IO = BSG_Constructor__IO();
	IO->baseInstance->baseClass->retain(IO->baseInstance);
	struct BSG_Instance__MainClass* mainInstance = BSG_Constructor__MainClass();
	mainInstance->baseInstance->baseClass->retain(mainInstance->baseInstance);
	mainInstance->class->main(mainInstance);
}
