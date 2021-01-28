#ifndef BSG_H__MainClass
#define BSG_H__MainClass
#include "bsg_preamble.h"
#include "IO.h"
const BSG_AnyType BSG_Type__MainClass;
struct BSG_Instance__MainClass {
	struct BSG_AnyBaseInstance* baseInstance;
	struct BSG_Class__MainClass* class;
};
struct BSG_BaseInstance__MainClass {
	struct BSG_AnyBaseClass* baseClass;
	int refCount;
	struct BSG_Instance__MainClass MainClass;
};
struct BSG_Class__MainClass {
	BSG_Void (*main)(struct BSG_Instance__MainClass*);
};
struct BSG_AnyInstance* BSG_BaseMethod__MainClass_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type);
void BSG_BaseMethod__MainClass_retain(struct BSG_AnyBaseInstance* base);
void BSG_BaseMethod__MainClass_release(struct BSG_AnyBaseInstance* base);
extern struct BSG_BaseClass BSG_BaseClassSingleton__MainClass;
BSG_Void BSG_Method__MainClass_main(struct BSG_Instance__MainClass* this);
struct BSG_MethodFatPtr__MainClass_main {
	struct BSG_Instance__MainClass* this;
	BSG_Void (*method)(struct BSG_Instance__MainClass* this);
};
extern struct BSG_Class__MainClass BSG_ClassSingleton__MainClass_MainClass;
extern struct BSG_Instance__MainClass* BSG_Constructor__MainClass();
extern struct BSG_Instance__MainClass* MainClass;
#endif
