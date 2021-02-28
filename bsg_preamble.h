#ifndef BSG_PREAMBLE_H
#define BSG_PREAMBLE_H

#include <stdint.h>
#include <stdlib.h>
#include <stdbool.h>
#include <stdio.h>

typedef uint8_t BSG_Char;
typedef int8_t BSG_Byte;
typedef int16_t BSG_Short;
typedef int32_t BSG_Int;
typedef int64_t BSG_Long;
typedef uint8_t BSG_UByte;
typedef uint16_t BSG_UShort;
typedef uint32_t BSG_UInt;
typedef uint64_t BSG_ULong;

typedef float BSG_Float;
typedef double BSG_Double;

typedef BSG_Byte BSG_Bool;

typedef void BSG_Void;

typedef void* BSG_Opaque;

union BSG_AnyPrimitive {
    BSG_Char CharValue;
    BSG_Byte ByteValue;
    BSG_Short ShortValue;
    BSG_Int IntValue;
    BSG_Long LongValue;
    BSG_UByte UByteValue;
    BSG_UShort UShortValue;
    BSG_UInt UIntValue;
    BSG_ULong ULongValue;

    BSG_Float FloatValue;
    BSG_Double DoubleValue;

    BSG_Bool BoolValue;

    BSG_Opaque OpaqueValue;
};

typedef long BSG_AnyType;

struct BSG_AnyBaseInstance {
    struct BSG_BaseClass* baseClass;
    int refCount;
};

struct BSG_BaseClass {
    struct BSG_AnyInstance* (*cast)(struct BSG_AnyBaseInstance*,BSG_AnyType);
    BSG_Bool (*canCast)(struct BSG_AnyBaseInstance*,BSG_AnyType);
    void (*retain)(struct BSG_AnyBaseInstance*);
    void (*release)(struct BSG_AnyBaseInstance*);
};

typedef void* BSG_AnyClass;

struct BSG_AnyInstance {
    struct BSG_AnyBaseInstance* baseInstance;
    BSG_AnyClass class;
};
typedef struct BSG_AnyInstance BSG_AnyInstance;

typedef void* BSG_AnyMethod;

struct BSG_AnyMethodFatPtr {
    BSG_AnyInstance* this;
    BSG_AnyMethod method;
};
typedef struct BSG_AnyMethodFatPtr BSG_AnyMethodFatPtr;

union BSG_Any_Content {
    BSG_AnyInstance* instance;
    BSG_AnyMethodFatPtr method;
    union BSG_AnyPrimitive primitive;
};

enum BSG_Any_ContentType {
    BSG_Any_ContentType__Instance,
    BSG_Any_ContentType__Method,
    BSG_Any_ContentType__Char,
    BSG_Any_ContentType__Byte,
    BSG_Any_ContentType__Short,
    BSG_Any_ContentType__Int,
    BSG_Any_ContentType__Long,
    BSG_Any_ContentType__UByte,
    BSG_Any_ContentType__UShort,
    BSG_Any_ContentType__UInt,
    BSG_Any_ContentType__ULong,
    BSG_Any_ContentType__Float,
    BSG_Any_ContentType__Double,
    BSG_Any_ContentType__Bool,
    BSG_Any_ContentType__Opaque,
};

struct BSG_Any {
    enum BSG_Any_ContentType type;
    union BSG_Any_Content content;
};
typedef struct BSG_Any BSG_Any;

BSG_Bool BSG_Any_Equality(BSG_Any, BSG_Any);

// Debuggable Allocator
void* allocate(size_t size);
void deallocate(void* ptr);

// Boxed Method
struct BSG_Instance__BoxedMethod {
    struct BSG_AnyBaseInstance* baseInstance;
    BSG_AnyClass class;
    BSG_AnyMethodFatPtr method;
};
typedef struct BSG_Instance__BoxedMethod BSG_Instance__BoxedMethod;

struct BSG_BaseInstance__BoxedMethod {
    struct BSG_AnyBaseClass* baseClass;
    int refCount;
    BSG_Instance__BoxedMethod BoxedMethod;
};
typedef struct BSG_BaseInstance__BoxedMethod BSG_BaseInstance__BoxedMethod;

void BSG_BaseMethod__BoxedMethod_retain(struct BSG_AnyBaseInstance* base);
void BSG_BaseMethod__BoxedMethod_release(struct BSG_AnyBaseInstance* base);

extern struct BSG_BaseClass BSG_BaseClassSingleton__BoxedMethod;

extern struct BSG_Instance__BoxedMethod* BSG_Constructor__BoxedMethod();

#endif
