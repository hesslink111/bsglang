#include "String.h"
const BSG_AnyType BSG_Type__String = 1l;
struct BSG_AnyInstance* BSG_BaseMethod__String_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type) {
	struct BSG_BaseInstance__String* b = (struct BSG_BaseInstance__String*)base;
	switch(type) {
	case BSG_Type__String:
		return (struct BSG_AnyInstance*)&b->String;
	}
	return NULL;
}
void BSG_BaseMethod__String_retain(struct BSG_AnyBaseInstance* base) {
	base->refCount++;
}
void BSG_BaseMethod__String_release(struct BSG_AnyBaseInstance* base) {
	base->refCount--;
	if(base->refCount <= 0) {
		struct BSG_BaseInstance__String* b = (struct BSG_BaseInstance__String*)base;
		base->refCount += 2;
		struct BSG_Instance__String* this = &b->String;
		this->class->deinit(this);
		base->refCount--;
		free(base);
	}
}
struct BSG_BaseClass BSG_BaseClassSingleton__String = {
	.cast = &BSG_BaseMethod__String_cast,
	.retain = &BSG_BaseMethod__String_retain,
	.release = &BSG_BaseMethod__String_release,
};
BSG_Void BSG_Method__String_deinit(struct BSG_Instance__String* this) {
	free(this->cStr);
	if(this) {
		this->baseInstance->baseClass->release(this->baseInstance);
	}
	return;
}
struct BSG_Class__String BSG_ClassSingleton__String_String = {
	.deinit = &BSG_Method__String_deinit,
};
struct BSG_Instance__String* BSG_Constructor__String() {
	struct BSG_BaseInstance__String* baseInstance = malloc(sizeof(struct BSG_BaseInstance__String));
	baseInstance->String = (struct BSG_Instance__String) {
		.baseInstance = (struct BSG_AnyBaseInstance*)baseInstance,
		.class = &BSG_ClassSingleton__String_String,
	};
	baseInstance->baseClass = (struct BSG_AnyBaseClass*) &BSG_BaseClassSingleton__String;
	return &baseInstance->String;
}
