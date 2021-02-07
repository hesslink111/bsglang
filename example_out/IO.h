#ifndef BSG_H__IO
#define BSG_H__IO
#include "bsg_preamble.h"
#include <stdio.h>
#include "String.h"
#define BSG_Type__IO 3l
struct BSG_Instance__IO {
	struct BSG_AnyBaseInstance* baseInstance;
	struct BSG_Class__IO* class;
};
struct BSG_BaseInstance__IO {
	struct BSG_AnyBaseClass* baseClass;
	int refCount;
	struct BSG_Instance__IO IO;
};
struct BSG_Class__IO {
	BSG_Void (*println)(struct BSG_Instance__IO*,struct BSG_Instance__String*);
};
struct BSG_AnyInstance* BSG_BaseMethod__IO_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type);
BSG_Bool BSG_BaseMethod__IO_canCast(struct BSG_AnyBaseInstance* base, BSG_AnyType type);
void BSG_BaseMethod__IO_retain(struct BSG_AnyBaseInstance* base);
void BSG_BaseMethod__IO_release(struct BSG_AnyBaseInstance* base);
extern struct BSG_BaseClass BSG_BaseClassSingleton__IO;
BSG_Void BSG_Method__IO_IO_println(struct BSG_Instance__IO* this,struct BSG_Instance__String* message);
struct BSG_MethodFatPtr__IO_println {
	struct BSG_Instance__IO* this;
	BSG_Void (*method)(struct BSG_Instance__IO* this,struct BSG_Instance__String* message);
};
extern struct BSG_Class__IO BSG_ClassSingleton__IO_IO;
extern struct BSG_Instance__IO* BSG_Constructor__IO();
extern struct BSG_Instance__IO* IO;
#endif
