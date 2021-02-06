#ifndef BSG_H__StringExtensions
#define BSG_H__StringExtensions
#include "bsg_preamble.h"
#include <stdio.h>
#include "String.h"
const BSG_AnyType BSG_Type__StringExtensions;
struct BSG_Instance__StringExtensions {
	struct BSG_AnyBaseInstance* baseInstance;
	struct BSG_Class__StringExtensions* class;
};
struct BSG_BaseInstance__StringExtensions {
	struct BSG_AnyBaseClass* baseClass;
	int refCount;
	struct BSG_Instance__StringExtensions StringExtensions;
};
struct BSG_Class__StringExtensions {
	struct BSG_Instance__String* (*intToString)(struct BSG_Instance__StringExtensions*,BSG_Int);
	struct BSG_Instance__String* (*opaqueToString)(struct BSG_Instance__StringExtensions*,BSG_Opaque);
};
struct BSG_AnyInstance* BSG_BaseMethod__StringExtensions_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type);
void BSG_BaseMethod__StringExtensions_retain(struct BSG_AnyBaseInstance* base);
void BSG_BaseMethod__StringExtensions_release(struct BSG_AnyBaseInstance* base);
extern struct BSG_BaseClass BSG_BaseClassSingleton__StringExtensions;
struct BSG_Instance__String* BSG_Method__StringExtensions_intToString(struct BSG_Instance__StringExtensions* this,BSG_Int i);
struct BSG_MethodFatPtr__StringExtensions_intToString {
	struct BSG_Instance__StringExtensions* this;
	struct BSG_Instance__String* (*method)(struct BSG_Instance__StringExtensions* this,BSG_Int i);
};
struct BSG_Instance__String* BSG_Method__StringExtensions_opaqueToString(struct BSG_Instance__StringExtensions* this,BSG_Opaque i);
struct BSG_MethodFatPtr__StringExtensions_opaqueToString {
	struct BSG_Instance__StringExtensions* this;
	struct BSG_Instance__String* (*method)(struct BSG_Instance__StringExtensions* this,BSG_Opaque i);
};
extern struct BSG_Class__StringExtensions BSG_ClassSingleton__StringExtensions_StringExtensions;
extern struct BSG_Instance__StringExtensions* BSG_Constructor__StringExtensions();
extern struct BSG_Instance__StringExtensions* StringExtensions;
#endif
