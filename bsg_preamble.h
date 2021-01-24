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

union BSG_AnyValue {
    BSG_Char char_value;
    BSG_Byte byte_value;
    BSG_Short short_value;
    BSG_Int int_value_value;
    BSG_Long long_value;
    BSG_UByte ubyte_value;
    BSG_UShort ushort_value;
    BSG_UInt uint_value;
    BSG_ULong ulong_value;

    BSG_Float float_value;
    BSG_Double double_value;

    BSG_Bool bool_value;
};

typedef long BSG_AnyType;

struct BSG_AnyBaseInstance {
    struct BSG_BaseClass* baseClass;
    int refCount;
};

struct BSG_BaseClass {
    struct BSG_AnyInstance* (*cast)(struct BSG_AnyBaseInstance*,BSG_AnyType);
    void (*retain)(struct BSG_AnyBaseInstance*);
    void (*release)(struct BSG_AnyBaseInstance*);
};

typedef void* BSG_AnyClass;

struct BSG_AnyInstance {
    struct BSG_AnyBaseInstance* baseInstance;
    BSG_AnyClass class;
};

#endif
