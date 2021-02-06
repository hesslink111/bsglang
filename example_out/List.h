#ifndef BSG_H__List
#define BSG_H__List
#include "bsg_preamble.h"
#include <string.h>
#include "IO.h"
#include "StringExtensions.h"
const BSG_AnyType BSG_Type__List;
struct BSG_Instance__List {
	struct BSG_AnyBaseInstance* baseInstance;
	struct BSG_Class__List* class;
	BSG_Opaque arr;
	BSG_Int size;
	BSG_Int capacity;
};
struct BSG_BaseInstance__List {
	struct BSG_AnyBaseClass* baseClass;
	int refCount;
	struct BSG_Instance__List List;
};
struct BSG_Class__List {
	BSG_Void (*init)(struct BSG_Instance__List*);
	BSG_Void (*add)(struct BSG_Instance__List*,struct BSG_Any);
	BSG_Void (*set)(struct BSG_Instance__List*,BSG_Int,struct BSG_Any);
	struct BSG_Any (*get)( struct BSG_Instance__List*,BSG_Int);
	BSG_Void (*remove)(struct BSG_Instance__List*,BSG_Int);
	BSG_Void (*clear)(struct BSG_Instance__List*);
	BSG_Void (*releaseAll)(struct BSG_Instance__List*);
	BSG_Void (*deinit)(struct BSG_Instance__List*);
};
struct BSG_AnyInstance* BSG_BaseMethod__List_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type);
void BSG_BaseMethod__List_retain(struct BSG_AnyBaseInstance* base);
void BSG_BaseMethod__List_release(struct BSG_AnyBaseInstance* base);
extern struct BSG_BaseClass BSG_BaseClassSingleton__List;
BSG_Void BSG_Method__List_init(struct BSG_Instance__List* this);
struct BSG_MethodFatPtr__List_init {
	struct BSG_Instance__List* this;
	BSG_Void (*method)(struct BSG_Instance__List* this);
};
BSG_Void BSG_Method__List_add(struct BSG_Instance__List* this,struct BSG_Any value);
struct BSG_MethodFatPtr__List_add {
	struct BSG_Instance__List* this;
	BSG_Void (*method)(struct BSG_Instance__List* this,struct BSG_Any value);
};
BSG_Void BSG_Method__List_set(struct BSG_Instance__List* this,BSG_Int index,struct BSG_Any value);
struct BSG_MethodFatPtr__List_set {
	struct BSG_Instance__List* this;
	BSG_Void (*method)(struct BSG_Instance__List* this,BSG_Int index,struct BSG_Any value);
};
struct BSG_Any BSG_Method__List_get(struct BSG_Instance__List* this,BSG_Int index);
struct BSG_MethodFatPtr__List_get {
	struct BSG_Instance__List* this;
	struct BSG_Any (*method)( struct BSG_Instance__List* this,BSG_Int index);
};
BSG_Void BSG_Method__List_remove(struct BSG_Instance__List* this,BSG_Int index);
struct BSG_MethodFatPtr__List_remove {
	struct BSG_Instance__List* this;
	BSG_Void (*method)(struct BSG_Instance__List* this,BSG_Int index);
};
BSG_Void BSG_Method__List_clear(struct BSG_Instance__List* this);
struct BSG_MethodFatPtr__List_clear {
	struct BSG_Instance__List* this;
	BSG_Void (*method)(struct BSG_Instance__List* this);
};
BSG_Void BSG_Method__List_releaseAll(struct BSG_Instance__List* this);
struct BSG_MethodFatPtr__List_releaseAll {
	struct BSG_Instance__List* this;
	BSG_Void (*method)(struct BSG_Instance__List* this);
};
BSG_Void BSG_Method__List_deinit(struct BSG_Instance__List* this);
struct BSG_MethodFatPtr__List_deinit {
	struct BSG_Instance__List* this;
	BSG_Void (*method)(struct BSG_Instance__List* this);
};
extern struct BSG_Class__List BSG_ClassSingleton__List_List;
extern struct BSG_Instance__List* BSG_Constructor__List();
#endif
