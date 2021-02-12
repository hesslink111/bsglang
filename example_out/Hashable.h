#ifndef BSG_H__Hashable
#define BSG_H__Hashable
#include "bsg_preamble.h"
#define BSG_Type__Hashable 3l
struct BSG_Instance__Hashable {
struct BSG_AnyBaseInstance* baseInstance;
struct BSG_Class__Hashable* class;
};
struct BSG_BaseInstance__Hashable {
struct BSG_AnyBaseClass* baseClass;
int refCount;
struct BSG_Instance__Hashable Hashable;
};
struct BSG_Class__Hashable {
BSG_Int (*hashCode)(struct BSG_Instance__Hashable*);
BSG_Bool (*equals)(struct BSG_Instance__Hashable*,struct BSG_Instance__Hashable*);
};
struct BSG_AnyInstance* BSG_BaseMethod__Hashable_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type);
BSG_Bool BSG_BaseMethod__Hashable_canCast(struct BSG_AnyBaseInstance* base, BSG_AnyType type);
void BSG_BaseMethod__Hashable_retain(struct BSG_AnyBaseInstance* base);
void BSG_BaseMethod__Hashable_release(struct BSG_AnyBaseInstance* base);
extern struct BSG_BaseClass BSG_BaseClassSingleton__Hashable;
BSG_Int BSG_Method__Hashable_Hashable_hashCode(struct BSG_Instance__Hashable* this);
struct BSG_MethodFatPtr__Hashable_hashCode {
struct BSG_Instance__Hashable* this;
BSG_Int (*method)(struct BSG_Instance__Hashable* this);
};
BSG_Bool BSG_Method__Hashable_Hashable_equals(struct BSG_Instance__Hashable* this,struct BSG_Instance__Hashable* other);
struct BSG_MethodFatPtr__Hashable_equals {
struct BSG_Instance__Hashable* this;
BSG_Bool (*method)(struct BSG_Instance__Hashable* this,struct BSG_Instance__Hashable* other);
};
extern struct BSG_Class__Hashable BSG_ClassSingleton__Hashable_Hashable;
extern struct BSG_Instance__Hashable* BSG_Constructor__Hashable();
#endif
