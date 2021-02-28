// Includes
#include "IO.h"

// Base Methods
BSG_AnyInstance* BSG_BaseMethod__IO_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type) {
    struct BSG_BaseInstance__IO* b = (struct BSG_BaseInstance__IO*)base;
    switch(type) {
        case BSG_Type__IO:
            return (BSG_AnyInstance*)&b->IO;
    }
    return NULL;
}
BSG_Bool BSG_BaseMethod__IO_canCast(struct BSG_AnyBaseInstance* base, BSG_AnyType type) {
    struct BSG_BaseInstance__IO* b = (struct BSG_BaseInstance__IO*)base;
    switch(type) {
        case BSG_Type__IO:
            return true;
    }
    return false;
}
void BSG_BaseMethod__IO_retain(struct BSG_AnyBaseInstance* base) {
    base->refCount++;
}
void BSG_BaseMethod__IO_release(struct BSG_AnyBaseInstance* base) {
    base->refCount--;
    if(base->refCount <= 0) {
        struct BSG_BaseInstance__IO* b = (struct BSG_BaseInstance__IO*)base;
        deallocate(base);
    }
}

// Base Class Singleton
struct BSG_BaseClass BSG_BaseClassSingleton__IO = {
    .cast = &BSG_BaseMethod__IO_cast,
    .canCast = &BSG_BaseMethod__IO_canCast,
    .retain = &BSG_BaseMethod__IO_retain,
    .release = &BSG_BaseMethod__IO_release,
};

// Methods
BSG_Void BSG_Method__IO·println(BSG_AnyInstance* _tmp_0,BSG_InstancePtr__String message) {
    BSG_InstancePtr__IO this = (struct BSG_Instance__IO*)_tmp_0;
    puts(message->cStr);
    if(this) {
        this->baseInstance->baseClass->release(this->baseInstance);
    }
    if(message) {
        message->baseInstance->baseClass->release(message->baseInstance);
    }
    return;
}

// Class Singletons
struct BSG_Class__IO BSG_ClassSingleton__IO_IO = {
    .println = &BSG_Method__IO·println,
};

// Constructor
struct BSG_Instance__IO* BSG_Constructor__IO() {
    struct BSG_BaseInstance__IO* baseInstance = allocate(sizeof(struct BSG_BaseInstance__IO));
    baseInstance->refCount = 0;
    baseInstance->IO = (struct BSG_Instance__IO) {
        .baseInstance = (struct BSG_AnyBaseInstance*)baseInstance,
        .class = &BSG_ClassSingleton__IO_IO,
    };
    baseInstance->baseClass = (struct BSG_AnyBaseClass*) &BSG_BaseClassSingleton__IO;
    return &baseInstance->IO;
}

// Singleton
struct BSG_Instance__IO* IO = NULL;
