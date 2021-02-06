#include "HelloWorld.h"
struct BSG_AnyInstance* BSG_BaseMethod__HelloWorld_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type) {
	struct BSG_BaseInstance__HelloWorld* b = (struct BSG_BaseInstance__HelloWorld*)base;
	switch(type) {
	case BSG_Type__HelloWorld:
		return (struct BSG_AnyInstance*)&b->HelloWorld;
	}
	return NULL;
}
void BSG_BaseMethod__HelloWorld_retain(struct BSG_AnyBaseInstance* base) {
	base->refCount++;
}
void BSG_BaseMethod__HelloWorld_release(struct BSG_AnyBaseInstance* base) {
	base->refCount--;
	if(base->refCount <= 0) {
		struct BSG_BaseInstance__HelloWorld* b = (struct BSG_BaseInstance__HelloWorld*)base;
		free(base);
	}
}
struct BSG_BaseClass BSG_BaseClassSingleton__HelloWorld = {
	.cast = &BSG_BaseMethod__HelloWorld_cast,
	.retain = &BSG_BaseMethod__HelloWorld_retain,
	.release = &BSG_BaseMethod__HelloWorld_release,
};
BSG_Void BSG_Method__HelloWorld_HelloWorld_main(struct BSG_Instance__HelloWorld* this) {
	struct BSG_Instance__IO* __0;
	__0 = IO;
	if(__0) {
		__0->baseInstance->baseClass->retain(__0->baseInstance);
	}
	struct BSG_MethodFatPtr__IO_println __1;
	__1.this = __0;
	__1.method = __0->class->println;
	struct BSG_Instance__String* __2 = BSG_Constructor__String();
	__2->baseInstance->baseClass->retain(__2->baseInstance);
	char* __3 = "Hello, world!";
	__2->cStr = (BSG_Opaque) malloc(14 * sizeof(char));
	strcpy((char*)__2->cStr, __3);
	__1.this->baseInstance->baseClass->retain(__1.this->baseInstance);
	if(__2) {
		__2->baseInstance->baseClass->retain(__2->baseInstance);
	}
	__1.method(__1.this,__2);
	if(this) {
		this->baseInstance->baseClass->release(this->baseInstance);
	}
	if(__0) {
		__0->baseInstance->baseClass->release(__0->baseInstance);
	}
	if(__2) {
		__2->baseInstance->baseClass->release(__2->baseInstance);
	}
	return;
}
struct BSG_Class__HelloWorld BSG_ClassSingleton__HelloWorld_HelloWorld = {
	.main = &BSG_Method__HelloWorld_HelloWorld_main,
};
struct BSG_Instance__HelloWorld* BSG_Constructor__HelloWorld() {
	struct BSG_BaseInstance__HelloWorld* baseInstance = malloc(sizeof(struct BSG_BaseInstance__HelloWorld));
	baseInstance->HelloWorld = (struct BSG_Instance__HelloWorld) {
		.baseInstance = (struct BSG_AnyBaseInstance*)baseInstance,
		.class = &BSG_ClassSingleton__HelloWorld_HelloWorld,
	};
	baseInstance->baseClass = (struct BSG_AnyBaseClass*) &BSG_BaseClassSingleton__HelloWorld;
	return &baseInstance->HelloWorld;
}
struct BSG_Instance__HelloWorld* HelloWorld = NULL;
