#ifndef BSG_H__Hashable
#define BSG_H__Hashable

// Includes
#include "bsg_preamble.h"
#include "Equatable.h"

// Type Num
#define BSG_Type__Hashable 3l

// Instance
struct BSG_Instance__Hashable {
struct BSG_AnyBaseInstance* baseInstance;
struct BSG_Class__Hashable* class;
};
typedef struct BSG_Instance__Hashable* BSG_InstancePtr__Hashable;

// Base Instance
struct BSG_BaseInstance__Hashable {
    struct BSG_AnyBaseClass* baseClass;
    int refCount;
    struct BSG_Instance__Hashable Hashable;
    struct BSG_Instance__Equatable Equatable;
};

// Method Typedefs
#ifndef BSG_MethodDef__｢｣￫BSG_Int
#define BSG_MethodDef__｢｣￫BSG_Int
typedef BSG_Int (*BSG_Function__｢｣￫BSG_Int)(BSG_AnyInstance*);
typedef struct BSG_MethodFatPtr__｢｣￫BSG_Int {
    BSG_AnyInstance* this;
    BSG_Function__｢｣￫BSG_Int method;
} BSG_MethodFatPtr__｢｣￫BSG_Int;
typedef struct BSG_MethodFatPtr__｢｣￫BSG_Int BSG_MethodFatPtr__｢｣￫BSG_Int;
#endif
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
struct BSG_Class__Hashable {
    BSG_Function__｢｣￫BSG_Int hashCode;
};

// Base Methods
BSG_AnyInstance* BSG_BaseMethod__Hashable_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type);
BSG_Bool BSG_BaseMethod__Hashable_canCast(struct BSG_AnyBaseInstance* base, BSG_AnyType type);
void BSG_BaseMethod__Hashable_retain(struct BSG_AnyBaseInstance* base);
void BSG_BaseMethod__Hashable_release(struct BSG_AnyBaseInstance* base);

// Base Class Singleton
extern struct BSG_BaseClass BSG_BaseClassSingleton__Hashable;

// Methods
BSG_Int BSG_Method__Hashable·hashCode(BSG_AnyInstance* _tmp_0);
BSG_Bool BSG_Method__Hashable·equals(BSG_AnyInstance* _tmp_2,BSG_InstancePtr__Equatable other);

// Class Singletons
extern struct BSG_Class__Hashable BSG_ClassSingleton__Hashable_Hashable;
extern struct BSG_Class__Equatable BSG_ClassSingleton__Hashable_Equatable;

// Constructor
extern struct BSG_Instance__Hashable* BSG_Constructor__Hashable();

// Singleton

#endif
