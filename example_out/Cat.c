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
struct BSG_Class__Cat BSG_ClassSingleton__Cat_Cat = {
};
struct BSG_Class__Animal BSG_ClassSingleton__Cat_Animal = {
	.talk = &BSG_Method__Animal_Animal_talk,
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
