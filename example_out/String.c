// Includes
#include "String.h"

// Base Methods
struct BSG_AnyInstance* BSG_BaseMethod__String_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type) {
    struct BSG_BaseInstance__String* b = (struct BSG_BaseInstance__String*)base;
    switch(type) {
        case BSG_Type__String:
            return (struct BSG_AnyInstance*)&b->String;
        case BSG_Type__Hashable:
            return (struct BSG_AnyInstance*)&b->Hashable;
        case BSG_Type__Equatable:
            return (struct BSG_AnyInstance*)&b->Equatable;
    }
    return NULL;
}
BSG_Bool BSG_BaseMethod__String_canCast(struct BSG_AnyBaseInstance* base, BSG_AnyType type) {
    struct BSG_BaseInstance__String* b = (struct BSG_BaseInstance__String*)base;
    switch(type) {
        case BSG_Type__String:
            return true;
        case BSG_Type__Hashable:
            return true;
        case BSG_Type__Equatable:
            return true;
    }
    return false;
}
void BSG_BaseMethod__String_retain(struct BSG_AnyBaseInstance* base) {
    base->refCount++;
}
void BSG_BaseMethod__String_release(struct BSG_AnyBaseInstance* base) {
    base->refCount--;
    if(base->refCount <= 0) {
        struct BSG_BaseInstance__String* b = (struct BSG_BaseInstance__String*)base;
        base->refCount += 2;
        struct BSG_Instance__String* this = &b->String;
        this->class->deinit((BSG_AnyInstancePtr)this, NULL);
        base->refCount--;
        free(base);
    }
}

// Base Class Singleton
struct BSG_BaseClass BSG_BaseClassSingleton__String = {
    .cast = &BSG_BaseMethod__String_cast,
    .canCast = &BSG_BaseMethod__String_canCast,
    .retain = &BSG_BaseMethod__String_retain,
    .release = &BSG_BaseMethod__String_release,
};

// Methods
BSG_Void BSG_Method__String·init(BSG_AnyInstancePtr _tmp_0,BSG_Opaque data) {
    BSG_InstancePtr__String this = (struct BSG_Instance__String*)_tmp_0;
    this->cStr = "";
    BSG_Int* _tmp_1;
    _tmp_1 = &this->length;
    BSG_Int _tmp_2 = 0;
    *_tmp_1 = _tmp_2;
    BSG_Bool* _tmp_3;
    _tmp_3 = &this->isLiteral;
    BSG_Bool _tmp_4 = true;
    *_tmp_3 = _tmp_4;
    if(this) {
        this->baseInstance->baseClass->release(this->baseInstance);
    }
    return;
}
BSG_Int BSG_Method__String·hashCode(BSG_AnyInstancePtr _tmp_5,BSG_Opaque data) {
    BSG_InstancePtr__String this = (BSG_InstancePtr__String)_tmp_5->baseInstance->baseClass->cast(_tmp_5->baseInstance, BSG_Type__String);
    BSG_Int h;
    BSG_Int* _tmp_6;
    _tmp_6 = &h;
    BSG_Int _tmp_7 = 0;
    *_tmp_6 = _tmp_7;
    BSG_Int i;
    BSG_Int* _tmp_8;
    _tmp_8 = &i;
    BSG_Int _tmp_9 = 0;
    *_tmp_8 = _tmp_9;
    while(true) {
        BSG_Int _tmp_10 = this->length;
        BSG_Bool _tmp_11 = i < _tmp_10;
        if(!_tmp_11) {
            break;
        }
        BSG_Int intValue;
        intValue = (int)((char*)this->cStr)[i];
        BSG_Int* _tmp_12;
        _tmp_12 = &h;
        BSG_Int _tmp_13 = 31;
        BSG_Int _tmp_14 = _tmp_13 * h;
        BSG_Int _tmp_15 = _tmp_14 + intValue;
        *_tmp_12 = _tmp_15;
        BSG_Int* _tmp_16;
        _tmp_16 = &i;
        BSG_Int _tmp_17 = 1;
        BSG_Int _tmp_18 = i + _tmp_17;
        *_tmp_16 = _tmp_18;
    }
    if(this) {
        this->baseInstance->baseClass->release(this->baseInstance);
    }
    return h;
}
BSG_Bool BSG_Method__String·equals(BSG_AnyInstancePtr _tmp_19,BSG_Opaque data,BSG_InstancePtr__Equatable other) {
    BSG_InstancePtr__String this = (BSG_InstancePtr__String)_tmp_19->baseInstance->baseClass->cast(_tmp_19->baseInstance, BSG_Type__String);
    BSG_Bool _tmp_20 = other->baseInstance->baseClass->canCast(other->baseInstance, BSG_Type__String);
    BSG_Bool _tmp_21 = false;
    BSG_Bool _tmp_22 = _tmp_20 == _tmp_21;
    if(_tmp_22) {
        BSG_Bool _tmp_23 = false;
        if(this) {
            this->baseInstance->baseClass->release(this->baseInstance);
        }
        if(other) {
            other->baseInstance->baseClass->release(other->baseInstance);
        }
        return _tmp_23;
    }
    BSG_InstancePtr__String otherString;
    BSG_InstancePtr__String* _tmp_24;
    _tmp_24 = &otherString;
    BSG_InstancePtr__String _tmp_25 = (BSG_InstancePtr__String) other->baseInstance->baseClass->cast(other->baseInstance, BSG_Type__String);
    *_tmp_24 = _tmp_25;
    BSG_Int _tmp_26 = this->length;
    BSG_Int _tmp_27 = otherString->length;
    BSG_Bool _tmp_28 = _tmp_26 == _tmp_27;
    BSG_Bool _tmp_29 = false;
    BSG_Bool _tmp_30 = _tmp_28 == _tmp_29;
    if(_tmp_30) {
        BSG_Bool _tmp_31 = false;
        if(this) {
            this->baseInstance->baseClass->release(this->baseInstance);
        }
        if(other) {
            other->baseInstance->baseClass->release(other->baseInstance);
        }
        return _tmp_31;
    }
    BSG_Int i;
    BSG_Int* _tmp_32;
    _tmp_32 = &i;
    BSG_Int _tmp_33 = 0;
    *_tmp_32 = _tmp_33;
    BSG_Bool eq;
    BSG_Bool* _tmp_34;
    _tmp_34 = &eq;
    BSG_Bool _tmp_35 = true;
    *_tmp_34 = _tmp_35;
    while(true) {
        BSG_Int _tmp_36 = this->length;
        BSG_Bool _tmp_37 = i < _tmp_36;
        BSG_Bool _tmp_38;
        if(_tmp_37) {
            _tmp_38 = eq;
        } else {
            _tmp_38 = false;
        }
        if(!_tmp_38) {
            break;
        }
        eq = (int)((char*)this->cStr)[i] == (int)((char*)otherString->cStr)[i];
        BSG_Int* _tmp_39;
        _tmp_39 = &i;
        BSG_Int _tmp_40 = 1;
        BSG_Int _tmp_41 = i + _tmp_40;
        *_tmp_39 = _tmp_41;
    }
    if(this) {
        this->baseInstance->baseClass->release(this->baseInstance);
    }
    if(other) {
        other->baseInstance->baseClass->release(other->baseInstance);
    }
    return eq;
}
BSG_InstancePtr__String BSG_Method__String·plus(BSG_AnyInstancePtr _tmp_42,BSG_Opaque data,BSG_InstancePtr__String otherString) {
    BSG_InstancePtr__String this = (struct BSG_Instance__String*)_tmp_42;
    BSG_InstancePtr__String newString;
    BSG_InstancePtr__String* _tmp_43;
    _tmp_43 = &newString;
    BSG_InstancePtr__String _tmp_44 = BSG_Constructor__String();
    _tmp_44->baseInstance->baseClass->retain(_tmp_44->baseInstance);
    *_tmp_43 = _tmp_44;
    BSG_Int* _tmp_45 = &newString->length;
    BSG_Int _tmp_46 = this->length;
    BSG_Int _tmp_47 = otherString->length;
    BSG_Int _tmp_48 = _tmp_46 + _tmp_47;
    *_tmp_45 = _tmp_48;
    BSG_Bool* _tmp_49 = &newString->isLiteral;
    BSG_Bool _tmp_50 = false;
    *_tmp_49 = _tmp_50;
    newString->cStr = malloc((newString->length + 1) * sizeof(char));
    strcpy(newString->cStr, this->cStr);
    strcat(newString->cStr, otherString->cStr);
    if(this) {
        this->baseInstance->baseClass->release(this->baseInstance);
    }
    if(otherString) {
        otherString->baseInstance->baseClass->release(otherString->baseInstance);
    }
    return newString;
}
BSG_Void BSG_Method__String·deinit(BSG_AnyInstancePtr _tmp_51,BSG_Opaque data) {
    BSG_InstancePtr__String this = (struct BSG_Instance__String*)_tmp_51;
    BSG_Bool _tmp_52 = this->isLiteral;
    BSG_Bool _tmp_53 = false;
    BSG_Bool _tmp_54 = _tmp_52 == _tmp_53;
    if(_tmp_54) {
        free(this->cStr);
    }
    if(this) {
        this->baseInstance->baseClass->release(this->baseInstance);
    }
    return;
}

// Class Singletons
struct BSG_Class__String BSG_ClassSingleton__String_String = {
    .init = &BSG_Method__String·init,
    .plus = &BSG_Method__String·plus,
    .deinit = &BSG_Method__String·deinit,
};
struct BSG_Class__Hashable BSG_ClassSingleton__String_Hashable = {
    .hashCode = &BSG_Method__String·hashCode,
};
struct BSG_Class__Equatable BSG_ClassSingleton__String_Equatable = {
    .equals = &BSG_Method__String·equals,
};

// Constructor
struct BSG_Instance__String* BSG_Constructor__String() {
    struct BSG_BaseInstance__String* baseInstance = malloc(sizeof(struct BSG_BaseInstance__String));
    baseInstance->String = (struct BSG_Instance__String) {
        .baseInstance = (struct BSG_AnyBaseInstance*)baseInstance,
        .class = &BSG_ClassSingleton__String_String,
    };
    baseInstance->Hashable = (struct BSG_Instance__Hashable) {
        .baseInstance = (struct BSG_AnyBaseInstance*)baseInstance,
        .class = &BSG_ClassSingleton__String_Hashable,
    };
    baseInstance->Equatable = (struct BSG_Instance__Equatable) {
        .baseInstance = (struct BSG_AnyBaseInstance*)baseInstance,
        .class = &BSG_ClassSingleton__String_Equatable,
    };
    baseInstance->baseClass = (struct BSG_AnyBaseClass*) &BSG_BaseClassSingleton__String;
    baseInstance->refCount += 2;
    struct BSG_Instance__String* this = &baseInstance->String;
    this->class->init((BSG_AnyInstancePtr)this, NULL);
    baseInstance->refCount--;
    return &baseInstance->String;
}

// Singleton
