#include "HelloWorld.h"
struct BSG_AnyInstance* BSG_BaseMethod__HelloWorld_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type) {
	struct BSG_BaseInstance__HelloWorld* b = (struct BSG_BaseInstance__HelloWorld*)base;
	switch(type) {
	case BSG_Type__HelloWorld:
		return (struct BSG_AnyInstance*)&b->HelloWorld;
	}
	return NULL;
}
BSG_Bool BSG_BaseMethod__HelloWorld_canCast(struct BSG_AnyBaseInstance* base, BSG_AnyType type) {
	struct BSG_BaseInstance__HelloWorld* b = (struct BSG_BaseInstance__HelloWorld*)base;
	switch(type) {
	case BSG_Type__HelloWorld:
		return true;
	}
	return false;
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
	.canCast = &BSG_BaseMethod__HelloWorld_canCast,
	.retain = &BSG_BaseMethod__HelloWorld_retain,
	.release = &BSG_BaseMethod__HelloWorld_release,
};
BSG_Void BSG_Method__HelloWorld_HelloWorld_main(struct BSG_Instance__HelloWorld* this) {
	struct BSG_Instance__IO* _tmp_0;
	_tmp_0 = IO;
	if(_tmp_0) {
		_tmp_0->baseInstance->baseClass->retain(_tmp_0->baseInstance);
	}
	struct BSG_MethodFatPtr__IO_println _tmp_1;
	_tmp_1.this = _tmp_0;
	_tmp_1.method = _tmp_0->class->println;
	struct BSG_Instance__String* _tmp_2 = BSG_Constructor__String();
	_tmp_2->baseInstance->baseClass->retain(_tmp_2->baseInstance);
	_tmp_2->cStr = "Hello, world!";
	_tmp_2->length = 13;
	_tmp_2->isLiteral = true;
	_tmp_1.this->baseInstance->baseClass->retain(_tmp_1.this->baseInstance);
	if(_tmp_2) {
		_tmp_2->baseInstance->baseClass->retain(_tmp_2->baseInstance);
	}
	_tmp_1.method(_tmp_1.this,_tmp_2);
	if(this) {
		this->baseInstance->baseClass->release(this->baseInstance);
	}
	if(_tmp_0) {
		_tmp_0->baseInstance->baseClass->release(_tmp_0->baseInstance);
	}
	if(_tmp_2) {
		_tmp_2->baseInstance->baseClass->release(_tmp_2->baseInstance);
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
