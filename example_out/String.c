#include "String.h"
struct BSG_AnyInstance* BSG_BaseMethod__String_cast(struct BSG_AnyBaseInstance* base, BSG_AnyType type) {
struct BSG_BaseInstance__String* b = (struct BSG_BaseInstance__String*)base;
switch(type) {
case BSG_Type__String:
return (struct BSG_AnyInstance*)&b->String;
case BSG_Type__Hashable:
return (struct BSG_AnyInstance*)&b->Hashable;
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
this->class->deinit(this);
base->refCount--;
free(base);
}
}
struct BSG_BaseClass BSG_BaseClassSingleton__String = {
.cast = &BSG_BaseMethod__String_cast,
.canCast = &BSG_BaseMethod__String_canCast,
.retain = &BSG_BaseMethod__String_retain,
.release = &BSG_BaseMethod__String_release,
};
BSG_Void BSG_Method__String_String_init(struct BSG_Instance__String* this) {
this->cStr = "";
BSG_Int* _tmp_0;
_tmp_0 = &this->length;
BSG_Int _tmp_1 = 0;
*_tmp_0 = _tmp_1;
BSG_Bool* _tmp_2;
_tmp_2 = &this->isLiteral;
BSG_Bool _tmp_3 = true;
*_tmp_2 = _tmp_3;
if(this) {
    this->baseInstance->baseClass->release(this->baseInstance);
}
return;
}
BSG_Int BSG_Method__String_Hashable_hashCode(struct BSG_Instance__Hashable* _tmp_4) {
struct BSG_Instance__String* this = (struct BSG_Instance__String*)_tmp_4->baseInstance->baseClass->cast(_tmp_4->baseInstance, BSG_Type__String);
BSG_Int h;
BSG_Int* _tmp_5;
_tmp_5 = &h;
BSG_Int _tmp_6 = 0;
*_tmp_5 = _tmp_6;
BSG_Int i;
BSG_Int* _tmp_7;
_tmp_7 = &i;
BSG_Int _tmp_8 = 0;
*_tmp_7 = _tmp_8;
while(true) {
BSG_Int _tmp_9 = this->length;
BSG_Int _tmp_10 = i < _tmp_9;
if(!_tmp_10) {
break;
}
BSG_Int intValue;
intValue = (int)((char*)this->cStr)[i];
BSG_Int* _tmp_11;
_tmp_11 = &h;
BSG_Int _tmp_12 = 31;
BSG_Int _tmp_13 = _tmp_12 * h;
BSG_Int _tmp_14 = _tmp_13 + intValue;
*_tmp_11 = _tmp_14;
BSG_Int* _tmp_15;
_tmp_15 = &i;
BSG_Int _tmp_16 = 1;
BSG_Int _tmp_17 = i + _tmp_16;
*_tmp_15 = _tmp_17;
}
if(this) {
    this->baseInstance->baseClass->release(this->baseInstance);
}
return h;
}
BSG_Bool BSG_Method__String_Hashable_equals(struct BSG_Instance__Hashable* _tmp_18,struct BSG_Instance__Hashable* other) {
struct BSG_Instance__String* this = (struct BSG_Instance__String*)_tmp_18->baseInstance->baseClass->cast(_tmp_18->baseInstance, BSG_Type__String);
BSG_Bool _tmp_19 = other->baseInstance->baseClass->canCast(other->baseInstance, BSG_Type__String);
BSG_Bool _tmp_20 = false;
BSG_Bool _tmp_21 = _tmp_19 == _tmp_20;
if(_tmp_21) {
BSG_Bool _tmp_22 = false;
if(this) {
    this->baseInstance->baseClass->release(this->baseInstance);
}
if(other) {
    other->baseInstance->baseClass->release(other->baseInstance);
}
return _tmp_22;
}
struct BSG_Instance__String* otherString;
struct BSG_Instance__String** _tmp_23;
_tmp_23 = &otherString;
struct BSG_Instance__String* _tmp_24 = (struct BSG_Instance__String*) other->baseInstance->baseClass->cast(other->baseInstance, BSG_Type__String);
*_tmp_23 = _tmp_24;
BSG_Int _tmp_25 = this->length;
BSG_Int _tmp_26 = otherString->length;
BSG_Bool _tmp_27 = _tmp_25 == _tmp_26;
BSG_Bool _tmp_28 = false;
BSG_Bool _tmp_29 = _tmp_27 == _tmp_28;
if(_tmp_29) {
BSG_Bool _tmp_30 = false;
if(this) {
    this->baseInstance->baseClass->release(this->baseInstance);
}
if(other) {
    other->baseInstance->baseClass->release(other->baseInstance);
}
return _tmp_30;
}
BSG_Int i;
BSG_Int* _tmp_31;
_tmp_31 = &i;
BSG_Int _tmp_32 = 0;
*_tmp_31 = _tmp_32;
BSG_Bool eq;
BSG_Bool* _tmp_33;
_tmp_33 = &eq;
BSG_Bool _tmp_34 = true;
*_tmp_33 = _tmp_34;
while(true) {
BSG_Int _tmp_35 = this->length;
BSG_Int _tmp_36 = i < _tmp_35;
BSG_Bool _tmp_37;
if(_tmp_36) {
_tmp_37 = eq;
} else {
_tmp_37 = false;
}
if(!_tmp_37) {
break;
}
eq = (int)((char*)this->cStr)[i] == (int)((char*)otherString->cStr)[i];
BSG_Int* _tmp_38;
_tmp_38 = &i;
BSG_Int _tmp_39 = 1;
BSG_Int _tmp_40 = i + _tmp_39;
*_tmp_38 = _tmp_40;
}
if(this) {
    this->baseInstance->baseClass->release(this->baseInstance);
}
if(other) {
    other->baseInstance->baseClass->release(other->baseInstance);
}
return eq;
}
struct BSG_Instance__String* BSG_Method__String_String_plus(struct BSG_Instance__String* this,struct BSG_Instance__String* otherString) {
struct BSG_Instance__String* newString;
struct BSG_Instance__String** _tmp_41;
_tmp_41 = &newString;
struct BSG_Instance__String* _tmp_42 = BSG_Constructor__String();
_tmp_42->baseInstance->baseClass->retain(_tmp_42->baseInstance);
*_tmp_41 = _tmp_42;
BSG_Int* _tmp_43 = &newString->length;
BSG_Int _tmp_44 = this->length;
BSG_Int _tmp_45 = otherString->length;
BSG_Int _tmp_46 = _tmp_44 + _tmp_45;
*_tmp_43 = _tmp_46;
BSG_Bool* _tmp_47 = &newString->isLiteral;
BSG_Bool _tmp_48 = false;
*_tmp_47 = _tmp_48;
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
BSG_Void BSG_Method__String_String_deinit(struct BSG_Instance__String* this) {
BSG_Bool _tmp_49 = this->isLiteral;
BSG_Bool _tmp_50 = false;
BSG_Bool _tmp_51 = _tmp_49 == _tmp_50;
if(_tmp_51) {
free(this->cStr);
}
if(this) {
    this->baseInstance->baseClass->release(this->baseInstance);
}
return;
}
struct BSG_Class__String BSG_ClassSingleton__String_String = {
.init = &BSG_Method__String_String_init,
.plus = &BSG_Method__String_String_plus,
.deinit = &BSG_Method__String_String_deinit,
};
struct BSG_Class__Hashable BSG_ClassSingleton__String_Hashable = {
.hashCode = &BSG_Method__String_Hashable_hashCode,
.equals = &BSG_Method__String_Hashable_equals,
};
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
baseInstance->baseClass = (struct BSG_AnyBaseClass*) &BSG_BaseClassSingleton__String;
baseInstance->refCount += 2;
struct BSG_Instance__String* this = &baseInstance->String;
this->class->init(this);
baseInstance->refCount--;
return &baseInstance->String;
}
