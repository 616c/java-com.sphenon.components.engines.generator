package com.sphenon.basics.data.conversion;

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
import com.sphenon.basics.message.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.encoding.*;
import com.sphenon.basics.expression.*;
import com.sphenon.basics.expression.classes.*;
import com.sphenon.basics.data.*;
import com.sphenon.basics.metadata.*;
import com.sphenon.basics.locating.*;
import com.sphenon.basics.locating.locators.*;
import com.sphenon.basics.locating.factories.*;
import com.sphenon.basics.locating.returncodes.*;
import com.sphenon.basics.validation.returncodes.*;
import com.sphenon.basics.security.*;
import com.sphenon.basics.system.*;
import com.sphenon.basics.data.*;
import com.sphenon.basics.aspects.*;

import com.sphenon.engines.generator.*;
import com.sphenon.engines.generator.classes.*;

import java.io.File;

public class DataConverter_MediaObject_Generator implements DataConverter {
    static protected long notification_level;
    static public    long adjustNotificationLevel(long new_level) { long old_level = notification_level; notification_level = new_level; return old_level; }
    static public    long getNotificationLevel() { return notification_level; }
    static { notification_level = NotificationLocationContext.getLevel(RootContext.getInitialisationContext(), "com.sphenon.basics.data.conversion.DataConverter_MediaObject_Generator"); };

    protected String               id;
    protected TypeImpl_MediaObject target_type;
    protected TypeImpl_MediaObject source_type;
    protected String               filename_substitution_regexp;
    protected DynamicString        filename_substitution_subst;

    public DataConverter_MediaObject_Generator (CallContext context, String id, TypeImpl_MediaObject source_type, TypeImpl_MediaObject target_type, String filename_substitution_regexp, String filename_substitution_subst) {
        this.id = id;
        this.target_type = target_type;
        this.source_type = source_type;
        this.filename_substitution_regexp = filename_substitution_regexp;
        this.filename_substitution_subst = new DynamicString(context, filename_substitution_subst);
    }

    public String getId (CallContext context) {
        return this.id;
    }

    public Type getSourceType (CallContext context) {
        return this.source_type;
    }

    public Type getTargetType (CallContext context) {
        return this.target_type;
    }

    public Data convert (CallContext call_context, Data source) {
        return convert (call_context, source, null);
    }

    public Data convert (CallContext call_context, Data source, java.util.Map arguments) {
        Context context = Context.create(call_context);
        CustomaryContext cc = CustomaryContext.create(context);
        if (! source.getDataType(context).isA(context, this.source_type) || ! (source.getDataType(context) instanceof TypeImpl_MediaObject)) {
            cc.throwPreConditionViolation(context, "Source of data converter is not a '%(expected)' (TypeImpl_MediaObject), but a '%(got)' ('%(gottype)')", "expected", this.source_type.getName(context), "got", source.getDataType(context).getName(context), "gottype", source.getDataType(context).getClass().getName());
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }
        if (! (source instanceof Data_MediaObject)) {
            cc.throwPreConditionViolation(context, "Source of data converter is not a 'Data_MediaObject', but a '%(got)'", "got", source.getClass().getName());
            throw (ExceptionPreConditionViolation) null; // compiler insists
        }

        Scope scope = new Class_Scope(context, null, null, arguments);
        Data_MediaObject source_data = (Data_MediaObject) source;

        if ((notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { cc.sendTrace(context, Notifier.SELF_DIAGNOSTICS, "Creating Generator DataConverter: arguments: '%(arguments)'", "arguments", arguments); }

        return new Data_MediaObject_ConversionAdapter_Generator(context, ((Data_MediaObject) source), scope, target_type, filename_substitution_regexp, this.filename_substitution_subst.get(context, scope));
    }
}
