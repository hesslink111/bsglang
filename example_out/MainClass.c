#include "MainClass.h"
const BSG_AnyType BSG_Type__MainClass = 0l;
struct BSG_AnyInstance* BSG_BaseMethod__MainClass_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type) {
	struct BSG_BaseInstance__MainClass* b = (struct BSG_BaseInstance__MainClass*)base;
	switch(type) {
	case BSG_Type__MainClass:
		return (struct BSG_AnyInstance*)&b->MainClass;
	}
	return NULL;
}
void BSG_BaseMethod__MainClass_retain(struct BSG_AnyBaseInstance* base) {
	base->refCount++;
}
void BSG_BaseMethod__MainClass_release(struct BSG_AnyBaseInstance* base) {
	base->refCount--;
	if(base->refCount <= 0) {
		struct BSG_BaseInstance__MainClass* b = (struct BSG_BaseInstance__MainClass*)base;
		free(base);
	}
}
struct BSG_BaseClass BSG_BaseClassSingleton__MainClass = {
	.cast = &BSG_BaseMethod__MainClass_cast,
	.retain = &BSG_BaseMethod__MainClass_retain,
	.release = &BSG_BaseMethod__MainClass_release,
};
BSG_Void BSG_Method__MainClass_main(struct BSG_Instance__MainClass* this) {
	struct BSG_MethodFatPtr__IO_println __0;
	__0.this = IO;
	__0.method = IO->class->println;
	__0.this->baseInstance->baseClass->retain(__0.this->baseInstance);
	struct BSG_Instance__String* __1 = BSG_Constructor__String();
	__1->baseInstance->baseClass->retain(__1->baseInstance);
	char* __2 = "Hello, world!";
	__1->cStr = (BSG_Opaque) malloc(14 * sizeof(char));
	strcpy((char*)__1->cStr, __2);
	__0.this->baseInstance->baseClass->retain(__0.this->baseInstance);
	__1->baseInstance->baseClass->retain(__1->baseInstance);
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
struct BSG_Class__MainClass BSG_ClassSingleton__MainClass_MainClass = {
	.main = &BSG_Method__MainClass_main,
};
struct BSG_Instance__MainClass* BSG_Constructor__MainClass() {
	struct BSG_BaseInstance__MainClass* baseInstance = malloc(sizeof(struct BSG_BaseInstance__MainClass));
	baseInstance->MainClass = (struct BSG_Instance__MainClass) {
		.baseInstance = (struct BSG_AnyBaseInstance*)baseInstance,
		.class = &BSG_ClassSingleton__MainClass_MainClass,
	};
	baseInstance->baseClass = (struct BSG_AnyBaseClass*) &BSG_BaseClassSingleton__MainClass;
	return &baseInstance->MainClass;
}
struct BSG_Instance__MainClass* MainClass = NULL;
