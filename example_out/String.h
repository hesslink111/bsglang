#ifndef BSG_H__String
#define BSG_H__String

// Includes
#include "bsg_preamble.h"
#include <string.h>
#include "Hashable.h"

// Type Num
#define BSG_Type__String 2l

// Instance
struct BSG_Instance__String {
struct BSG_AnyBaseInstance* baseInstance;
struct BSG_Class__String* class;
BSG_Opaque cStr;
BSG_Int length;
BSG_Bool isLiteral;
};
typedef struct BSG_Instance__String* BSG_InstancePtr__String;

// Base Instance
struct BSG_BaseInstance__String {
    struct BSG_AnyBaseClass* baseClass;
    int refCount;
    struct BSG_Instance__String String;
    struct BSG_Instance__Hashable Hashable;
    struct BSG_Instance__Equatable Equatable;
};

// Method Typedefs
#ifndef BSG_MethodDef__｢｣￫BSG_Void
#define BSG_MethodDef__｢｣￫BSG_Void
typedef BSG_Void (*BSG_Function__｢BSG_AnyInstancePtr｣￫BSG_Void)(BSG_AnyInstancePtr);
typedef struct BSG_MethodFatPtr__｢｣￫BSG_Void {
    BSG_AnyInstancePtr this;
    BSG_Function__｢BSG_AnyInstancePtr｣￫BSG_Void method;
} BSG_MethodFatPtr__｢｣￫BSG_Void;
typedef struct BSG_MethodFatPtr__｢｣￫BSG_Void BSG_MethodFatPtr__｢｣￫BSG_Void;
#endif
#ifndef BSG_MethodDef__｢｣￫BSG_Int
#define BSG_MethodDef__｢｣￫BSG_Int
typedef BSG_Int (*BSG_Function__｢BSG_AnyInstancePtr｣￫BSG_Int)(BSG_AnyInstancePtr);
typedef struct BSG_MethodFatPtr__｢｣￫BSG_Int {
    BSG_AnyInstancePtr this;
    BSG_Function__｢BSG_AnyInstancePtr｣￫BSG_Int method;
} BSG_MethodFatPtr__｢｣￫BSG_Int;
typedef struct BSG_MethodFatPtr__｢｣￫BSG_Int BSG_MethodFatPtr__｢｣￫BSG_Int;
#endif
#ifndef BSG_MethodDef__｢BSG_InstancePtr__Equatable｣￫BSG_Bool
#define BSG_MethodDef__｢BSG_InstancePtr__Equatable｣￫BSG_Bool
typedef BSG_Bool (*BSG_Function__｢BSG_AnyInstancePtr·BSG_InstancePtr__Equatable｣￫BSG_Bool)(BSG_AnyInstancePtr,BSG_InstancePtr__Equatable);
typedef struct BSG_MethodFatPtr__｢BSG_InstancePtr__Equatable｣￫BSG_Bool {
    BSG_AnyInstancePtr this;
    BSG_Function__｢BSG_AnyInstancePtr·BSG_InstancePtr__Equatable｣￫BSG_Bool method;
} BSG_MethodFatPtr__｢BSG_InstancePtr__Equatable｣￫BSG_Bool;
typedef struct BSG_MethodFatPtr__｢BSG_InstancePtr__Equatable｣￫BSG_Bool BSG_MethodFatPtr__｢BSG_InstancePtr__Equatable｣￫BSG_Bool;
#endif
#ifndef BSG_MethodDef__｢BSG_InstancePtr__String｣￫BSG_InstancePtr__String
#define BSG_MethodDef__｢BSG_InstancePtr__String｣￫BSG_InstancePtr__String
typedef BSG_InstancePtr__String (*BSG_Function__｢BSG_AnyInstancePtr·BSG_InstancePtr__String｣￫BSG_InstancePtr__String)(BSG_AnyInstancePtr,BSG_InstancePtr__String);
typedef struct BSG_MethodFatPtr__｢BSG_InstancePtr__String｣￫BSG_InstancePtr__String {
    BSG_AnyInstancePtr this;
    BSG_Function__｢BSG_AnyInstancePtr·BSG_InstancePtr__String｣￫BSG_InstancePtr__String method;
} BSG_MethodFatPtr__｢BSG_InstancePtr__String｣￫BSG_InstancePtr__String;
typedef struct BSG_MethodFatPtr__｢BSG_InstancePtr__String｣￫BSG_InstancePtr__String BSG_MethodFatPtr__｢BSG_InstancePtr__String｣￫BSG_InstancePtr__String;
#endif

// Class
struct BSG_Class__String {
    BSG_Function__｢BSG_AnyInstancePtr｣￫BSG_Void init;
    BSG_Function__｢BSG_AnyInstancePtr·BSG_InstancePtr__String｣￫BSG_InstancePtr__String plus;
    BSG_Function__｢BSG_AnyInstancePtr｣￫BSG_Void deinit;
};

// Base Methods
struct BSG_AnyInstance* BSG_BaseMethod__String_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type);
BSG_Bool BSG_BaseMethod__String_canCast(struct BSG_AnyBaseInstance* base, BSG_AnyType type);
void BSG_BaseMethod__String_retain(struct BSG_AnyBaseInstance* base);
void BSG_BaseMethod__String_release(struct BSG_AnyBaseInstance* base);

// Base Class Singleton
extern struct BSG_BaseClass BSG_BaseClassSingleton__String;

// Methods
BSG_Void BSG_Method__String·init(BSG_AnyInstancePtr _tmp_0);
BSG_Int BSG_Method__String·hashCode(BSG_AnyInstancePtr _tmp_5);
BSG_Bool BSG_Method__String·equals(BSG_AnyInstancePtr _tmp_19,BSG_InstancePtr__Equatable other);
BSG_InstancePtr__String BSG_Method__String·plus(BSG_AnyInstancePtr _tmp_42,BSG_InstancePtr__String otherString);
BSG_Void BSG_Method__String·deinit(BSG_AnyInstancePtr _tmp_51);

// Class Singletons
extern struct BSG_Class__String BSG_ClassSingleton__String_String;
extern struct BSG_Class__Hashable BSG_ClassSingleton__String_Hashable;
extern struct BSG_Class__Equatable BSG_ClassSingleton__String_Equatable;

// Constructor
extern struct BSG_Instance__String* BSG_Constructor__String();

// Singleton

#endif
