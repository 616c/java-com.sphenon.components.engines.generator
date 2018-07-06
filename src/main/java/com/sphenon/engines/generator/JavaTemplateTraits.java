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
import com.sphenon.basics.expression.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.data.*;

import com.sphenon.engines.generator.tom.*;
import com.sphenon.engines.generator.tom.classes.*;
import com.sphenon.engines.generator.tchandler.*;
import com.sphenon.engines.generator.returncodes.*;

public class JavaTemplateTraits {

    public JavaTemplateTraits (CallContext context) {
    }

    public String get(CallContext context, String trait, String template, Object... arguments) {
        String[] traits = this.getTraits(context, trait, template, arguments);

        String texp = template.replaceFirst(".*\\.","");
        String[] tparts = texp.split("\\[",2);
        texp = tparts[0];
        if (arguments != null && arguments.length > 3) {
            for (int ai=3; ai<arguments.length; ai++) {
                texp += (ai == 3 ? "<" : ",") + arguments[ai];
            }
            texp += ">";
        }
        if (tparts.length > 1) {
            texp += "[" + tparts[1];
        }
        for (String t : traits) {
            String[] ta = t.split("=",2);
            String result = ta.length == 2 ? ta[1] : null;
            String[] tc = ta[0].split(":",2);
            RegularExpression re = new RegularExpression(context, tc[1], result);
            if (trait.equals(tc[0]) && re.matches(context, texp)) {
                result = (result == null ? null : re.replaceFirst(context, texp));
                return result;
            }
        }
        
        CustomaryContext.create((Context)context).throwPreConditionViolation(context, "No trait '%(trait)' for '%(template)'", "template", texp, "trait", trait);
        throw (ExceptionPreConditionViolation) null; // compiler insists
    }

    protected String[] getTraits(CallContext context, String trait, String template, Object... arguments) {
        CustomaryContext.create((Context)context).throwPreConditionViolation(context, "No traits provided while trying to access trait '%(trait)' of template '%(template)'", "template", template, "trait", trait);
        throw (ExceptionPreConditionViolation) null; // compiler insists
    }
}
