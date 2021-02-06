#include "Animal.h"
struct BSG_AnyInstance* BSG_BaseMethod__Animal_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type) {
	struct BSG_BaseInstance__Animal* b = (struct BSG_BaseInstance__Animal*)base;
	switch(type) {
	case BSG_Type__Animal:
		return (struct BSG_AnyInstance*)&b->Animal;
	}
	return NULL;
}
void BSG_BaseMethod__Animal_retain(struct BSG_AnyBaseInstance* base) {
	base->refCount++;
}
void BSG_BaseMethod__Animal_release(struct BSG_AnyBaseInstance* base) {
	base->refCount--;
	if(base->refCount <= 0) {
		struct BSG_BaseInstance__Animal* b = (struct BSG_BaseInstance__Animal*)base;
		free(base);
	}
}
struct BSG_BaseClass BSG_BaseClassSingleton__Animal = {
	.cast = &BSG_BaseMethod__Animal_cast,
	.retain = &BSG_BaseMethod__Animal_retain,
	.release = &BSG_BaseMethod__Animal_release,
};
struct BSG_Instance__String* BSG_Method__Animal_Animal_talk(struct BSG_Instance__Animal* this) {
	struct BSG_Instance__String* __0 = BSG_Constructor__String();
	__0->baseInstance->baseClass->retain(__0->baseInstance);
	char* __1 = "";
	__0->cStr = (BSG_Opaque) malloc(1 * sizeof(char));
	strcpy((char*)__0->cStr, __1);
	if(this) {
		this->baseInstance->baseClass->release(this->baseInstance);
	}
	return __0;
}
struct BSG_Class__Animal BSG_ClassSingleton__Animal_Animal = {
	.talk = &BSG_Method__Animal_Animal_talk,
};
struct BSG_Instance__Animal* BSG_Constructor__Animal() {
	struct BSG_BaseInstance__Animal* baseInstance = malloc(sizeof(struct BSG_BaseInstance__Animal));
	baseInstance->Animal = (struct BSG_Instance__Animal) {
		.baseInstance = (struct BSG_AnyBaseInstance*)baseInstance,
		.class = &BSG_ClassSingleton__Animal_Animal,
	};
	baseInstance->baseClass = (struct BSG_AnyBaseClass*) &BSG_BaseClassSingleton__Animal;
	return &baseInstance->Animal;
}
