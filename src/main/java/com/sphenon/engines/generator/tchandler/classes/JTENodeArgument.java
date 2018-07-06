package com.sphenon.engines.generator.tchandler.classes;

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
import com.sphenon.engines.generator.tom.*;
import com.sphenon.engines.generator.tom.classes.*;
import com.sphenon.engines.generator.tchandler.*;
import com.sphenon.engines.generator.returncodes.*;

public class JTENodeArgument {

    public JTENodeArgument (CallContext context, String identifier, JTENodeArgumentList arguments) {
        this(context, identifier, arguments, null, false);
    }

    public JTENodeArgument (CallContext context, String identifier, JTENodeArgumentList arguments, String default_value) {
        this(context, identifier, arguments, default_value, false);
    }

    public JTENodeArgument (CallContext context, String identifier, JTENodeArgumentList arguments, boolean is_code) {
        this(context, identifier, arguments, null, is_code);
    }

    public JTENodeArgument (CallContext context, String identifier, JTENodeArgumentList arguments, String default_value, boolean is_code) {
        this.identifier    = identifier;
        this.arguments     = arguments;
        this.default_value = default_value;
        this.is_code       = is_code;
    }

    protected String identifier;

    public String getIdentifier (CallContext context) {
        return this.identifier;
    }

    protected JTENodeArgumentList arguments;

    public JTENodeArgumentList getArguments (CallContext context) {
        return this.arguments;
    }

    protected String default_value;

    public String getDefaultValue (CallContext context) {
        return this.default_value;
    }

    protected boolean is_code;

    public boolean isCode (CallContext context) {
        return this.is_code;
    }

    public String toExpressionString(CallContext context) {
        String expression = this.identifier;
        if (arguments != null && arguments.getArguments(context) != null && arguments.getArguments(context).size() != 0) {
            expression += "<" + arguments.toExpressionString(context) + ">";
        }
        return expression;
    }
}

