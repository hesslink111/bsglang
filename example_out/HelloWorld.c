#include "HelloWorld.h"
const BSG_AnyType BSG_Type__HelloWorld = 0l;
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
BSG_Void BSG_Method__HelloWorld_main(struct BSG_Instance__HelloWorld* this) {
	struct BSG_MethodFatPtr__IO_println __0;
	__0.this = IO;
	__0.method = IO->class->println;
	if(__0.this) {
		__0.this->baseInstance->baseClass->retain(__0.this->baseInstance);
	}
	struct BSG_Instance__String* __1 = BSG_Constructor__String();
	__1->baseInstance->baseClass->retain(__1->baseInstance);
	char* __2 = "Hello, world!";
	__1->cStr = (BSG_Opaque) malloc(14 * sizeof(char));
	strcpy((char*)__1->cStr, __2);
	__0.this->baseInstance->baseClass->retain(__0.this->baseInstance);
	if(__1) {
		__1->baseInstance->baseClass->retain(__1->baseInstance);
	}
	__0.method(__0.this,__1);
	if(this) {
		this->baseInstance->baseClass->release(this->baseInstance);
	}
	if(__0.this) {
		__0.this->baseInstance->baseClass->release(__0.this->baseInstance);
	}
	if(__1) {
		__1->baseInstance->baseClass->release(__1->baseInstance);
	}
	return;
}
struct BSG_Class__HelloWorld BSG_ClassSingleton__HelloWorld_HelloWorld = {
	.main = &BSG_Method__HelloWorld_main,
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
