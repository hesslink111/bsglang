#ifndef BSG_H__Equatable
#define BSG_H__Equatable

// Includes
#include "bsg_preamble.h"

// Type Num
#define BSG_Type__Equatable 4l

// Instance
struct BSG_Instance__Equatable {
struct BSG_AnyBaseInstance* baseInstance;
struct BSG_Class__Equatable* class;
};
typedef struct BSG_Instance__Equatable* BSG_InstancePtr__Equatable;

// Base Instance
struct BSG_BaseInstance__Equatable {
    struct BSG_AnyBaseClass* baseClass;
    int refCount;
    struct BSG_Instance__Equatable Equatable;
};

// Method Typedefs
#ifndef BSG_MethodDef__｢BSG_InstancePtr__Equatable｣￫BSG_Bool
#define BSG_MethodDef__｢BSG_InstancePtr__Equatable｣￫BSG_Bool
typedef BSG_Bool (*BSG_Function__｢BSG_InstancePtr__Equatable｣￫BSG_Bool)(BSG_AnyInstance*,BSG_InstancePtr__Equatable);
typedef struct BSG_MethodFatPtr__｢BSG_InstancePtr__Equatable｣￫BSG_Bool {
    BSG_AnyInstance* this;
    BSG_Function__｢BSG_InstancePtr__Equatable｣￫BSG_Bool method;
} BSG_MethodFatPtr__｢BSG_InstancePtr__Equatable｣￫BSG_Bool;
typedef struct BSG_MethodFatPtr__｢BSG_InstancePtr__Equatable｣￫BSG_Bool BSG_MethodFatPtr__｢BSG_InstancePtr__Equatable｣￫BSG_Bool;
#endif

// Class
struct BSG_Class__Equatable {
    BSG_Function__｢BSG_InstancePtr__Equatable｣￫BSG_Bool equals;
};

// Base Methods
BSG_AnyInstance* BSG_BaseMethod__Equatable_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type);
BSG_Bool BSG_BaseMethod__Equatable_canCast(struct BSG_AnyBaseInstance* base, BSG_AnyType type);
void BSG_BaseMethod__Equatable_retain(struct BSG_AnyBaseInstance* base);
void BSG_BaseMethod__Equatable_release(struct BSG_AnyBaseInstance* base);

// Base Class Singleton
extern struct BSG_BaseClass BSG_BaseClassSingleton__Equatable;

// Methods
BSG_Bool BSG_Method__Equatable·equals(BSG_AnyInstance* _tmp_0,BSG_InstancePtr__Equatable other);

// Class Singletons
extern struct BSG_Class__Equatable BSG_ClassSingleton__Equatable_Equatable;

// Constructor
extern struct BSG_Instance__Equatable* BSG_Constructor__Equatable();

// Singleton

#endif
