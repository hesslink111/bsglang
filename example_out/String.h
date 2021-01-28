#ifndef BSG_H__String
#define BSG_H__String
#include "bsg_preamble.h"
#include <string.h>
const BSG_AnyType BSG_Type__String;
struct BSG_Instance__String {
	struct BSG_AnyBaseInstance* baseInstance;
	struct BSG_Class__String* class;
	BSG_Int length;
	BSG_Opaque cStr;
};
struct BSG_BaseInstance__String {
	struct BSG_AnyBaseClass* baseClass;
	int refCount;
	struct BSG_Instance__String String;
};
struct BSG_Class__String {
	BSG_Void (*deinit)(struct BSG_Instance__String*);
};
struct BSG_AnyInstance* BSG_BaseMethod__String_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type);
void BSG_BaseMethod__String_retain(struct BSG_AnyBaseInstance* base);
void BSG_BaseMethod__String_release(struct BSG_AnyBaseInstance* base);
extern struct BSG_BaseClass BSG_BaseClassSingleton__String;
BSG_Void BSG_Method__String_deinit(struct BSG_Instance__String* this);
struct BSG_MethodFatPtr__String_deinit {
	struct BSG_Instance__String* this;
	BSG_Void (*method)(struct BSG_Instance__String* this);
};
extern struct BSG_Class__String BSG_ClassSingleton__String_String;
extern struct BSG_Instance__String* BSG_Constructor__String();
#endif
