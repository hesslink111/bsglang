#include "bsg_preamble.h"
#include <stdio.h>

// Boxed Method
void BSG_BaseMethod__BoxedMethod_retain(struct BSG_AnyBaseInstance* base) {
    base->refCount++;
}
void BSG_BaseMethod__BoxedMethod_release(struct BSG_AnyBaseInstance* base) {
    base->refCount--;
    if(base->refCount <= 0) {
        BSG_BaseInstance__BoxedMethod* b = (BSG_BaseInstance__BoxedMethod*)base;
        b->BoxedMethod.method.this->baseInstance->baseClass->release(b->BoxedMethod.method.this->baseInstance);
        free(base);
    }
}

struct BSG_BaseClass BSG_BaseClassSingleton__BoxedMethod = {
    .retain = &BSG_BaseMethod__BoxedMethod_retain,
    .release = &BSG_BaseMethod__BoxedMethod_release,
};

struct BSG_Instance__BoxedMethod* BSG_Constructor__BoxedMethod() {
    BSG_BaseInstance__BoxedMethod* baseInstance = malloc(sizeof(BSG_BaseInstance__BoxedMethod));
    baseInstance->refCount = 0;
    baseInstance->BoxedMethod = (BSG_Instance__BoxedMethod) {
        .baseInstance = (struct BSG_AnyBaseInstance*)baseInstance,
    };
    baseInstance->baseClass = (struct BSG_AnyBaseClass*) &BSG_BaseClassSingleton__BoxedMethod;
    return &baseInstance->BoxedMethod;
}
