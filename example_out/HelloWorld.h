#ifndef BSG_H__HelloWorld
#define BSG_H__HelloWorld
#include "bsg_preamble.h"
#include "IO.h"
#define BSG_Type__HelloWorld 0l
struct BSG_Instance__HelloWorld {
struct BSG_AnyBaseInstance* baseInstance;
struct BSG_Class__HelloWorld* class;
};
struct BSG_BaseInstance__HelloWorld {
struct BSG_AnyBaseClass* baseClass;
int refCount;
struct BSG_Instance__HelloWorld HelloWorld;
};
struct BSG_Class__HelloWorld {
BSG_Void (*main)(struct BSG_Instance__HelloWorld*);
};
struct BSG_AnyInstance* BSG_BaseMethod__HelloWorld_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type);
BSG_Bool BSG_BaseMethod__HelloWorld_canCast(struct BSG_AnyBaseInstance* base, BSG_AnyType type);
void BSG_BaseMethod__HelloWorld_retain(struct BSG_AnyBaseInstance* base);
void BSG_BaseMethod__HelloWorld_release(struct BSG_AnyBaseInstance* base);
extern struct BSG_BaseClass BSG_BaseClassSingleton__HelloWorld;
BSG_Void BSG_Method__HelloWorld_HelloWorld_main(struct BSG_Instance__HelloWorld* this);
struct BSG_MethodFatPtr__HelloWorld_main {
struct BSG_Instance__HelloWorld* this;
BSG_Void (*method)(struct BSG_Instance__HelloWorld* this);
};
extern struct BSG_Class__HelloWorld BSG_ClassSingleton__HelloWorld_HelloWorld;
extern struct BSG_Instance__HelloWorld* BSG_Constructor__HelloWorld();
extern struct BSG_Instance__HelloWorld* HelloWorld;
#endif
