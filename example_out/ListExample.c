#include "ListExample.h"
struct BSG_AnyInstance* BSG_BaseMethod__ListExample_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type) {
	struct BSG_BaseInstance__ListExample* b = (struct BSG_BaseInstance__ListExample*)base;
	switch(type) {
	case BSG_Type__ListExample:
		return (struct BSG_AnyInstance*)&b->ListExample;
	}
	return NULL;
}
void BSG_BaseMethod__ListExample_retain(struct BSG_AnyBaseInstance* base) {
	base->refCount++;
}
void BSG_BaseMethod__ListExample_release(struct BSG_AnyBaseInstance* base) {
	base->refCount--;
	if(base->refCount <= 0) {
		struct BSG_BaseInstance__ListExample* b = (struct BSG_BaseInstance__ListExample*)base;
		free(base);
	}
}
struct BSG_BaseClass BSG_BaseClassSingleton__ListExample = {
	.cast = &BSG_BaseMethod__ListExample_cast,
	.retain = &BSG_BaseMethod__ListExample_retain,
	.release = &BSG_BaseMethod__ListExample_release,
};
BSG_Void BSG_Method__ListExample_ListExample_main(struct BSG_Instance__ListExample* this) {
	struct BSG_Instance__List* l;
	struct BSG_Instance__List** __0;
	__0 = &l;
	struct BSG_Instance__List* __1 = BSG_Constructor__List();
	__1->baseInstance->baseClass->retain(__1->baseInstance);
	*__0 = __1;
	struct BSG_Instance__IO* __2;
	__2 = IO;
	if(__2) {
		__2->baseInstance->baseClass->retain(__2->baseInstance);
	}
	struct BSG_MethodFatPtr__IO_println __3;
	__3.this = __2;
	__3.method = __2->class->println;
	struct BSG_Instance__String* __4 = BSG_Constructor__String();
	__4->baseInstance->baseClass->retain(__4->baseInstance);
	char* __5 = "Add two cats:";
	__4->cStr = (BSG_Opaque) malloc(14 * sizeof(char));
	strcpy((char*)__4->cStr, __5);
	__3.this->baseInstance->baseClass->retain(__3.this->baseInstance);
	if(__4) {
		__4->baseInstance->baseClass->retain(__4->baseInstance);
	}
	__3.method(__3.this,__4);
	BSG_Int i;
	BSG_Int* __7;
	__7 = &i;
	BSG_Int __8 = 0;
	*__7 = __8;
	while(true) {
		BSG_Int __9 = 2;
		BSG_Int __10 = i < __9;
		if(!__10) {
			break;
		}
		struct BSG_MethodFatPtr__List_add __11;
		__11.this = l;
		__11.method = l->class->add;
		struct BSG_Instance__Cat* __12 = BSG_Constructor__Cat();
		__12->baseInstance->baseClass->retain(__12->baseInstance);
		struct BSG_Any __13;
		__13.type = BSG_Any_ContentType__Instance;
		__13.content.instance = (struct BSG_AnyInstance*) __12;
		__11.this->baseInstance->baseClass->retain(__11.this->baseInstance);
		if(__13.type == BSG_Any_ContentType__Instance && __13.content.instance) {
			__13.content.instance->baseInstance->baseClass->retain(__13.content.instance->baseInstance);
		} else if(__13.type == BSG_Any_ContentType__Method && __13.content.method.this) {
			__13.content.method.this->baseInstance->baseClass->retain(__13.content.method.this->baseInstance);
		}
		__11.method(__11.this,__13);
		BSG_Int* __15;
		__15 = &i;
		BSG_Int __16 = 1;
		BSG_Int __17 = i + __16;
		*__15 = __17;
		if(__12) {
			__12->baseInstance->baseClass->release(__12->baseInstance);
		}
	}
	struct BSG_Instance__IO* __18;
	__18 = IO;
	if(__18) {
		__18->baseInstance->baseClass->retain(__18->baseInstance);
	}
	struct BSG_MethodFatPtr__IO_println __19;
	__19.this = __18;
	__19.method = __18->class->println;
	struct BSG_Instance__String* __20 = BSG_Constructor__String();
	__20->baseInstance->baseClass->retain(__20->baseInstance);
	char* __21 = "Replace first cat with dog:";
	__20->cStr = (BSG_Opaque) malloc(28 * sizeof(char));
	strcpy((char*)__20->cStr, __21);
	__19.this->baseInstance->baseClass->retain(__19.this->baseInstance);
	if(__20) {
		__20->baseInstance->baseClass->retain(__20->baseInstance);
	}
	__19.method(__19.this,__20);
	struct BSG_MethodFatPtr__List_set __23;
	__23.this = l;
	__23.method = l->class->set;
	BSG_Int __24 = 0;
	struct BSG_Instance__Dog* __25 = BSG_Constructor__Dog();
	__25->baseInstance->baseClass->retain(__25->baseInstance);
	struct BSG_Any __26;
	__26.type = BSG_Any_ContentType__Instance;
	__26.content.instance = (struct BSG_AnyInstance*) __25;
	__23.this->baseInstance->baseClass->retain(__23.this->baseInstance);
	if(__26.type == BSG_Any_ContentType__Instance && __26.content.instance) {
		__26.content.instance->baseInstance->baseClass->retain(__26.content.instance->baseInstance);
	} else if(__26.type == BSG_Any_ContentType__Method && __26.content.method.this) {
		__26.content.method.this->baseInstance->baseClass->retain(__26.content.method.this->baseInstance);
	}
	__23.method(__23.this,__24,__26);
	struct BSG_Instance__IO* __28;
	__28 = IO;
	if(__28) {
		__28->baseInstance->baseClass->retain(__28->baseInstance);
	}
	struct BSG_MethodFatPtr__IO_println __29;
	__29.this = __28;
	__29.method = __28->class->println;
	struct BSG_Instance__String* __30 = BSG_Constructor__String();
	__30->baseInstance->baseClass->retain(__30->baseInstance);
	char* __31 = "List with Cats and dogs:";
	__30->cStr = (BSG_Opaque) malloc(25 * sizeof(char));
	strcpy((char*)__30->cStr, __31);
	__29.this->baseInstance->baseClass->retain(__29.this->baseInstance);
	if(__30) {
		__30->baseInstance->baseClass->retain(__30->baseInstance);
	}
	__29.method(__29.this,__30);
	BSG_Int* __33;
	__33 = &i;
	BSG_Int __34 = 0;
	*__33 = __34;
	while(true) {
		BSG_Int __35 = 2;
		BSG_Int __36 = i < __35;
		if(!__36) {
			break;
		}
		struct BSG_Instance__IO* __37;
		__37 = IO;
		if(__37) {
			__37->baseInstance->baseClass->retain(__37->baseInstance);
		}
		struct BSG_MethodFatPtr__IO_println __38;
		__38.this = __37;
		__38.method = __37->class->println;
		struct BSG_MethodFatPtr__List_get __39;
		__39.this = l;
		__39.method = l->class->get;
		__39.this->baseInstance->baseClass->retain(__39.this->baseInstance);
		struct BSG_Any __40 = __39.method(__39.this,i);
		struct BSG_Instance__Animal* __41 = (struct BSG_Instance__Animal*) __40.content.instance->baseInstance->baseClass->cast(__40.content.instance->baseInstance, BSG_Type__Animal);
		struct BSG_MethodFatPtr__Animal_talk __42;
		__42.this = __41;
		__42.method = __41->class->talk;
		__42.this->baseInstance->baseClass->retain(__42.this->baseInstance);
		struct BSG_Instance__String* __43 = __42.method(__42.this);
		__38.this->baseInstance->baseClass->retain(__38.this->baseInstance);
		if(__43) {
			__43->baseInstance->baseClass->retain(__43->baseInstance);
		}
		__38.method(__38.this,__43);
		BSG_Int* __45;
		__45 = &i;
		BSG_Int __46 = 1;
		BSG_Int __47 = i + __46;
		*__45 = __47;
		if(__37) {
			__37->baseInstance->baseClass->release(__37->baseInstance);
		}
		if(__40.type == BSG_Any_ContentType__Instance && __40.content.instance) {
			__40.content.instance->baseInstance->baseClass->release(__40.content.instance->baseInstance);
		} else if(__40.type == BSG_Any_ContentType__Method && __40.content.method.this) {
			__40.content.method.this->baseInstance->baseClass->release(__40.content.method.this->baseInstance);
		}
		if(__43) {
			__43->baseInstance->baseClass->release(__43->baseInstance);
		}
	}
	struct BSG_Instance__IO* __48;
	__48 = IO;
	if(__48) {
		__48->baseInstance->baseClass->retain(__48->baseInstance);
	}
	struct BSG_MethodFatPtr__IO_println __49;
	__49.this = __48;
	__49.method = __48->class->println;
	struct BSG_Instance__String* __50 = BSG_Constructor__String();
	__50->baseInstance->baseClass->retain(__50->baseInstance);
	char* __51 = "Remove the first animal (dog):";
	__50->cStr = (BSG_Opaque) malloc(31 * sizeof(char));
	strcpy((char*)__50->cStr, __51);
	__49.this->baseInstance->baseClass->retain(__49.this->baseInstance);
	if(__50) {
		__50->baseInstance->baseClass->retain(__50->baseInstance);
	}
	__49.method(__49.this,__50);
	struct BSG_MethodFatPtr__List_remove __53;
	__53.this = l;
	__53.method = l->class->remove;
	BSG_Int __54 = 0;
	__53.this->baseInstance->baseClass->retain(__53.this->baseInstance);
	__53.method(__53.this,__54);
	struct BSG_Instance__IO* __56;
	__56 = IO;
	if(__56) {
		__56->baseInstance->baseClass->retain(__56->baseInstance);
	}
	struct BSG_MethodFatPtr__IO_println __57;
	__57.this = __56;
	__57.method = __56->class->println;
	struct BSG_Instance__String* __58 = BSG_Constructor__String();
	__58->baseInstance->baseClass->retain(__58->baseInstance);
	char* __59 = "List with just 1 cat:";
	__58->cStr = (BSG_Opaque) malloc(22 * sizeof(char));
	strcpy((char*)__58->cStr, __59);
	__57.this->baseInstance->baseClass->retain(__57.this->baseInstance);
	if(__58) {
		__58->baseInstance->baseClass->retain(__58->baseInstance);
	}
	__57.method(__57.this,__58);
	BSG_Int* __61;
	__61 = &i;
	BSG_Int __62 = 0;
	*__61 = __62;
	while(true) {
		BSG_Int __63 = 1;
		BSG_Int __64 = i < __63;
		if(!__64) {
			break;
		}
		struct BSG_Instance__IO* __65;
		__65 = IO;
		if(__65) {
			__65->baseInstance->baseClass->retain(__65->baseInstance);
		}
		struct BSG_MethodFatPtr__IO_println __66;
		__66.this = __65;
		__66.method = __65->class->println;
		struct BSG_MethodFatPtr__List_get __67;
		__67.this = l;
		__67.method = l->class->get;
		__67.this->baseInstance->baseClass->retain(__67.this->baseInstance);
		struct BSG_Any __68 = __67.method(__67.this,i);
		struct BSG_Instance__Animal* __69 = (struct BSG_Instance__Animal*) __68.content.instance->baseInstance->baseClass->cast(__68.content.instance->baseInstance, BSG_Type__Animal);
		struct BSG_MethodFatPtr__Animal_talk __70;
		__70.this = __69;
		__70.method = __69->class->talk;
		__70.this->baseInstance->baseClass->retain(__70.this->baseInstance);
		struct BSG_Instance__String* __71 = __70.method(__70.this);
		__66.this->baseInstance->baseClass->retain(__66.this->baseInstance);
		if(__71) {
			__71->baseInstance->baseClass->retain(__71->baseInstance);
		}
		__66.method(__66.this,__71);
		BSG_Int* __73;
		__73 = &i;
		BSG_Int __74 = 1;
		BSG_Int __75 = i + __74;
		*__73 = __75;
		if(__65) {
			__65->baseInstance->baseClass->release(__65->baseInstance);
		}
		if(__68.type == BSG_Any_ContentType__Instance && __68.content.instance) {
			__68.content.instance->baseInstance->baseClass->release(__68.content.instance->baseInstance);
		} else if(__68.type == BSG_Any_ContentType__Method && __68.content.method.this) {
			__68.content.method.this->baseInstance->baseClass->release(__68.content.method.this->baseInstance);
		}
		if(__71) {
			__71->baseInstance->baseClass->release(__71->baseInstance);
		}
	}
	if(this) {
		this->baseInstance->baseClass->release(this->baseInstance);
	}
	if(__1) {
		__1->baseInstance->baseClass->release(__1->baseInstance);
	}
	if(__2) {
		__2->baseInstance->baseClass->release(__2->baseInstance);
	}
	if(__4) {
		__4->baseInstance->baseClass->release(__4->baseInstance);
	}
	if(__18) {
		__18->baseInstance->baseClass->release(__18->baseInstance);
	}
	if(__20) {
		__20->baseInstance->baseClass->release(__20->baseInstance);
	}
	if(__25) {
		__25->baseInstance->baseClass->release(__25->baseInstance);
	}
	if(__28) {
		__28->baseInstance->baseClass->release(__28->baseInstance);
	}
	if(__30) {
		__30->baseInstance->baseClass->release(__30->baseInstance);
	}
	if(__48) {
		__48->baseInstance->baseClass->release(__48->baseInstance);
	}
	if(__50) {
		__50->baseInstance->baseClass->release(__50->baseInstance);
	}
	if(__56) {
		__56->baseInstance->baseClass->release(__56->baseInstance);
	}
	if(__58) {
		__58->baseInstance->baseClass->release(__58->baseInstance);
	}
	return;
}
struct BSG_Class__ListExample BSG_ClassSingleton__ListExample_ListExample = {
	.main = &BSG_Method__ListExample_ListExample_main,
};
struct BSG_Instance__ListExample* BSG_Constructor__ListExample() {
	struct BSG_BaseInstance__ListExample* baseInstance = malloc(sizeof(struct BSG_BaseInstance__ListExample));
	baseInstance->ListExample = (struct BSG_Instance__ListExample) {
		.baseInstance = (struct BSG_AnyBaseInstance*)baseInstance,
		.class = &BSG_ClassSingleton__ListExample_ListExample,
	};
	baseInstance->baseClass = (struct BSG_AnyBaseClass*) &BSG_BaseClassSingleton__ListExample;
	return &baseInstance->ListExample;
}
struct BSG_Instance__ListExample* ListExample = NULL;
