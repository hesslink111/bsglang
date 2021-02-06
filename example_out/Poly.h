#ifndef BSG_H__Poly
#define BSG_H__Poly
#include "bsg_preamble.h"
#include "Dog.h"
#include "Cat.h"
#include "Animal.h"
#include "String.h"
#include "IO.h"
#define BSG_Type__Poly 0l
struct BSG_Instance__Poly {
	struct BSG_AnyBaseInstance* baseInstance;
	struct BSG_Class__Poly* class;
};
struct BSG_BaseInstance__Poly {
	struct BSG_AnyBaseClass* baseClass;
	int refCount;
	struct BSG_Instance__Poly Poly;
};
struct BSG_Class__Poly {
	BSG_Void (*main)(struct BSG_Instance__Poly*);
	struct BSG_Instance__String* (*getAnimalSound)(struct BSG_Instance__Poly*,struct BSG_Instance__Animal*);
};
struct BSG_AnyInstance* BSG_BaseMethod__Poly_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type);
void BSG_BaseMethod__Poly_retain(struct BSG_AnyBaseInstance* base);
void BSG_BaseMethod__Poly_release(struct BSG_AnyBaseInstance* base);
extern struct BSG_BaseClass BSG_BaseClassSingleton__Poly;
BSG_Void BSG_Method__Poly_Poly_main(struct BSG_Instance__Poly* this);
struct BSG_MethodFatPtr__Poly_main {
	struct BSG_Instance__Poly* this;
	BSG_Void (*method)(struct BSG_Instance__Poly* this);
};
struct BSG_Instance__String* BSG_Method__Poly_Poly_getAnimalSound(struct BSG_Instance__Poly* this,struct BSG_Instance__Animal* animal);
struct BSG_MethodFatPtr__Poly_getAnimalSound {
	struct BSG_Instance__Poly* this;
	struct BSG_Instance__String* (*method)(struct BSG_Instance__Poly* this,struct BSG_Instance__Animal* animal);
};
extern struct BSG_Class__Poly BSG_ClassSingleton__Poly_Poly;
extern struct BSG_Instance__Poly* BSG_Constructor__Poly();
extern struct BSG_Instance__Poly* Poly;
#endif
