// Includes
#include "HelloWorld.h"

// Base Methods
struct BSG_AnyInstance* BSG_BaseMethod__HelloWorld_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type) {
    struct BSG_BaseInstance__HelloWorld* b = (struct BSG_BaseInstance__HelloWorld*)base;
    switch(type) {
        case BSG_Type__HelloWorld:
            return (struct BSG_AnyInstance*)&b->HelloWorld;
    }
    return NULL;
}
BSG_Bool BSG_BaseMethod__HelloWorld_canCast(struct BSG_AnyBaseInstance* base, BSG_AnyType type) {
    struct BSG_BaseInstance__HelloWorld* b = (struct BSG_BaseInstance__HelloWorld*)base;
    switch(type) {
        case BSG_Type__HelloWorld:
            return true;
    }
    return false;
}
void BSG_BaseMethod__HelloWorld_retain(struct BSG_AnyBaseInstance* base) {
    base->refCount++;
}
void BSG_BaseMethod__HelloWorld_release(struct BSG_AnyBaseInstance* base) {
    base->refCount--;
    if(base->refCount <= 0) {
        struct BSG_BaseInstance__HelloWorld* b = (struct BSG_BaseInstance__HelloWorld*)base;
        free(base);
    }
}

// Base Class Singleton
struct BSG_BaseClass BSG_BaseClassSingleton__HelloWorld = {
    .cast = &BSG_BaseMethod__HelloWorld_cast,
    .canCast = &BSG_BaseMethod__HelloWorld_canCast,
    .retain = &BSG_BaseMethod__HelloWorld_retain,
    .release = &BSG_BaseMethod__HelloWorld_release,
};

// Methods
BSG_Void BSG_Method__HelloWorld·main(BSG_AnyInstancePtr _tmp_0) {
    BSG_InstancePtr__HelloWorld this = (struct BSG_Instance__HelloWorld*)_tmp_0;
    BSG_InstancePtr__IO _tmp_1;
    _tmp_1 = IO;
    if(_tmp_1) {
        _tmp_1->baseInstance->baseClass->retain(_tmp_1->baseInstance);
    }
    BSG_MethodFatPtr__｢BSG_InstancePtr__String｣￫BSG_Void _tmp_2;
    _tmp_2.this = (BSG_AnyInstancePtr) _tmp_1;
    _tmp_2.method = _tmp_1->class->println;
    BSG_InstancePtr__String _tmp_3 = BSG_Constructor__String();
    _tmp_3->baseInstance->baseClass->retain(_tmp_3->baseInstance);
    _tmp_3->cStr = "Hello, world!";
    _tmp_3->length = 13;
    _tmp_3->isLiteral = true;
    _tmp_2.this->baseInstance->baseClass->retain(_tmp_2.this->baseInstance);
    if(_tmp_3) {
        _tmp_3->baseInstance->baseClass->retain(_tmp_3->baseInstance);
    }
    _tmp_2.method(_tmp_2.this,_tmp_3);
    if(this) {
        this->baseInstance->baseClass->release(this->baseInstance);
    }
    if(_tmp_1) {
        _tmp_1->baseInstance->baseClass->release(_tmp_1->baseInstance);
    }
    if(_tmp_3) {
        _tmp_3->baseInstance->baseClass->release(_tmp_3->baseInstance);
    }
    return;
}

// Class Singletons
struct BSG_Class__HelloWorld BSG_ClassSingleton__HelloWorld_HelloWorld = {
    .main = &BSG_Method__HelloWorld·main,
};

// Constructor
struct BSG_Instance__HelloWorld* BSG_Constructor__HelloWorld() {
    struct BSG_BaseInstance__HelloWorld* baseInstance = malloc(sizeof(struct BSG_BaseInstance__HelloWorld));
    baseInstance->HelloWorld = (struct BSG_Instance__HelloWorld) {
        .baseInstance = (struct BSG_AnyBaseInstance*)baseInstance,
        .class = &BSG_ClassSingleton__HelloWorld_HelloWorld,
    };
    baseInstance->baseClass = (struct BSG_AnyBaseClass*) &BSG_BaseClassSingleton__HelloWorld;
    return &baseInstance->HelloWorld;
}

// Singleton
struct BSG_Instance__HelloWorld* HelloWorld = NULL;
