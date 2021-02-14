#ifndef BSG_H__Hashable
#define BSG_H__Hashable
#include "bsg_preamble.h"
#define BSG_Type__Hashable 3l
struct BSG_Instance__Hashable {
	struct BSG_AnyBaseInstance* baseInstance;
	struct BSG_Class__Hashable* class;
};
typedef struct BSG_Instance__Hashable* BSG_InstancePtr__Hashable;
struct BSG_BaseInstance__Hashable {
	struct BSG_AnyBaseClass* baseClass;
	int refCount;
	struct BSG_Instance__Hashable Hashable;
};
#ifndef BSG_MethodDef__｢｣￫BSG_Int
#define BSG_MethodDef__｢｣￫BSG_Int
typedef BSG_Int (*BSG_Function__｢BSG_AnyInstancePtr｣￫BSG_Int)(BSG_AnyInstancePtr);
typedef struct BSG_MethodFatPtr__｢｣￫BSG_Int {
	BSG_AnyInstancePtr this;
	BSG_Function__｢BSG_AnyInstancePtr｣￫BSG_Int method;
} BSG_MethodFatPtr__｢｣￫BSG_Int;
typedef struct BSG_MethodFatPtr__｢｣￫BSG_Int BSG_MethodFatPtr__｢｣￫BSG_Int;
#endif
#ifndef BSG_MethodDef__｢BSG_InstancePtr__Hashable｣￫BSG_Bool
#define BSG_MethodDef__｢BSG_InstancePtr__Hashable｣￫BSG_Bool
typedef BSG_Bool (*BSG_Function__｢BSG_AnyInstancePtr·BSG_InstancePtr__Hashable｣￫BSG_Bool)(BSG_AnyInstancePtr,BSG_InstancePtr__Hashable);
typedef struct BSG_MethodFatPtr__｢BSG_InstancePtr__Hashable｣￫BSG_Bool {
	BSG_AnyInstancePtr this;
	BSG_Function__｢BSG_AnyInstancePtr·BSG_InstancePtr__Hashable｣￫BSG_Bool method;
} BSG_MethodFatPtr__｢BSG_InstancePtr__Hashable｣￫BSG_Bool;
typedef struct BSG_MethodFatPtr__｢BSG_InstancePtr__Hashable｣￫BSG_Bool BSG_MethodFatPtr__｢BSG_InstancePtr__Hashable｣￫BSG_Bool;
#endif
struct BSG_Class__Hashable {
	BSG_Function__｢BSG_AnyInstancePtr｣￫BSG_Int hashCode;
	BSG_Function__｢BSG_AnyInstancePtr·BSG_InstancePtr__Hashable｣￫BSG_Bool equals;
};
struct BSG_AnyInstance* BSG_BaseMethod__Hashable_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type);
BSG_Bool BSG_BaseMethod__Hashable_canCast(struct BSG_AnyBaseInstance* base, BSG_AnyType type);
void BSG_BaseMethod__Hashable_retain(struct BSG_AnyBaseInstance* base);
void BSG_BaseMethod__Hashable_release(struct BSG_AnyBaseInstance* base);
extern struct BSG_BaseClass BSG_BaseClassSingleton__Hashable;
BSG_Int BSG_Method__Hashable·hashCode(BSG_AnyInstancePtr _tmp_0);
BSG_Bool BSG_Method__Hashable·equals(BSG_AnyInstancePtr _tmp_2,BSG_InstancePtr__Hashable other);
extern struct BSG_Class__Hashable BSG_ClassSingleton__Hashable_Hashable;
extern struct BSG_Instance__Hashable* BSG_Constructor__Hashable();
#endif
