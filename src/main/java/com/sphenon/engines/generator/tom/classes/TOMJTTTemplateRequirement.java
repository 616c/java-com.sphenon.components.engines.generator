package com.sphenon.engines.generator.tom.classes;

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

import com.sphenon.engines.generator.*;
import com.sphenon.engines.generator.classes.*;
import com.sphenon.engines.generator.tom.*;
import com.sphenon.engines.generator.returncodes.*;
import com.sphenon.engines.generator.tchandler.classes.*;

import com.sphenon.basics.expression.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.StringWriter;
import java.io.IOException;

public class TOMJTTTemplateRequirement extends TOMTemplateRequirement {

    // Configuration ------------------------------------------------------------------

    // Construction -------------------------------------------------------------------

    public TOMJTTTemplateRequirement(CallContext context, TOMNode parent, String template_name, JTENodeArgumentList list) throws InvalidTemplateSyntax {
        super(context, parent, template_name, null);
        this.list = list;
    }

    // Attributes ---------------------------------------------------------------------

    protected JTENodeArgumentList  list;

    protected String getArguments(CallContext context) {
        String expression = "";
        TOMSkeleton skeleton = this.findSuperNode(context, TOMSkeleton.class, null);
        for (JTENodeArgument arg : this.list.getArguments(context)) {
            expression += (expression.length() == 0 ? "" : ", ") + optinallyQuote(context, arg, skeleton);
        }
        return expression;
    }

    protected String optinallyQuote(CallContext context, JTENodeArgument arg, TOMSkeleton skeleton) {
        String identifier = arg.getIdentifier(context);
        if (arg.getArguments(context) == null || arg.getArguments(context).getArguments(context) == null || arg.getArguments(context).getArguments(context).size() == 0) {
            for (FormalArgument fa : skeleton.getSignature(context).getFormalArguments(context)) {
                if (fa.getArgumentName(context).equals(identifier)) {
                    return identifier;
                }
            }
            return "\"" + identifier + "\"";
        } else {
            String result = "\"" + identifier + "\"";
            for (JTENodeArgument a : arg.getArguments(context).getArguments(context)) {
                result += " + \"_\" + " + optinallyQuote(context, a, skeleton);
            }
            result += " + \"_\"";
            return result;
        }
    }
}
