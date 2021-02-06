#ifndef BSG_H__Animal
#define BSG_H__Animal
#include "bsg_preamble.h"
#include "String.h"
#define BSG_Type__Animal 6l
struct BSG_Instance__Animal {
	struct BSG_AnyBaseInstance* baseInstance;
	struct BSG_Class__Animal* class;
};
struct BSG_BaseInstance__Animal {
	struct BSG_AnyBaseClass* baseClass;
	int refCount;
	struct BSG_Instance__Animal Animal;
};
struct BSG_Class__Animal {
	struct BSG_Instance__String* (*talk)(struct BSG_Instance__Animal*);
};
struct BSG_AnyInstance* BSG_BaseMethod__Animal_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type);
void BSG_BaseMethod__Animal_retain(struct BSG_AnyBaseInstance* base);
void BSG_BaseMethod__Animal_release(struct BSG_AnyBaseInstance* base);
extern struct BSG_BaseClass BSG_BaseClassSingleton__Animal;
struct BSG_Instance__String* BSG_Method__Animal_Animal_talk(struct BSG_Instance__Animal* this);
struct BSG_MethodFatPtr__Animal_talk {
	struct BSG_Instance__Animal* this;
	struct BSG_Instance__String* (*method)(struct BSG_Instance__Animal* this);
};
extern struct BSG_Class__Animal BSG_ClassSingleton__Animal_Animal;
extern struct BSG_Instance__Animal* BSG_Constructor__Animal();
#endif
