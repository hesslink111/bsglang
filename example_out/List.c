#include "List.h"
const BSG_AnyType BSG_Type__List = 0l;
struct BSG_AnyInstance* BSG_BaseMethod__List_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type) {
	struct BSG_BaseInstance__List* b = (struct BSG_BaseInstance__List*)base;
	switch(type) {
	case BSG_Type__List:
		return (struct BSG_AnyInstance*)&b->List;
	}
	return NULL;
}
void BSG_BaseMethod__List_retain(struct BSG_AnyBaseInstance* base) {
	base->refCount++;
}
void BSG_BaseMethod__List_release(struct BSG_AnyBaseInstance* base) {
	base->refCount--;
	if(base->refCount <= 0) {
		struct BSG_BaseInstance__List* b = (struct BSG_BaseInstance__List*)base;
		base->refCount += 2;
		struct BSG_Instance__List* this = &b->List;
		this->class->deinit(this);
		base->refCount--;
		free(base);
	}
}
struct BSG_BaseClass BSG_BaseClassSingleton__List = {
	.cast = &BSG_BaseMethod__List_cast,
	.retain = &BSG_BaseMethod__List_retain,
	.release = &BSG_BaseMethod__List_release,
};
BSG_Void BSG_Method__List_init(struct BSG_Instance__List* this) {
	this->arr = malloc(10 * sizeof(struct BSG_Any));
	BSG_Int* __0 = &this->capacity;
	BSG_Int __1 = 10;
	*__0 = __1;
	BSG_Int* __2 = &this->size;
	BSG_Int __3 = 0;
	*__2 = __3;
	if(this) {
		this->baseInstance->baseClass->release(this->baseInstance);
	}
	return;
}
BSG_Void BSG_Method__List_add(struct BSG_Instance__List* this,struct BSG_Any value) {
	BSG_Int __4 = this->size;
	BSG_Int __5 = 1;
	BSG_Int __6 = __4 + __5;
	BSG_Int __7 = this->capacity;
	BSG_Int __8 = __6 > __7;
	if(__8) {
		BSG_Int newCapacity;
		BSG_Int* __9;
		__9 = &newCapacity;
		BSG_Int __10 = this->capacity;
		BSG_Int __11 = this->capacity;
		BSG_Int __12 = 2;
		BSG_Int __13 = __11 / __12;
		BSG_Int __14 = __10 + __13;
		*__9 = __14;
		this->arr = realloc(this->arr, newCapacity * sizeof(struct BSG_Any));
		BSG_Int* __15;
		__15 = &this->capacity;
		*__15 = newCapacity;
	}
	((struct BSG_Any*) this->arr)[this->size] = value;
	if(!value.isPrimitive && value.instanceOrPrimitive.instance) {
		value.instanceOrPrimitive.instance->baseInstance->baseClass->retain(value.instanceOrPrimitive.instance->baseInstance);
	}
	BSG_Int* __16;
	__16 = &this->size;
	BSG_Int __17 = this->size;
	BSG_Int __18 = 1;
	BSG_Int __19 = __17 + __18;
	*__16 = __19;
	if(this) {
		this->baseInstance->baseClass->release(this->baseInstance);
	}
	if(!value.isPrimitive && value.instanceOrPrimitive.instance) {
		value.instanceOrPrimitive.instance->baseInstance->baseClass->release(value.instanceOrPrimitive.instance->baseInstance);
	}
	return;
}
BSG_Void BSG_Method__List_set(struct BSG_Instance__List* this,BSG_Int index,struct BSG_Any value) {
	if(!value.isPrimitive && value.instanceOrPrimitive.instance) {
		value.instanceOrPrimitive.instance->baseInstance->baseClass->retain(value.instanceOrPrimitive.instance->baseInstance);
	}
	struct BSG_Any prevVal = ((struct BSG_Any*)this->arr)[index];
	if(!prevVal.isPrimitive && prevVal.instanceOrPrimitive.instance) {
		prevVal.instanceOrPrimitive.instance->baseInstance->baseClass->release(prevVal.instanceOrPrimitive.instance->baseInstance);
	}
	((struct BSG_Any*)this->arr)[index] = value;
	if(this) {
		this->baseInstance->baseClass->release(this->baseInstance);
	}
	if(!value.isPrimitive && value.instanceOrPrimitive.instance) {
		value.instanceOrPrimitive.instance->baseInstance->baseClass->release(value.instanceOrPrimitive.instance->baseInstance);
	}
	return;
}
struct BSG_Any BSG_Method__List_get(struct BSG_Instance__List* this,BSG_Int index) {
	struct BSG_Any value;
	value = ((struct BSG_Any*)this->arr)[index];
	if(!value.isPrimitive && value.instanceOrPrimitive.instance) {
		value.instanceOrPrimitive.instance->baseInstance->baseClass->retain(value.instanceOrPrimitive.instance->baseInstance);
	}
	if(this) {
		this->baseInstance->baseClass->release(this->baseInstance);
	}
	return value;
}
BSG_Void BSG_Method__List_remove(struct BSG_Instance__List* this,BSG_Int index) {
	struct BSG_Any value;
	value = ((struct BSG_Any*)this->arr)[index];
	if(!value.isPrimitive && value.instanceOrPrimitive.instance) {
		value.instanceOrPrimitive.instance->baseInstance->baseClass->release(value.instanceOrPrimitive.instance->baseInstance);
	}
	BSG_Int numToCopy;
	BSG_Int* __20;
	__20 = &numToCopy;
	BSG_Int __21 = this->size;
	BSG_Int __22 = __21 - index;
	*__20 = __22;
	memcpy(((struct BSG_Any*)this->arr) + index, ((struct BSG_Any*)this->arr) + index + 1, numToCopy * sizeof(struct BSG_Any));
	BSG_Int* __23;
	__23 = &this->size;
	BSG_Int __24 = this->size;
	BSG_Int __25 = 1;
	BSG_Int __26 = __24 - __25;
	*__23 = __26;
	if(this) {
		this->baseInstance->baseClass->release(this->baseInstance);
	}
	return;
}
BSG_Void BSG_Method__List_clear(struct BSG_Instance__List* this) {
	struct BSG_MethodFatPtr__List_deinit __27;
	__27.this = this;
	__27.method = this->class->deinit;
	__27.this->baseInstance->baseClass->retain(__27.this->baseInstance);
	__27.method(__27.this);
	struct BSG_MethodFatPtr__List_init __29;
	__29.this = this;
	__29.method = this->class->init;
	__29.this->baseInstance->baseClass->retain(__29.this->baseInstance);
	__29.method(__29.this);
	if(this) {
		this->baseInstance->baseClass->release(this->baseInstance);
	}
	return;
}
BSG_Void BSG_Method__List_releaseAll(struct BSG_Instance__List* this) {
	BSG_Int i;
	BSG_Int* __31;
	__31 = &i;
	BSG_Int __32 = 0;
	*__31 = __32;
	while(true) {
		BSG_Int __33 = this->size;
		BSG_Int __34 = i < __33;
		if(!__34) {
			break;
		}
		BSG_Int* __35;
		__35 = &i;
		BSG_Int __36 = 1;
		BSG_Int __37 = i + __36;
		*__35 = __37;
		struct BSG_Any value;
		value = ((struct BSG_Any*)this->arr)[i];
		if(!value.isPrimitive && value.instanceOrPrimitive.instance) {
			value.instanceOrPrimitive.instance->baseInstance->baseClass->release(value.instanceOrPrimitive.instance->baseInstance);
		}
	}
	if(this) {
		this->baseInstance->baseClass->release(this->baseInstance);
	}
	return;
}
BSG_Void BSG_Method__List_deinit(struct BSG_Instance__List* this) {
	struct BSG_MethodFatPtr__List_releaseAll __38;
	__38.this = this;
	__38.method = this->class->releaseAll;
	__38.this->baseInstance->baseClass->retain(__38.this->baseInstance);
	__38.method(__38.this);
	free(this->arr);
	if(this) {
		this->baseInstance->baseClass->release(this->baseInstance);
	}
	return;
}
struct BSG_Class__List BSG_ClassSingleton__List_List = {
	.init = &BSG_Method__List_init,
	.add = &BSG_Method__List_add,
	.set = &BSG_Method__List_set,
	.get = &BSG_Method__List_get,
	.remove = &BSG_Method__List_remove,
	.clear = &BSG_Method__List_clear,
	.releaseAll = &BSG_Method__List_releaseAll,
	.deinit = &BSG_Method__List_deinit,
};
struct BSG_Instance__List* BSG_Constructor__List() {
	struct BSG_BaseInstance__List* baseInstance = malloc(sizeof(struct BSG_BaseInstance__List));
	baseInstance->List = (struct BSG_Instance__List) {
		.baseInstance = (struct BSG_AnyBaseInstance*)baseInstance,
		.class = &BSG_ClassSingleton__List_List,
	};
	baseInstance->baseClass = (struct BSG_AnyBaseClass*) &BSG_BaseClassSingleton__List;
	baseInstance->refCount += 2;
	struct BSG_Instance__List* this = &baseInstance->List;
	this->class->init(this);
	baseInstance->refCount--;
	return &baseInstance->List;
}
