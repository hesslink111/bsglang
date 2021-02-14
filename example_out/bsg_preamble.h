#ifndef BSG_PREAMBLE_H
#define BSG_PREAMBLE_H

#include <stdint.h>
#include <stdlib.h>
#include <stdbool.h>

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
typedef struct BSG_AnyInstance* BSG_AnyInstancePtr;

typedef void* BSG_AnyMethod;

struct BSG_AnyMethodFatPtr {
    struct BSG_AnyInstance* this;
    BSG_AnyMethod method;
};

union BSG_Any_Content {
    struct BSG_AnyInstance* instance;
    struct BSG_AnyMethodFatPtr method;
    union BSG_AnyPrimitive primitive;
};

//｢a·b·c｣￫d

enum BSG_Any_ContentType {
    BSG_Any_ContentType__Instance = 0,
    BSG_Any_ContentType__Method = 1,
    BSG_Any_ContentType__Primitive = 2,
};

struct BSG_Any {
    enum BSG_Any_ContentType type;
    union BSG_Any_Content content;
};
typedef struct BSG_Any BSG_Any;

#endif
