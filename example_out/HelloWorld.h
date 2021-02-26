#ifndef BSG_H__HelloWorld
#define BSG_H__HelloWorld

// Includes
#include "bsg_preamble.h"
#include "IO.h"

// Type Num
#define BSG_Type__HelloWorld 0l

// Instance
struct BSG_Instance__HelloWorld {
struct BSG_AnyBaseInstance* baseInstance;
struct BSG_Class__HelloWorld* class;
};
typedef struct BSG_Instance__HelloWorld* BSG_InstancePtr__HelloWorld;

// Base Instance
struct BSG_BaseInstance__HelloWorld {
    struct BSG_AnyBaseClass* baseClass;
    int refCount;
    struct BSG_Instance__HelloWorld HelloWorld;
};

// Method Typedefs
#ifndef BSG_MethodDef__｢｣￫BSG_Void
#define BSG_MethodDef__｢｣￫BSG_Void
typedef BSG_Void (*BSG_Function__｢｣￫BSG_Void)(BSG_AnyInstance*);
typedef struct BSG_MethodFatPtr__｢｣￫BSG_Void {
    BSG_AnyInstance* this;
    BSG_Function__｢｣￫BSG_Void method;
} BSG_MethodFatPtr__｢｣￫BSG_Void;
typedef struct BSG_MethodFatPtr__｢｣￫BSG_Void BSG_MethodFatPtr__｢｣￫BSG_Void;
#endif

// Class
struct BSG_Class__HelloWorld {
    BSG_Function__｢｣￫BSG_Void main;
};

// Base Methods
BSG_AnyInstance* BSG_BaseMethod__HelloWorld_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type);
BSG_Bool BSG_BaseMethod__HelloWorld_canCast(struct BSG_AnyBaseInstance* base, BSG_AnyType type);
void BSG_BaseMethod__HelloWorld_retain(struct BSG_AnyBaseInstance* base);
void BSG_BaseMethod__HelloWorld_release(struct BSG_AnyBaseInstance* base);

// Base Class Singleton
extern struct BSG_BaseClass BSG_BaseClassSingleton__HelloWorld;

// Methods
BSG_Void BSG_Method__HelloWorld·main(BSG_AnyInstance* _tmp_0);

// Class Singletons
extern struct BSG_Class__HelloWorld BSG_ClassSingleton__HelloWorld_HelloWorld;

// Constructor
extern struct BSG_Instance__HelloWorld* BSG_Constructor__HelloWorld();

// Singleton
extern struct BSG_Instance__HelloWorld* HelloWorld;

#endif
