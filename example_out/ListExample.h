#ifndef BSG_H__ListExample
#define BSG_H__ListExample
#include "bsg_preamble.h"
#include "IO.h"
#include "List.h"
#include "StringExtensions.h"
#include "Cat.h"
#include "Dog.h"
#include "Animal.h"
#define BSG_Type__ListExample 0l
struct BSG_Instance__ListExample {
	struct BSG_AnyBaseInstance* baseInstance;
	struct BSG_Class__ListExample* class;
};
struct BSG_BaseInstance__ListExample {
	struct BSG_AnyBaseClass* baseClass;
	int refCount;
	struct BSG_Instance__ListExample ListExample;
};
struct BSG_Class__ListExample {
	BSG_Void (*main)(struct BSG_Instance__ListExample*);
};
struct BSG_AnyInstance* BSG_BaseMethod__ListExample_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type);
void BSG_BaseMethod__ListExample_retain(struct BSG_AnyBaseInstance* base);
void BSG_BaseMethod__ListExample_release(struct BSG_AnyBaseInstance* base);
extern struct BSG_BaseClass BSG_BaseClassSingleton__ListExample;
BSG_Void BSG_Method__ListExample_ListExample_main(struct BSG_Instance__ListExample* this);
struct BSG_MethodFatPtr__ListExample_main {
	struct BSG_Instance__ListExample* this;
	BSG_Void (*method)(struct BSG_Instance__ListExample* this);
};
extern struct BSG_Class__ListExample BSG_ClassSingleton__ListExample_ListExample;
extern struct BSG_Instance__ListExample* BSG_Constructor__ListExample();
extern struct BSG_Instance__ListExample* ListExample;
#endif
