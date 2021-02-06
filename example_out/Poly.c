#include "Poly.h"
struct BSG_AnyInstance* BSG_BaseMethod__Poly_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type) {
	struct BSG_BaseInstance__Poly* b = (struct BSG_BaseInstance__Poly*)base;
	switch(type) {
	case BSG_Type__Poly:
		return (struct BSG_AnyInstance*)&b->Poly;
	}
	return NULL;
}
void BSG_BaseMethod__Poly_retain(struct BSG_AnyBaseInstance* base) {
	base->refCount++;
}
void BSG_BaseMethod__Poly_release(struct BSG_AnyBaseInstance* base) {
	base->refCount--;
	if(base->refCount <= 0) {
		struct BSG_BaseInstance__Poly* b = (struct BSG_BaseInstance__Poly*)base;
		free(base);
	}
}
struct BSG_BaseClass BSG_BaseClassSingleton__Poly = {
	.cast = &BSG_BaseMethod__Poly_cast,
	.retain = &BSG_BaseMethod__Poly_retain,
	.release = &BSG_BaseMethod__Poly_release,
};
BSG_Void BSG_Method__Poly_Poly_main(struct BSG_Instance__Poly* this) {
	struct BSG_Instance__Dog* dog;
	struct BSG_Instance__Dog** __0;
	__0 = &dog;
	struct BSG_Instance__Dog* __1 = BSG_Constructor__Dog();
	__1->baseInstance->baseClass->retain(__1->baseInstance);
	*__0 = __1;
	struct BSG_Instance__Cat* cat;
	struct BSG_Instance__Cat** __2;
	__2 = &cat;
	struct BSG_Instance__Cat* __3 = BSG_Constructor__Cat();
	__3->baseInstance->baseClass->retain(__3->baseInstance);
	*__2 = __3;
	struct BSG_Instance__IO* __4;
	__4 = IO;
	if(__4) {
		__4->baseInstance->baseClass->retain(__4->baseInstance);
	}
	struct BSG_MethodFatPtr__IO_println __5;
	__5.this = __4;
	__5.method = __4->class->println;
	struct BSG_MethodFatPtr__Poly_getAnimalSound __6;
	__6.this = this;
	__6.method = this->class->getAnimalSound;
	struct BSG_Instance__Animal* __7 = (struct BSG_Instance__Animal*) dog->baseInstance->baseClass->cast(dog->baseInstance, BSG_Type__Animal);
	__6.this->baseInstance->baseClass->retain(__6.this->baseInstance);
	if(__7) {
		__7->baseInstance->baseClass->retain(__7->baseInstance);
	}
	struct BSG_Instance__String* __8 = __6.method(__6.this,__7);
	__5.this->baseInstance->baseClass->retain(__5.this->baseInstance);
	if(__8) {
		__8->baseInstance->baseClass->retain(__8->baseInstance);
	}
	__5.method(__5.this,__8);
	struct BSG_Instance__IO* __10;
	__10 = IO;
	if(__10) {
		__10->baseInstance->baseClass->retain(__10->baseInstance);
	}
	struct BSG_MethodFatPtr__IO_println __11;
	__11.this = __10;
	__11.method = __10->class->println;
	struct BSG_MethodFatPtr__Poly_getAnimalSound __12;
	__12.this = this;
	__12.method = this->class->getAnimalSound;
	struct BSG_Instance__Animal* __13 = (struct BSG_Instance__Animal*) cat->baseInstance->baseClass->cast(cat->baseInstance, BSG_Type__Animal);
	__12.this->baseInstance->baseClass->retain(__12.this->baseInstance);
	if(__13) {
		__13->baseInstance->baseClass->retain(__13->baseInstance);
	}
	struct BSG_Instance__String* __14 = __12.method(__12.this,__13);
	__11.this->baseInstance->baseClass->retain(__11.this->baseInstance);
	if(__14) {
		__14->baseInstance->baseClass->retain(__14->baseInstance);
	}
	__11.method(__11.this,__14);
	if(this) {
		this->baseInstance->baseClass->release(this->baseInstance);
	}
	if(__1) {
		__1->baseInstance->baseClass->release(__1->baseInstance);
	}
	if(__3) {
		__3->baseInstance->baseClass->release(__3->baseInstance);
	}
	if(__4) {
		__4->baseInstance->baseClass->release(__4->baseInstance);
	}
	if(__8) {
		__8->baseInstance->baseClass->release(__8->baseInstance);
	}
	if(__10) {
		__10->baseInstance->baseClass->release(__10->baseInstance);
	}
	if(__14) {
		__14->baseInstance->baseClass->release(__14->baseInstance);
	}
	return;
}
struct BSG_Instance__String* BSG_Method__Poly_Poly_getAnimalSound(struct BSG_Instance__Poly* this,struct BSG_Instance__Animal* animal) {
	struct BSG_MethodFatPtr__Animal_talk __16;
	__16.this = animal;
	__16.method = animal->class->talk;
	__16.this->baseInstance->baseClass->retain(__16.this->baseInstance);
	struct BSG_Instance__String* __17 = __16.method(__16.this);
	if(this) {
		this->baseInstance->baseClass->release(this->baseInstance);
	}
	if(animal) {
		animal->baseInstance->baseClass->release(animal->baseInstance);
	}
	return __17;
}
struct BSG_Class__Poly BSG_ClassSingleton__Poly_Poly = {
	.main = &BSG_Method__Poly_Poly_main,
	.getAnimalSound = &BSG_Method__Poly_Poly_getAnimalSound,
};
struct BSG_Instance__Poly* BSG_Constructor__Poly() {
	struct BSG_BaseInstance__Poly* baseInstance = malloc(sizeof(struct BSG_BaseInstance__Poly));
	baseInstance->Poly = (struct BSG_Instance__Poly) {
		.baseInstance = (struct BSG_AnyBaseInstance*)baseInstance,
		.class = &BSG_ClassSingleton__Poly_Poly,
	};
	baseInstance->baseClass = (struct BSG_AnyBaseClass*) &BSG_BaseClassSingleton__Poly;
	return &baseInstance->Poly;
}
struct BSG_Instance__Poly* Poly = NULL;
