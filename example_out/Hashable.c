// Includes
#include "Hashable.h"

// Base Methods
struct BSG_AnyInstance* BSG_BaseMethod__Hashable_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type) {
    struct BSG_BaseInstance__Hashable* b = (struct BSG_BaseInstance__Hashable*)base;
    switch(type) {
        case BSG_Type__Hashable:
            return (struct BSG_AnyInstance*)&b->Hashable;
        case BSG_Type__Equatable:
            return (struct BSG_AnyInstance*)&b->Equatable;
    }
    return NULL;
}
BSG_Bool BSG_BaseMethod__Hashable_canCast(struct BSG_AnyBaseInstance* base, BSG_AnyType type) {
    struct BSG_BaseInstance__Hashable* b = (struct BSG_BaseInstance__Hashable*)base;
    switch(type) {
        case BSG_Type__Hashable:
            return true;
        case BSG_Type__Equatable:
            return true;
    }
    return false;
}
void BSG_BaseMethod__Hashable_retain(struct BSG_AnyBaseInstance* base) {
    base->refCount++;
}
void BSG_BaseMethod__Hashable_release(struct BSG_AnyBaseInstance* base) {
    base->refCount--;
    if(base->refCount <= 0) {
        struct BSG_BaseInstance__Hashable* b = (struct BSG_BaseInstance__Hashable*)base;
        free(base);
    }
}

// Base Class Singleton
struct BSG_BaseClass BSG_BaseClassSingleton__Hashable = {
    .cast = &BSG_BaseMethod__Hashable_cast,
    .canCast = &BSG_BaseMethod__Hashable_canCast,
    .retain = &BSG_BaseMethod__Hashable_retain,
    .release = &BSG_BaseMethod__Hashable_release,
};

// Methods
BSG_Int BSG_Method__Hashable·hashCode(BSG_AnyInstancePtr _tmp_0) {
    BSG_InstancePtr__Hashable this = (struct BSG_Instance__Hashable*)_tmp_0;
    BSG_Int _tmp_1 = 0;
    if(this) {
        this->baseInstance->baseClass->release(this->baseInstance);
    }
    return _tmp_1;
}
BSG_Bool BSG_Method__Hashable·equals(BSG_AnyInstancePtr _tmp_2,BSG_InstancePtr__Equatable other) {
    BSG_InstancePtr__Hashable this = (BSG_InstancePtr__Hashable)_tmp_2->baseInstance->baseClass->cast(_tmp_2->baseInstance, BSG_Type__Hashable);
    BSG_Bool _tmp_3 = true;
    if(this) {
        this->baseInstance->baseClass->release(this->baseInstance);
    }
    if(other) {
        other->baseInstance->baseClass->release(other->baseInstance);
    }
    return _tmp_3;
}

// Class Singletons
struct BSG_Class__Hashable BSG_ClassSingleton__Hashable_Hashable = {
    .hashCode = &BSG_Method__Hashable·hashCode,
};
struct BSG_Class__Equatable BSG_ClassSingleton__Hashable_Equatable = {
    .equals = &BSG_Method__Hashable·equals,
};

// Constructor
struct BSG_Instance__Hashable* BSG_Constructor__Hashable() {
    struct BSG_BaseInstance__Hashable* baseInstance = malloc(sizeof(struct BSG_BaseInstance__Hashable));
    baseInstance->Hashable = (struct BSG_Instance__Hashable) {
        .baseInstance = (struct BSG_AnyBaseInstance*)baseInstance,
        .class = &BSG_ClassSingleton__Hashable_Hashable,
    };
    baseInstance->Equatable = (struct BSG_Instance__Equatable) {
        .baseInstance = (struct BSG_AnyBaseInstance*)baseInstance,
        .class = &BSG_ClassSingleton__Hashable_Equatable,
    };
    baseInstance->baseClass = (struct BSG_AnyBaseClass*) &BSG_BaseClassSingleton__Hashable;
    return &baseInstance->Hashable;
}

// Singleton
