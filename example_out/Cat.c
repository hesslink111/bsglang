#include "Cat.h"
struct BSG_AnyInstance* BSG_BaseMethod__Cat_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type) {
	struct BSG_BaseInstance__Cat* b = (struct BSG_BaseInstance__Cat*)base;
	switch(type) {
	case BSG_Type__Cat:
		return (struct BSG_AnyInstance*)&b->Cat;
	case BSG_Type__Animal:
		return (struct BSG_AnyInstance*)&b->Animal;
	}
	return NULL;
}
void BSG_BaseMethod__Cat_retain(struct BSG_AnyBaseInstance* base) {
	base->refCount++;
}
void BSG_BaseMethod__Cat_release(struct BSG_AnyBaseInstance* base) {
	base->refCount--;
	if(base->refCount <= 0) {
		struct BSG_BaseInstance__Cat* b = (struct BSG_BaseInstance__Cat*)base;
		free(base);
	}
}
struct BSG_BaseClass BSG_BaseClassSingleton__Cat = {
	.cast = &BSG_BaseMethod__Cat_cast,
	.retain = &BSG_BaseMethod__Cat_retain,
	.release = &BSG_BaseMethod__Cat_release,
};
struct BSG_Instance__String* BSG_Method__Cat_Animal_talk(struct BSG_Instance__Animal* __0) {
	struct BSG_Instance__Cat* this = (struct BSG_Instance__Cat*)__0->baseInstance->baseClass->cast(__0->baseInstance, BSG_Type__Cat);
	struct BSG_Instance__String* __1 = BSG_Constructor__String();
	__1->baseInstance->baseClass->retain(__1->baseInstance);
	char* __2 = "Meow!";
	__1->cStr = (BSG_Opaque) malloc(6 * sizeof(char));
	strcpy((char*)__1->cStr, __2);
	if(this) {
		this->baseInstance->baseClass->release(this->baseInstance);
	}
	return __1;
}
struct BSG_Class__Cat BSG_ClassSingleton__Cat_Cat = {
};
struct BSG_Class__Animal BSG_ClassSingleton__Cat_Animal = {
	.talk = &BSG_Method__Cat_Animal_talk,
};
struct BSG_Instance__Cat* BSG_Constructor__Cat() {
	struct BSG_BaseInstance__Cat* baseInstance = malloc(sizeof(struct BSG_BaseInstance__Cat));
	baseInstance->Cat = (struct BSG_Instance__Cat) {
		.baseInstance = (struct BSG_AnyBaseInstance*)baseInstance,
		.class = &BSG_ClassSingleton__Cat_Cat,
	};
	baseInstance->Animal = (struct BSG_Instance__Animal) {
		.baseInstance = (struct BSG_AnyBaseInstance*)baseInstance,
		.class = &BSG_ClassSingleton__Cat_Animal,
	};
	baseInstance->baseClass = (struct BSG_AnyBaseClass*) &BSG_BaseClassSingleton__Cat;
	return &baseInstance->Cat;
}
