#include "StringExtensions.h"
struct BSG_AnyInstance* BSG_BaseMethod__StringExtensions_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type) {
	struct BSG_BaseInstance__StringExtensions* b = (struct BSG_BaseInstance__StringExtensions*)base;
	switch(type) {
	case BSG_Type__StringExtensions:
		return (struct BSG_AnyInstance*)&b->StringExtensions;
	}
	return NULL;
}
void BSG_BaseMethod__StringExtensions_retain(struct BSG_AnyBaseInstance* base) {
	base->refCount++;
}
void BSG_BaseMethod__StringExtensions_release(struct BSG_AnyBaseInstance* base) {
	base->refCount--;
	if(base->refCount <= 0) {
		struct BSG_BaseInstance__StringExtensions* b = (struct BSG_BaseInstance__StringExtensions*)base;
		free(base);
	}
}
struct BSG_BaseClass BSG_BaseClassSingleton__StringExtensions = {
	.cast = &BSG_BaseMethod__StringExtensions_cast,
	.retain = &BSG_BaseMethod__StringExtensions_retain,
	.release = &BSG_BaseMethod__StringExtensions_release,
};
struct BSG_Instance__String* BSG_Method__StringExtensions_StringExtensions_intToString(struct BSG_Instance__StringExtensions* this,BSG_Int i) {
	struct BSG_Instance__String* str;
	struct BSG_Instance__String** __0;
	__0 = &str;
	struct BSG_Instance__String* __1 = BSG_Constructor__String();
	__1->baseInstance->baseClass->retain(__1->baseInstance);
	*__0 = __1;
	str->cStr = malloc(1024);
	snprintf(str->cStr, 1024, "%d", i);
	if(this) {
		this->baseInstance->baseClass->release(this->baseInstance);
	}
	return str;
}
struct BSG_Instance__String* BSG_Method__StringExtensions_StringExtensions_opaqueToString(struct BSG_Instance__StringExtensions* this,BSG_Opaque i) {
	struct BSG_Instance__String* str;
	struct BSG_Instance__String** __2;
	__2 = &str;
	struct BSG_Instance__String* __3 = BSG_Constructor__String();
	__3->baseInstance->baseClass->retain(__3->baseInstance);
	*__2 = __3;
	str->cStr = malloc(1024);
	snprintf(str->cStr, 1024, "%p", i);
	if(this) {
		this->baseInstance->baseClass->release(this->baseInstance);
	}
	return str;
}
struct BSG_Class__StringExtensions BSG_ClassSingleton__StringExtensions_StringExtensions = {
	.intToString = &BSG_Method__StringExtensions_StringExtensions_intToString,
	.opaqueToString = &BSG_Method__StringExtensions_StringExtensions_opaqueToString,
};
struct BSG_Instance__StringExtensions* BSG_Constructor__StringExtensions() {
	struct BSG_BaseInstance__StringExtensions* baseInstance = malloc(sizeof(struct BSG_BaseInstance__StringExtensions));
	baseInstance->StringExtensions = (struct BSG_Instance__StringExtensions) {
		.baseInstance = (struct BSG_AnyBaseInstance*)baseInstance,
		.class = &BSG_ClassSingleton__StringExtensions_StringExtensions,
	};
	baseInstance->baseClass = (struct BSG_AnyBaseClass*) &BSG_BaseClassSingleton__StringExtensions;
	return &baseInstance->StringExtensions;
}
struct BSG_Instance__StringExtensions* StringExtensions = NULL;
