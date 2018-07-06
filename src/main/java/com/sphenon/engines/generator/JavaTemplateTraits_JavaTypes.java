package com.sphenon.engines.generator;

/****************************************************************************
  Copyright 2001-2018 Sphenon GmbH

  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  License for the specific language governing permissions and limitations
  under the License.
*****************************************************************************/

import com.sphenon.basics.context.*;
import com.sphenon.basics.context.classes.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.data.*;
import com.sphenon.basics.expression.*;

public class JavaTemplateTraits_JavaTypes extends JavaTemplateTraits {

    public JavaTemplateTraits_JavaTypes (CallContext context) {
        super(context);
    }

    protected String[] getTraits(CallContext context, String trait, String template, Object... arguments) {
        return traits;
    }

    static protected String[] traits = {
        "Anchorable:.*",
        "BaseItemType:.*",
        "VectorConstructor:^Factory_Vector<(.*),(.*)>$=VectorImpl_$1_$2_.create(context)",
        "VectorConstructionViaTSMFactory:.*",
        "AppendOperation:^Factory_Vector<Pair<String,(.*)>,.*>$=vector.set(context, i, new $1(context, names[i], values[i]));",
        "AppendOperation:^Factory_Vector<.*,.*>$=vector.set(context, i, values[i]);",
        "ValueType:^Factory_Vector<Pair<String,(.*)>,.*>$=$1",
        "ValueType:^Factory_Vector<(.*),.*>$=$1",
        "VectorConstructor:^Factory_VectorImplList<(.*),(.*)>$=VectorImplList_$1_$2_.create(context)",
        "VectorConstructorList:^Factory_VectorImplList<(.*),(.*)>$=VectorImplList_$1_$2_.create(context, list)",
        "AppendOperation:^Factory_VectorImplList<Pair<String,(.*)>,.*>$=vector.set(context, i, new $1(context, names[i], values[i]));",
        "AppendOperation:^Factory_VectorImplList<.*,.*>$=vector.set(context, i, values[i]);",
        "ValueType:^Factory_VectorImplList<Pair<String,(.*)>,.*>$=$1",
        "ValueType:^Factory_VectorImplList<(.*),.*>$=$1",

        /* UML begin -------------------------------------------------------------- */
        "TargetConstruction:^ConversionManager1ToNCache<(.*),(UML.*)>$=result = ((Converter<$1,List<$2>>)(additional_arguments[0])).convert(context, source_item);",
        "SourceRetrieval:^ConversionManager1ToNCache<(.*),(UML.*)>$=CustomaryContext.create((Context)context).throwLimitation(context, \"don't know how to reconvert $2 to $1\");",
        "CacheType:^ConversionManager1ToNCache<(.*),(UML.*)>$=KeyWeakHashMap<$1,$2>",
        "CacheTypeConstruction:^ConversionManager1ToNCache<(.*),(UML.*)>$=new KeyWeakHashMap<$1,$2>()",
        "Singleton:^ConversionManager1ToNCache<(.*),(UML.*)>$",
        "TargetConstruction:^ConversionManagerCache<(.*),(UML.*)>$=result = ((Converter<$1,$2>)(additional_arguments[0])).convert(context, source_item);",
        "SourceRetrieval:^ConversionManagerCache<(.*),(UML.*)>$=CustomaryContext.create((Context)context).throwLimitation(context, \"don't know how to reconvert $2 to $1\");",
        "CacheType:^ConversionManagerCache<(.*),(UML.*)>$=KeyWeakHashMap<$1,$2>",
        "CacheTypeConstruction:^ConversionManagerCache<(.*),(UML.*)>$=new KeyWeakHashMap<$1,$2>()",
        "Singleton:^ConversionManagerCache<(.*),(UML.*)>$",
        /* UML end ---------------------------------------------------------------- */

        "TargetConstruction:^ConversionManager1ToNCache<(.*),(.*)>$=result = Factory_$2.construct(context, source_item);",
        "CacheType:^ConversionManager1ToNCache<(.*),(.*)>$=DoubleWeakHashMap<$1,List<$2>>",
        "CacheTypeConstruction:^ConversionManager1ToNCache<(.*),(.*)>$=new DoubleWeakHashMap<$1,List<$2>>()",
        "Singleton:^ConversionManager1ToNCache<(.*),(.*)>$=true",
        "TargetConstruction:^ConversionManagerCache<(.*),(.*)>$=result = Factory_$2.construct(context, source_item);",
        "SourceRetrieval:^ConversionManagerCache<(.*),(.*)>$=CustomaryContext.create((Context)context).throwLimitation(context, \"don't know how to reconvert $2 to $1\");",
        "CacheType:^ConversionManagerCache<(.*),(.*)>$=DoubleWeakHashMap<$1,$2>",
        "CacheTypeConstruction:^ConversionManagerCache<(.*),(.*)>$=new DoubleWeakHashMap<$1,$2>()",
        "Singleton:^ConversionManagerCache<(.*),(.*)>$=true",
        "ConversionManager1ToNType:^VectorAdapter1ToN<(.*),(.*),(.*)>$=ConversionManager1ToNCache<$1,$2>",
        "ConversionManagerType:^VectorAdapter<(.*),(.*),(.*)>$=ConversionManagerCache_$2_$1_",
        "FilterCompareExpression:^FilterByValue<(boolean|byte|char|short|int|long|float|double)>$=(($1)value) == (($1)object)",
        "FilterCompareExpression:^FilterByValue<(Boolean|Byte|Character|Short|Integer|Long|Float|Double)>$=(($1)value) == (($1)object)",
        "FilterCompareExpression:^FilterByValue<(.*)>$=value == object",
        "FilterCompareExpression:^FilterByValue_Optional<(.*)>$=value == object",
        "FilterType:^Factory_Filter<(String)>$=FilterByExpression_$1_",
        "FilterType:^Factory_Filter<(boolean|byte|char)>$=FilterByValue_$1_",
        "FilterType:^Factory_Filter<(short|int|long|float|double)>$=FilterByRange_$1_",
        "FilterType:^Factory_Filter<(Date|Short|Integer|Long|Float|Double)>$=FilterByRange_$1_",
        "FilterType:^Factory_Filter<(.*)>$=FilterByValue_$1_",
        "FilterOptionalType:^Factory_Filter<(String)>$=FilterByExpression_Optional_$1_",
        "FilterOptionalType:^Factory_Filter<(boolean|byte|char)>$=FilterByValue_Optional_$1_",
        "FilterOptionalType:^Factory_Filter<(short|int|long|float|double)>$=FilterByRange_Optional_$1_",
        "FilterOptionalType:^Factory_Filter<(Date|Short|Integer|Long|Float|Double)>$=FilterByRange_Optional_$1_",
        "FilterOptionalType:^Factory_Filter<(.*)>$=FilterByValue_Optional_$1_",
        "ComparisonLessEqual:^FilterByRange(?:_Optional)?<Date>$=( ! this.minimum.after(object))",
        "ComparisonGreaterEqual:^FilterByRange(?:_Optional)?<Date>$=( ! this.maximum.before(object))",
        "ComparisonLessEqual:^FilterByRange(?:_Optional)?<(Byte|Character|Short|Integer|Long|Float|Double)>$=this.minimum <= object",
        "ComparisonGreaterEqual:^FilterByRange(?:_Optional)?<(Byte|Character|Short|Integer|Long|Float|Double)>$=this.maximum >= object",
        "ComparisonLessEqual:^FilterByRange(?:_Optional)?<(Boolean)>$=this.minimum == false || object == true",
        "ComparisonGreaterEqual:^FilterByRange(?:_Optional)?<(Boolean)>$=this.maximum == true || object == false",
        "SendEventInstances:.*",
        "ObjectType:^.*\\[boolean\\]$=Boolean",
        "ObjectType:^.*\\[byte\\]$=Byte",
        "ObjectType:^.*\\[char\\]$=Character",
        "ObjectType:^.*\\[short\\]$=Short",
        "ObjectType:^.*\\[int\\]$=Integer",
        "ObjectType:^.*\\[long\\]$=Long",
        "ObjectType:^.*\\[float\\]$=Float",
        "ObjectType:^.*\\[double\\]$=Double",
        "ObjectType:^.*\\[(.*)\\]$=$1"
    };
}
