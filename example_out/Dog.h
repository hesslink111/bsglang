#ifndef BSG_H__Dog
#define BSG_H__Dog
#include "bsg_preamble.h"
#include "Animal.h"
#include "String.h"
#include "IO.h"
#define BSG_Type__Dog 5l
struct BSG_Instance__Dog {
	struct BSG_AnyBaseInstance* baseInstance;
	struct BSG_Class__Dog* class;
};
struct BSG_BaseInstance__Dog {
	struct BSG_AnyBaseClass* baseClass;
	int refCount;
	struct BSG_Instance__Dog Dog;
	struct BSG_Instance__Animal Animal;
};
struct BSG_Class__Dog {
};
struct BSG_AnyInstance* BSG_BaseMethod__Dog_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type);
void BSG_BaseMethod__Dog_retain(struct BSG_AnyBaseInstance* base);
void BSG_BaseMethod__Dog_release(struct BSG_AnyBaseInstance* base);
extern struct BSG_BaseClass BSG_BaseClassSingleton__Dog;
struct BSG_Instance__String* BSG_Method__Dog_Animal_talk(struct BSG_Instance__Animal* __0);
extern struct BSG_Class__Dog BSG_ClassSingleton__Dog_Dog;
extern struct BSG_Class__Animal BSG_ClassSingleton__Dog_Animal;
extern struct BSG_Instance__Dog* BSG_Constructor__Dog();
#endif
