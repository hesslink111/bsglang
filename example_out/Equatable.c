#include "Equatable.h"
struct BSG_AnyInstance* BSG_BaseMethod__Equatable_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type) {
	struct BSG_BaseInstance__Equatable* b = (struct BSG_BaseInstance__Equatable*)base;
	switch(type) {
	case BSG_Type__Equatable:
		return (struct BSG_AnyInstance*)&b->Equatable;
	}
	return NULL;
}
BSG_Bool BSG_BaseMethod__Equatable_canCast(struct BSG_AnyBaseInstance* base, BSG_AnyType type) {
	struct BSG_BaseInstance__Equatable* b = (struct BSG_BaseInstance__Equatable*)base;
	switch(type) {
	case BSG_Type__Equatable:
		return true;
	}
	return false;
}
void BSG_BaseMethod__Equatable_retain(struct BSG_AnyBaseInstance* base) {
	base->refCount++;
}
void BSG_BaseMethod__Equatable_release(struct BSG_AnyBaseInstance* base) {
	base->refCount--;
	if(base->refCount <= 0) {
		struct BSG_BaseInstance__Equatable* b = (struct BSG_BaseInstance__Equatable*)base;
		free(base);
	}
}
struct BSG_BaseClass BSG_BaseClassSingleton__Equatable = {
	.cast = &BSG_BaseMethod__Equatable_cast,
	.canCast = &BSG_BaseMethod__Equatable_canCast,
	.retain = &BSG_BaseMethod__Equatable_retain,
	.release = &BSG_BaseMethod__Equatable_release,
};
BSG_Bool BSG_Method__Equatable·equals(BSG_AnyInstancePtr _tmp_0,BSG_InstancePtr__Equatable other) {
	BSG_InstancePtr__Equatable this = (struct BSG_Instance__Equatable*)_tmp_0;
	BSG_Bool _tmp_1 = false;
	if(this) {
		this->baseInstance->baseClass->release(this->baseInstance);
	}
	if(other) {
		other->baseInstance->baseClass->release(other->baseInstance);
	}
	return _tmp_1;
}
struct BSG_Class__Equatable BSG_ClassSingleton__Equatable_Equatable = {
	.equals = &BSG_Method__Equatable·equals,
};
struct BSG_Instance__Equatable* BSG_Constructor__Equatable() {
	struct BSG_BaseInstance__Equatable* baseInstance = malloc(sizeof(struct BSG_BaseInstance__Equatable));
	baseInstance->Equatable = (struct BSG_Instance__Equatable) {
		.baseInstance = (struct BSG_AnyBaseInstance*)baseInstance,
		.class = &BSG_ClassSingleton__Equatable_Equatable,
	};
	baseInstance->baseClass = (struct BSG_AnyBaseClass*) &BSG_BaseClassSingleton__Equatable;
	return &baseInstance->Equatable;
}
