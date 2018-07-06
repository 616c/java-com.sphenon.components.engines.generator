package com.sphenon.engines.generator.classes;

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
import com.sphenon.basics.locating.*;

import com.sphenon.engines.generator.*;
import com.sphenon.engines.generator.returncodes.*;
import com.sphenon.engines.generator.tchandler.classes.*;

import java.util.Vector;

public class Class_Signature implements Signature {

    public Class_Signature (CallContext context, String signature, Template template) throws InvalidTemplateSyntax {
        if (signature == null || signature.matches(" *")) {
            this.formal_arguments = new FormalArgument[0];
        } else {
            String[] arg_defs = signature.split(" *, *");
            this.formal_arguments = new FormalArgument[arg_defs.length];

            int i=0;
            for (String arg_def : arg_defs) {
                this.formal_arguments[i++] = new Class_FormalArgument(context, arg_def, template);
            }
        }
    }

    public Class_Signature (CallContext context, Vector<TCodeArgument> signature) {
        if (signature == null || signature.size() == 0) {
            this.formal_arguments = new FormalArgument[0];
        } else {
            this.formal_arguments = new FormalArgument[signature.size()];

            int i=0;
            for (TCodeArgument arg : signature) {
                this.formal_arguments[i++] = new Class_FormalArgument(context, arg);
            }
        }
    }

    protected FormalArgument[] formal_arguments;

    public FormalArgument[] getFormalArguments (CallContext context) {
        return formal_arguments;
    }

    public boolean isEmpty(CallContext context) {
        return (formal_arguments.length == 0);
    }

    public String toString(CallContext context) {
        String result = "";
        for (FormalArgument formal_argument : formal_arguments) {
            result += (result.length() == 0 ? "" : ", ") + formal_argument.getTypeName(context) + " " + formal_argument.getArgumentName(context);
        }
        return result;
    }
}
