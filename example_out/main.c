#include "main.h"
int main() {
	Poly = BSG_Constructor__Poly();
	Poly->baseInstance->baseClass->retain(Poly->baseInstance);
	IO = BSG_Constructor__IO();
	IO->baseInstance->baseClass->retain(IO->baseInstance);
	Poly->baseInstance->baseClass->retain(Poly->baseInstance);
	Poly->class->main(Poly);
	Poly->baseInstance->baseClass->release(Poly->baseInstance);
	IO->baseInstance->baseClass->release(IO->baseInstance);
}
