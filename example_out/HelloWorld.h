#ifndef BSG_H__HelloWorld
#define BSG_H__HelloWorld
#include "bsg_preamble.h"
#include "IO.h"
#define BSG_Type__HelloWorld 0l
struct BSG_Instance__HelloWorld {
	struct BSG_AnyBaseInstance* baseInstance;
	struct BSG_Class__HelloWorld* class;
};
typedef struct BSG_Instance__HelloWorld* BSG_InstancePtr__HelloWorld;
struct BSG_BaseInstance__HelloWorld {
	struct BSG_AnyBaseClass* baseClass;
	int refCount;
	struct BSG_Instance__HelloWorld HelloWorld;
};
#ifndef BSG_MethodDef__｢｣￫BSG_Void
#define BSG_MethodDef__｢｣￫BSG_Void
typedef BSG_Void (*BSG_Function__｢BSG_AnyInstancePtr｣￫BSG_Void)(BSG_AnyInstancePtr);
typedef struct BSG_MethodFatPtr__｢｣￫BSG_Void {
	BSG_AnyInstancePtr this;
	BSG_Function__｢BSG_AnyInstancePtr｣￫BSG_Void method;
} BSG_MethodFatPtr__｢｣￫BSG_Void;
typedef struct BSG_MethodFatPtr__｢｣￫BSG_Void BSG_MethodFatPtr__｢｣￫BSG_Void;
#endif
struct BSG_Class__HelloWorld {
	BSG_Function__｢BSG_AnyInstancePtr｣￫BSG_Void main;
};
struct BSG_AnyInstance* BSG_BaseMethod__HelloWorld_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type);
BSG_Bool BSG_BaseMethod__HelloWorld_canCast(struct BSG_AnyBaseInstance* base, BSG_AnyType type);
void BSG_BaseMethod__HelloWorld_retain(struct BSG_AnyBaseInstance* base);
void BSG_BaseMethod__HelloWorld_release(struct BSG_AnyBaseInstance* base);
extern struct BSG_BaseClass BSG_BaseClassSingleton__HelloWorld;
BSG_Void BSG_Method__HelloWorld·main(BSG_AnyInstancePtr _tmp_0);
extern struct BSG_Class__HelloWorld BSG_ClassSingleton__HelloWorld_HelloWorld;
extern struct BSG_Instance__HelloWorld* BSG_Constructor__HelloWorld();
extern struct BSG_Instance__HelloWorld* HelloWorld;
#endif
