#ifndef BSG_H__IO
#define BSG_H__IO

// Includes
#include "bsg_preamble.h"
#include <stdio.h>
#include "String.h"

// Type Num
#define BSG_Type__IO 1l

// Instance
struct BSG_Instance__IO {
struct BSG_AnyBaseInstance* baseInstance;
struct BSG_Class__IO* class;
};
typedef struct BSG_Instance__IO* BSG_InstancePtr__IO;

// Base Instance
struct BSG_BaseInstance__IO {
    struct BSG_AnyBaseClass* baseClass;
    int refCount;
    struct BSG_Instance__IO IO;
};

// Method Typedefs
#ifndef BSG_MethodDef__｢BSG_InstancePtr__String｣￫BSG_Void
#define BSG_MethodDef__｢BSG_InstancePtr__String｣￫BSG_Void
typedef BSG_Void (*BSG_Function__｢BSG_AnyInstancePtr·BSG_InstancePtr__String｣￫BSG_Void)(BSG_AnyInstancePtr,BSG_InstancePtr__String);
typedef struct BSG_MethodFatPtr__｢BSG_InstancePtr__String｣￫BSG_Void {
    BSG_AnyInstancePtr this;
    BSG_Function__｢BSG_AnyInstancePtr·BSG_InstancePtr__String｣￫BSG_Void method;
} BSG_MethodFatPtr__｢BSG_InstancePtr__String｣￫BSG_Void;
typedef struct BSG_MethodFatPtr__｢BSG_InstancePtr__String｣￫BSG_Void BSG_MethodFatPtr__｢BSG_InstancePtr__String｣￫BSG_Void;
#endif

// Class
struct BSG_Class__IO {
    BSG_Function__｢BSG_AnyInstancePtr·BSG_InstancePtr__String｣￫BSG_Void println;
};

// Base Methods
struct BSG_AnyInstance* BSG_BaseMethod__IO_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type);
BSG_Bool BSG_BaseMethod__IO_canCast(struct BSG_AnyBaseInstance* base, BSG_AnyType type);
void BSG_BaseMethod__IO_retain(struct BSG_AnyBaseInstance* base);
void BSG_BaseMethod__IO_release(struct BSG_AnyBaseInstance* base);

// Base Class Singleton
extern struct BSG_BaseClass BSG_BaseClassSingleton__IO;

// Methods
BSG_Void BSG_Method__IO·println(BSG_AnyInstancePtr _tmp_0,BSG_InstancePtr__String message);

// Class Singletons
extern struct BSG_Class__IO BSG_ClassSingleton__IO_IO;

// Constructor
extern struct BSG_Instance__IO* BSG_Constructor__IO();

// Singleton
extern struct BSG_Instance__IO* IO;

#endif
