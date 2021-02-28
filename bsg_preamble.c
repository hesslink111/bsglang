#include "bsg_preamble.h"
//#include <stdio.h>

// Debuggable Allocator
int allocations = 0;
void* allocate(size_t size) {
    allocations++;
    //printf("Total allocations++: %d\n", allocations);
    return malloc(size);
}

void deallocate(void* ptr) {
    allocations--;
    //printf("Total allocations--: %d\n", allocations);
    free(ptr);
}

// Boxed Method
void BSG_BaseMethod__BoxedMethod_retain(struct BSG_AnyBaseInstance* base) {
    base->refCount++;
}
void BSG_BaseMethod__BoxedMethod_release(struct BSG_AnyBaseInstance* base) {
    base->refCount--;
    if(base->refCount <= 0) {
        BSG_BaseInstance__BoxedMethod* b = (BSG_BaseInstance__BoxedMethod*)base;
        b->BoxedMethod.method.this->baseInstance->baseClass->release(b->BoxedMethod.method.this->baseInstance);
        deallocate(base);
    }
}

struct BSG_BaseClass BSG_BaseClassSingleton__BoxedMethod = {
    .retain = &BSG_BaseMethod__BoxedMethod_retain,
    .release = &BSG_BaseMethod__BoxedMethod_release,
};

struct BSG_Instance__BoxedMethod* BSG_Constructor__BoxedMethod() {
    BSG_BaseInstance__BoxedMethod* baseInstance = allocate(sizeof(BSG_BaseInstance__BoxedMethod));
    baseInstance->refCount = 0;
    baseInstance->BoxedMethod = (BSG_Instance__BoxedMethod) {
        .baseInstance = (struct BSG_AnyBaseInstance*)baseInstance,
    };
    baseInstance->baseClass = (struct BSG_AnyBaseClass*) &BSG_BaseClassSingleton__BoxedMethod;
    return &baseInstance->BoxedMethod;
}

// Any Equality
BSG_Bool BSG_Any_Equality(BSG_Any a, BSG_Any b) {
    if(a.type != b.type) {
        return false;
    }
    switch(a.type) {
        case BSG_Any_ContentType__Instance:
            return a.content.instance == b.content.instance;
        case BSG_Any_ContentType__Method:
            return a.content.method.this == b.content.method.this;
            return a.content.method.method == b.content.method.method;
        case BSG_Any_ContentType__Char:
            return a.content.primitive.CharValue == b.content.primitive.CharValue;
        case BSG_Any_ContentType__Byte:
            return a.content.primitive.ByteValue == b.content.primitive.ByteValue;
        case BSG_Any_ContentType__Short:
            return a.content.primitive.ShortValue == b.content.primitive.ShortValue;
        case BSG_Any_ContentType__Int:
            return a.content.primitive.IntValue == b.content.primitive.IntValue;
        case BSG_Any_ContentType__Long:
            return a.content.primitive.LongValue == b.content.primitive.LongValue;
        case BSG_Any_ContentType__UByte:
            return a.content.primitive.UByteValue == b.content.primitive.UByteValue;
        case BSG_Any_ContentType__UShort:
            return a.content.primitive.UShortValue == b.content.primitive.UShortValue;
        case BSG_Any_ContentType__UInt:
            return a.content.primitive.UIntValue == b.content.primitive.UIntValue;
        case BSG_Any_ContentType__ULong:
            return a.content.primitive.ULongValue == b.content.primitive.ULongValue;
        case BSG_Any_ContentType__Float:
            return a.content.primitive.FloatValue == b.content.primitive.FloatValue;
        case BSG_Any_ContentType__Double:
            return a.content.primitive.DoubleValue == b.content.primitive.DoubleValue;
        case BSG_Any_ContentType__Bool:
            return a.content.primitive.BoolValue == b.content.primitive.BoolValue;
        case BSG_Any_ContentType__Opaque:
            return a.content.primitive.OpaqueValue == b.content.primitive.OpaqueValue;
        default:
            printf("Could not perform cast from Any to unknown type.");
            exit(1);
    }
}
