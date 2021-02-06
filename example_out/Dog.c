#include "Dog.h"
struct BSG_AnyInstance* BSG_BaseMethod__Dog_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type) {
	struct BSG_BaseInstance__Dog* b = (struct BSG_BaseInstance__Dog*)base;
	switch(type) {
	case BSG_Type__Dog:
		return (struct BSG_AnyInstance*)&b->Dog;
	case BSG_Type__Animal:
		return (struct BSG_AnyInstance*)&b->Animal;
	}
	return NULL;
}
void BSG_BaseMethod__Dog_retain(struct BSG_AnyBaseInstance* base) {
	base->refCount++;
}
void BSG_BaseMethod__Dog_release(struct BSG_AnyBaseInstance* base) {
	base->refCount--;
	if(base->refCount <= 0) {
		struct BSG_BaseInstance__Dog* b = (struct BSG_BaseInstance__Dog*)base;
		free(base);
	}
}
struct BSG_BaseClass BSG_BaseClassSingleton__Dog = {
	.cast = &BSG_BaseMethod__Dog_cast,
	.retain = &BSG_BaseMethod__Dog_retain,
	.release = &BSG_BaseMethod__Dog_release,
};
struct BSG_Instance__String* BSG_Method__Dog_Animal_talk(struct BSG_Instance__Animal* __0) {
	struct BSG_Instance__Dog* this = (struct BSG_Instance__Dog*)__0->baseInstance->baseClass->cast(__0->baseInstance, BSG_Type__Dog);
	struct BSG_Instance__String* __1 = BSG_Constructor__String();
	__1->baseInstance->baseClass->retain(__1->baseInstance);
	char* __2 = "Woof!";
	__1->cStr = (BSG_Opaque) malloc(6 * sizeof(char));
	strcpy((char*)__1->cStr, __2);
	if(this) {
		this->baseInstance->baseClass->release(this->baseInstance);
	}
	return __1;
}
struct BSG_Class__Dog BSG_ClassSingleton__Dog_Dog = {
};
struct BSG_Class__Animal BSG_ClassSingleton__Dog_Animal = {
	.talk = &BSG_Method__Dog_Animal_talk,
};
struct BSG_Instance__Dog* BSG_Constructor__Dog() {
	struct BSG_BaseInstance__Dog* baseInstance = malloc(sizeof(struct BSG_BaseInstance__Dog));
	baseInstance->Dog = (struct BSG_Instance__Dog) {
		.baseInstance = (struct BSG_AnyBaseInstance*)baseInstance,
		.class = &BSG_ClassSingleton__Dog_Dog,
	};
	baseInstance->Animal = (struct BSG_Instance__Animal) {
		.baseInstance = (struct BSG_AnyBaseInstance*)baseInstance,
		.class = &BSG_ClassSingleton__Dog_Animal,
	};
	baseInstance->baseClass = (struct BSG_AnyBaseClass*) &BSG_BaseClassSingleton__Dog;
	return &baseInstance->Dog;
}
