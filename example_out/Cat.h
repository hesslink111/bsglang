#ifndef BSG_H__Cat
#define BSG_H__Cat
#include "bsg_preamble.h"
#include "Animal.h"
#include "String.h"
#include "IO.h"
#define BSG_Type__Cat 4l
struct BSG_Instance__Cat {
	struct BSG_AnyBaseInstance* baseInstance;
	struct BSG_Class__Cat* class;
};
struct BSG_BaseInstance__Cat {
	struct BSG_AnyBaseClass* baseClass;
	int refCount;
	struct BSG_Instance__Cat Cat;
	struct BSG_Instance__Animal Animal;
};
struct BSG_Class__Cat {
};
struct BSG_AnyInstance* BSG_BaseMethod__Cat_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type);
void BSG_BaseMethod__Cat_retain(struct BSG_AnyBaseInstance* base);
void BSG_BaseMethod__Cat_release(struct BSG_AnyBaseInstance* base);
extern struct BSG_BaseClass BSG_BaseClassSingleton__Cat;
struct BSG_Instance__String* BSG_Method__Cat_Animal_talk(struct BSG_Instance__Animal* __0);
extern struct BSG_Class__Cat BSG_ClassSingleton__Cat_Cat;
extern struct BSG_Class__Animal BSG_ClassSingleton__Cat_Animal;
extern struct BSG_Instance__Cat* BSG_Constructor__Cat();
#endif
