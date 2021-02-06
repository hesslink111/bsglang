#include "MainClass.h"
const BSG_AnyType BSG_Type__MainClass = 0l;
struct BSG_AnyInstance* BSG_BaseMethod__MainClass_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type) {
	struct BSG_BaseInstance__MainClass* b = (struct BSG_BaseInstance__MainClass*)base;
	switch(type) {
	case BSG_Type__MainClass:
		return (struct BSG_AnyInstance*)&b->MainClass;
	}
	return NULL;
}
void BSG_BaseMethod__MainClass_retain(struct BSG_AnyBaseInstance* base) {
	base->refCount++;
}
void BSG_BaseMethod__MainClass_release(struct BSG_AnyBaseInstance* base) {
	base->refCount--;
	if(base->refCount <= 0) {
		struct BSG_BaseInstance__MainClass* b = (struct BSG_BaseInstance__MainClass*)base;
		free(base);
	}
}
struct BSG_BaseClass BSG_BaseClassSingleton__MainClass = {
	.cast = &BSG_BaseMethod__MainClass_cast,
	.retain = &BSG_BaseMethod__MainClass_retain,
	.release = &BSG_BaseMethod__MainClass_release,
};
BSG_Void BSG_Method__MainClass_main(struct BSG_Instance__MainClass* this) {
	struct BSG_MethodFatPtr__IO_println __0;
	__0.this = IO;
	__0.method = IO->class->println;
	if(__0.this) {
		__0.this->baseInstance->baseClass->retain(__0.this->baseInstance);
	}
	struct BSG_Instance__String* __1 = BSG_Constructor__String();
	__1->baseInstance->baseClass->retain(__1->baseInstance);
	char* __2 = "Hello, world!";
	__1->cStr = (BSG_Opaque) malloc(14 * sizeof(char));
	strcpy((char*)__1->cStr, __2);
	__0.this->baseInstance->baseClass->retain(__0.this->baseInstance);
	if(__1) {
		__1->baseInstance->baseClass->retain(__1->baseInstance);
	}
	__0.method(__0.this,__1);
	struct BSG_Instance__List* l;
	struct BSG_Instance__List** __4;
	__4 = &l;
	struct BSG_Instance__List* __5 = BSG_Constructor__List();
	__5->baseInstance->baseClass->retain(__5->baseInstance);
	*__4 = __5;
	BSG_Int i;
	BSG_Int* __6;
	__6 = &i;
	BSG_Int __7 = 0;
	*__6 = __7;
	while(true) {
		BSG_Int __8 = 50;
		BSG_Int __9 = i < __8;
		if(!__9) {
			break;
		}
		struct BSG_MethodFatPtr__List_add __10;
		__10.this = l;
		__10.method = l->class->add;
		if(__10.this) {
			__10.this->baseInstance->baseClass->retain(__10.this->baseInstance);
		}
		struct BSG_Any __11;
		__11.isPrimitive = 1;
		__11.instanceOrPrimitive.primitive.IntValue = i;
		__10.this->baseInstance->baseClass->retain(__10.this->baseInstance);
		if(!__11.isPrimitive && __11.instanceOrPrimitive.instance) {
			__11.instanceOrPrimitive.instance->baseInstance->baseClass->retain(__11.instanceOrPrimitive.instance->baseInstance);
		}
		__10.method(__10.this,__11);
		BSG_Int* __13;
		__13 = &i;
		BSG_Int __14 = 1;
		BSG_Int __15 = i + __14;
		*__13 = __15;
		if(__10.this) {
			__10.this->baseInstance->baseClass->release(__10.this->baseInstance);
		}
	}
	struct BSG_MethodFatPtr__List_remove __16;
	__16.this = l;
	__16.method = l->class->remove;
	if(__16.this) {
		__16.this->baseInstance->baseClass->retain(__16.this->baseInstance);
	}
	BSG_Int __17 = 4;
	__16.this->baseInstance->baseClass->retain(__16.this->baseInstance);
	__16.method(__16.this,__17);
	BSG_Int* __19;
	__19 = &i;
	BSG_Int __20 = 0;
	*__19 = __20;
	while(true) {
		BSG_Int __21 = 49;
		BSG_Int __22 = i < __21;
		if(!__22) {
			break;
		}
		struct BSG_MethodFatPtr__IO_println __23;
		__23.this = IO;
		__23.method = IO->class->println;
		if(__23.this) {
			__23.this->baseInstance->baseClass->retain(__23.this->baseInstance);
		}
		struct BSG_MethodFatPtr__StringExtensions_intToString __24;
		__24.this = StringExtensions;
		__24.method = StringExtensions->class->intToString;
		if(__24.this) {
			__24.this->baseInstance->baseClass->retain(__24.this->baseInstance);
		}
		struct BSG_MethodFatPtr__List_get __25;
		__25.this = l;
		__25.method = l->class->get;
		if(__25.this) {
			__25.this->baseInstance->baseClass->retain(__25.this->baseInstance);
		}
		__25.this->baseInstance->baseClass->retain(__25.this->baseInstance);
		struct BSG_Any __26 = __25.method(__25.this,i);
		BSG_Int __27 = __26.instanceOrPrimitive.primitive.IntValue;
		__24.this->baseInstance->baseClass->retain(__24.this->baseInstance);
		struct BSG_Instance__String* __28 = __24.method(__24.this,__27);
		__23.this->baseInstance->baseClass->retain(__23.this->baseInstance);
		if(__28) {
			__28->baseInstance->baseClass->retain(__28->baseInstance);
		}
		__23.method(__23.this,__28);
		BSG_Int* __30;
		__30 = &i;
		BSG_Int __31 = 1;
		BSG_Int __32 = i + __31;
		*__30 = __32;
		if(__23.this) {
			__23.this->baseInstance->baseClass->release(__23.this->baseInstance);
		}
		if(__24.this) {
			__24.this->baseInstance->baseClass->release(__24.this->baseInstance);
		}
		if(__25.this) {
			__25.this->baseInstance->baseClass->release(__25.this->baseInstance);
		}
		if(!__26.isPrimitive && __26.instanceOrPrimitive.instance) {
			__26.instanceOrPrimitive.instance->baseInstance->baseClass->release(__26.instanceOrPrimitive.instance->baseInstance);
		}
		if(__28) {
			__28->baseInstance->baseClass->release(__28->baseInstance);
		}
	}
	if(this) {
		this->baseInstance->baseClass->release(this->baseInstance);
	}
	if(__0.this) {
		__0.this->baseInstance->baseClass->release(__0.this->baseInstance);
	}
	if(__1) {
		__1->baseInstance->baseClass->release(__1->baseInstance);
	}
	if(__5) {
		__5->baseInstance->baseClass->release(__5->baseInstance);
	}
	if(__16.this) {
		__16.this->baseInstance->baseClass->release(__16.this->baseInstance);
	}
	return;
}
struct BSG_Class__MainClass BSG_ClassSingleton__MainClass_MainClass = {
	.main = &BSG_Method__MainClass_main,
};
struct BSG_Instance__MainClass* BSG_Constructor__MainClass() {
	struct BSG_BaseInstance__MainClass* baseInstance = malloc(sizeof(struct BSG_BaseInstance__MainClass));
	baseInstance->MainClass = (struct BSG_Instance__MainClass) {
		.baseInstance = (struct BSG_AnyBaseInstance*)baseInstance,
		.class = &BSG_ClassSingleton__MainClass_MainClass,
	};
	baseInstance->baseClass = (struct BSG_AnyBaseClass*) &BSG_BaseClassSingleton__MainClass;
	return &baseInstance->MainClass;
}
struct BSG_Instance__MainClass* MainClass = NULL;
